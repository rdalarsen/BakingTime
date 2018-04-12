package me.worric.bakingtime.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.data.models.StepAndSteps;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;
import timber.log.Timber;

@ActivityScope
public class BakingViewModel extends ViewModel {

    private final MutableLiveData<Long> mRecipeId;
    private final MutableLiveData<Step> mChosenStep;
    private final Repository<RecipeView> mRecipeRepository;
    private final MediatorLiveData<RecipeView> mActiveRecipeMediator;

    @Inject
    public BakingViewModel(Repository<RecipeView> recipeRepository) {
        mRecipeId = new MutableLiveData<>();
        mChosenStep = new MutableLiveData<>();
        mRecipeRepository = recipeRepository;
        mActiveRecipeMediator = new MediatorLiveData<>();
        mActiveRecipeMediator.addSource(
                Transformations.switchMap(mRecipeId, mRecipeRepository::findOneById),
                recipeView -> {
                    Timber.d("Observation updated. Id of activeRecipe: %d", recipeView.mRecipe.getId());
                    mActiveRecipeMediator.setValue(recipeView);
                }
        );
    }

    public LiveData<List<RecipeView>> getRecipes() {
        return mRecipeRepository.findAll();
    }

    public LiveData<RecipeView> getChosenRecipe() {
        return mActiveRecipeMediator;
    }

    public void setChosenRecipe(Long recipeId) {
        if (mRecipeId.getValue() == null) mRecipeId.setValue(recipeId);
    }

    public void goToNextStep() {
        RecipeView recipeView = mActiveRecipeMediator.getValue();
        Step chosenStep = mChosenStep.getValue();
        if (recipeView != null && recipeView.mSteps != null && chosenStep != null) {
            int index = recipeView.mSteps.indexOf(chosenStep);
            if (index < (recipeView.mSteps.size() - 1)) {
                mChosenStep.setValue(recipeView.mSteps.get(index + 1));
            }
        }
    }

    public void goToPreviousStep() {
        RecipeView recipeView = mActiveRecipeMediator.getValue();
        Step chosenStep = mChosenStep.getValue();
        if (recipeView != null && recipeView.mSteps != null && chosenStep != null) {
            int index = recipeView.mSteps.indexOf(chosenStep);
            if (index > 0) {
                mChosenStep.setValue(recipeView.mSteps.get(index - 1));
            }
        }
    }

    public void setChosenStep(Step step) {
        mChosenStep.setValue(step);
    }

    public LiveData<Step> getChosenStep() {
        return mChosenStep;
    }

    public LiveData<StepAndSteps> getStepAndSteps() {
        return Transformations.map(
                mActiveRecipeMediator, recipeView ->
                        StepAndSteps.newInstance(mChosenStep.getValue(), recipeView.mSteps));
    }

    public LiveData<StepAndSteps> getMoreSteps() {
        return Transformations.switchMap(mChosenStep, (Step step) ->
                Transformations.map(mActiveRecipeMediator, (RecipeView recepe) ->
                        StepAndSteps.newInstance(step, recepe.mSteps)));
    }
}
