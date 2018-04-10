package me.worric.bakingtime.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;

@ActivityScope
public class BakingViewModel extends ViewModel {

    private final MutableLiveData<Long> mRecipeId;
    private final MutableLiveData<Step> mChosenStep;
    private final Repository<RecipeView> mRecipeRepository;
    private final MutableLiveData<Integer> mStepPosition;

    @Inject
    public BakingViewModel(Repository<RecipeView> recipeRepository) {
        mRecipeId = new MutableLiveData<>();
        mChosenStep = new MutableLiveData<>();
        mRecipeRepository = recipeRepository;
        mStepPosition = new MutableLiveData<>();
    }

    public LiveData<List<RecipeView>> getRecipes() {
        return mRecipeRepository.findAll();
    }

    public LiveData<RecipeView> getChosenRecipe() {
        return Transformations.switchMap(mRecipeId,
                recipeId -> mRecipeRepository.findOneById(recipeId));
    }

    public LiveData<Integer> getStepPosition() {
        return mStepPosition;
    }

    public void setStepPosition(int stepPosition) {
        mStepPosition.setValue(stepPosition);
    }

    public void setChosenRecipe(Long recipeId) {
        mRecipeId.setValue(recipeId);
    }

    public void setChosenRecipe(RecipeView recipeView) {
        setChosenRecipe(recipeView.mRecipe.getId());
    }

    public void setChosenStep(Step step) {
        mChosenStep.setValue(step);
    }

    public LiveData<Step> getChosenStep() {
        return mChosenStep;
    }
}
