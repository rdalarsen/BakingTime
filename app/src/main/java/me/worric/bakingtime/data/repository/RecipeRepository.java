package me.worric.bakingtime.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.worric.bakingtime.data.models.Recipe;
import me.worric.bakingtime.data.network.BakingWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class RecipeRepository implements Repository<Recipe>, Callback<List<Recipe>> {

    private final MutableLiveData<List<Recipe>> mRecipesList;
    private final BakingWebService mBakingWebService;

    @Inject
    public RecipeRepository(BakingWebService bakingWebService) {
        mRecipesList = new MutableLiveData<>();
        mBakingWebService = bakingWebService;
        loadData();
    }

    @Override
    public LiveData<List<Recipe>> getDataList() {
        return mRecipesList;
    }

    private void loadData() {
        mBakingWebService.fetchRecipes().enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        Timber.i("Call response: %s", response.toString());
        if (!response.isSuccessful()) return;

        List<Recipe> fetchedRecipes = response.body();

        if (fetchedRecipes != null) mRecipesList.setValue(fetchedRecipes);
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        Timber.e(t, "Call failed!");
    }
}
