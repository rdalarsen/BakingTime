package me.worric.bakingtime.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.worric.bakingtime.data.models.Recipe;

@Singleton
public class RecipeRepository implements Repository<Recipe> {

    private final MutableLiveData<List<Recipe>> mRecipesList;

    @Inject
    public RecipeRepository() {
        mRecipesList = new MutableLiveData<>();
    }

    @Override
    public LiveData<List<Recipe>> getDataList() {
        return mRecipesList;
    }

}
