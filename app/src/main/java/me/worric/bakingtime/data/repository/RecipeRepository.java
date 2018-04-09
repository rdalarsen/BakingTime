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
    public LiveData<List<RecipeView>> getDataList() {
        return mAppDatabase.recipeViewDao().findAll();
    }

    private void loadData() {
        mExecutors.diskIO().execute(() -> {
            if (mAppDatabase.recipeDao().loadAllRecipes().isEmpty()) {
                Timber.d("fecthing data...");
                mExecutors.mainThread().execute(() -> {
                    mBakingWebService.fetchRecipes().enqueue(this);
                });
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
            mExecutors.diskIO().execute(() -> persistEntities(fetchedRecipes));
        }
    }

    private void persistEntities(final List<Recipe> fetchedRecipes) {
        setIdOnEntities(fetchedRecipes);
        Timber.d("persisting recipes...");
        mAppDatabase.recipeDao().insert(fetchedRecipes);
    }

    private void setIdOnEntities(final List<Recipe> fetchedRecipes) {
        Timber.d("Setting IDs...");
        for (Recipe recipe : fetchedRecipes) {
            Long recipeId = recipe.getId();

            List<Ingredient> ingredients = recipe.getIngredients();
            Ingredient.setAllRecipeIds(ingredients, recipeId);
            Timber.d("persisting ingredients...");
            mAppDatabase.ingredientDao().insertAll(ingredients);

            List<Step> steps = recipe.getSteps();
            Step.setAllRecipeIds(steps, recipeId);
            Timber.d("persisting steps...");
            mAppDatabase.stepDao().insertAll(steps);
        }
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        Timber.e(t, "Call failed!");
    }
}
