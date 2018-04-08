package me.worric.bakingtime.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import me.worric.bakingtime.data.models.Recipe;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;

@ActivityScope
public class BakingViewModel extends ViewModel {

    private final MutableLiveData<Recipe> mChosenRecipe;
    private final Repository<Recipe> mRecipeRepository;

    @Inject
    public BakingViewModel(Repository<Recipe> recipeRepository) {
        mChosenRecipe = new MutableLiveData<>();
        mRecipeRepository = recipeRepository;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeRepository.getDataList();
    }

    public void setChosenRecipe(Recipe recipe) {
        mChosenRecipe.setValue(recipe);
    }
}
