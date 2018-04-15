package me.worric.bakingtime.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.worric.bakingtime.AppExecutors;
import me.worric.bakingtime.data.db.AppDatabase;
import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.models.Ingredient;
import me.worric.bakingtime.data.models.Recipe;
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.data.network.BakingWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class RecipeRepository implements Repository<RecipeView>, Callback<List<Recipe>> {

    private final MutableLiveData<List<RecipeView>> mRecipesList;
    private final BakingWebService mBakingWebService;
    private final AppDatabase mAppDatabase;
    private final AppExecutors mExecutors;

    @Inject
    public RecipeRepository(BakingWebService bakingWebService, AppDatabase appDatabase,
                            AppExecutors appExecutors) {
        mRecipesList = new MutableLiveData<>();
        mBakingWebService = bakingWebService;
        mAppDatabase = appDatabase;
        mExecutors = appExecutors;
        loadData();
    }

    @Override
    public LiveData<List<RecipeView>> findAll() {
        return mAppDatabase.recipeViewDao().findAll();
    }

    @Override
    public LiveData<RecipeView> findOneById(Long id) {
        return mAppDatabase.recipeViewDao().findOneById(id);
    }

    @Override
    public RecipeView findOneByIdSync(Long id) {
        return mAppDatabase.recipeViewDao().findOneByIdSync(id);
    }

    private void loadData() {
        mExecutors.diskIO().execute(() -> {
            if (mAppDatabase.recipeDao().findAll().isEmpty()) {
                Timber.d("fecthing data...");
                mExecutors.mainThread().execute(() ->
                        mBakingWebService.fetchRecipes().enqueue(this));
            } else {
                Timber.d("NOT fetching data; database has entries.");
            }
        });
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        Timber.d("Data fetched! Call response: %s", response.toString());
        if (!response.isSuccessful()) return;

        final List<Recipe> fetchedRecipes = response.body();

        if (fetchedRecipes != null) {
            mExecutors.diskIO().execute(() -> addIdAndPersistEntities(fetchedRecipes));
        }
    }

    private void addIdAndPersistEntities(final List<Recipe> fetchedRecipes) {

        try {
            mAppDatabase.beginTransaction();

            Timber.d("handling sublists...");

            for (Recipe recipe : fetchedRecipes) {
                Long recipeId = recipe.getId();

                Timber.d("Setting ID and persisting ingredients...");
                List<Ingredient> ingredients = recipe.getIngredients();
                Ingredient.setAllRecipeIds(ingredients, recipeId);
                mAppDatabase.ingredientDao().insertAll(ingredients);

                Timber.d("Setting ID and persisting steps...");
                List<Step> steps = recipe.getSteps();
                Step.setAllRecipeIds(steps, recipeId);
                mAppDatabase.stepDao().insertAll(steps);
            }

            Timber.d("Persisting recipes");
            mAppDatabase.recipeDao().insertAll(fetchedRecipes);

            mAppDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Timber.e(e, "There was an error persisting entities");
        } finally {
            mAppDatabase.endTransaction();
        }

    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        Timber.e(t, "Call failed!");
    }
}
