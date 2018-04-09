package me.worric.bakingtime.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;

@ActivityScope
public class BakingViewModel extends ViewModel {

    private final MutableLiveData<RecipeView> mChosenRecipe;
    private final Repository<RecipeView> mRecipeRepository;

    @Inject
    public BakingViewModel(Repository<RecipeView> recipeRepository) {
        mChosenRecipe = new MutableLiveData<>();
        mRecipeRepository = recipeRepository;
    }

    public LiveData<List<RecipeView>> getRecipes() {
        return mRecipeRepository.getDataList();
    }

    public void setChosenRecipe(RecipeView recipeView) {
        mChosenRecipe.setValue(recipeView);
    }
}
