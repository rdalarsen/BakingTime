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
import me.worric.bakingtime.data.models.StepDetails;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;
import timber.log.Timber;

@ActivityScope
public class BakingViewModel extends ViewModel {

    public static final int NEXT = 0;
    public static final int PREVIOUS = 1;

    private final MutableLiveData<Long> mRecipeId = new MutableLiveData<>();
    private final MediatorLiveData<RecipeView> mChosenRecipe = new MediatorLiveData<>();

    private final MutableLiveData<Step> mStep = new MutableLiveData<>();
    private final MutableLiveData<Long> mStepId = new MutableLiveData<>();
    private final MediatorLiveData<Step> mChosenStep = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> mStepButtonClicked = new MutableLiveData<>();

    private final Repository mRecipeRepository;

    @Inject
    public BakingViewModel(Repository recipeRepository) {
        mRecipeRepository = recipeRepository;
        mChosenRecipe.addSource(
                Transformations.switchMap(mRecipeId, mRecipeRepository::findRecipeById),
                mChosenRecipe::setValue);
        mChosenStep.addSource(
                Transformations.switchMap(mStepId, stepId ->
                        mRecipeRepository.findStepById(mRecipeId.getValue(), stepId)),
                mChosenStep::setValue);
        mChosenStep.addSource(
                mStep,
                mChosenStep::setValue);
    }

    public LiveData<List<RecipeView>> getAllRecipes() {
        return mRecipeRepository.findAllRecipes();
    }

    public LiveData<RecipeView> getChosenRecipe() {
        return mChosenRecipe;
    }

    public void setChosenRecipe(Long recipeId) {
        if (mRecipeId.getValue() == null) {
            mRecipeId.setValue(recipeId);
        }
    }

    public void setStep(Step step) {
        if (!step.equals(mChosenStep.getValue())){
            mStep.setValue(step);
        } else {
            Timber.e("Steps are identical; not updating");
        }
    }

    public void setStep(long stepId) {
        Timber.i("Finding step by ID...");
        mStepId.setValue(stepId);
    }

    public LiveData<Step> getStep() {
        return mChosenStep;
    }

    public void setStepButtonClicked(boolean stepButtonClicked) {
        mStepButtonClicked.setValue(stepButtonClicked);
    }

    public LiveData<Boolean> getStepButtonClicked() {
        return mStepButtonClicked;
    }

    public LiveData<StepDetails> getStepDetails() {
        return Transformations.switchMap(mChosenStep, (Step step) ->
                Transformations.map(mChosenRecipe, (RecipeView recipe) -> {
                    int indexOf = recipe.mSteps.indexOf(step);
                    int numSteps = recipe.mSteps.size();
                    return StepDetails.newInstance(indexOf, numSteps, step.getVideoURL(),
                            step.getDescription());
                }));
    }

    public void goToStep(int nextOrPrevious) {
        RecipeView recipeView = mChosenRecipe.getValue();
        Step chosenStep = mChosenStep.getValue();

        if (recipeView != null && recipeView.mSteps != null && chosenStep != null) {
            int index = recipeView.getIndexOfStep(chosenStep);
            int maxStepIndex = recipeView.mSteps.size() - 1;

            if (nextOrPrevious == NEXT && index < maxStepIndex) {
                mStep.setValue(recipeView.mSteps.get(index + 1));
            } else if (nextOrPrevious == PREVIOUS && index > 0) {
                mStep.setValue(recipeView.mSteps.get(index - 1));
            }
        }
    }
}
