package me.worric.bakingtime.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;

@ActivityScope
public class BakingViewModel extends ViewModel {

    private final MutableLiveData<Long> mRecipeId;
    private final Repository<RecipeView> mRecipeRepository;

    @Inject
    public BakingViewModel(Repository<RecipeView> recipeRepository) {
        mRecipeId = new MutableLiveData<>();
        mRecipeRepository = recipeRepository;
    }

    public LiveData<List<RecipeView>> getRecipes() {
        return mRecipeRepository.findAll();
    }

    public LiveData<RecipeView> getChosenRecipe() {
        return Transformations.switchMap(mRecipeId,
                recipeId -> mRecipeRepository.findOneById(recipeId));
    }

    public void setChosenRecipe(Long recipeId) {
        mRecipeId.setValue(recipeId);
    }

    public void setChosenRecipe(RecipeView recipeView) {
        setChosenRecipe(recipeView.mRecipe.getId());
    }

}
