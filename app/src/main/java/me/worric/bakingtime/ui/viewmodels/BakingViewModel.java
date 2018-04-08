package me.worric.bakingtime.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.Arrays;
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
        // TODO dummy return value
        return Transformations.map(mChosenRecipe, Arrays::asList);
    }

    public void setChosenRecipe(Recipe recipe) {
        mChosenRecipe.setValue(recipe);
    }
}
