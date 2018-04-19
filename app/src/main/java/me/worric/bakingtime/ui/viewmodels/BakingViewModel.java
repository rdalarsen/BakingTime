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

    private static final int NEXT = 0;
    private static final int PREVIOUS = 1;

    private final MutableLiveData<Long> mRecipeId;
    private final MutableLiveData<Step> mChosenStep;
    private final MutableLiveData<Boolean> mStepButtonClicked;
    private final MediatorLiveData<RecipeView> mChosenRecipe;
    private final Repository<RecipeView> mRecipeRepository;

    @Inject
    public BakingViewModel(Repository<RecipeView> recipeRepository) {
        mRecipeId = new MutableLiveData<>();
        mChosenStep = new MutableLiveData<>();
        mStepButtonClicked = new MutableLiveData<>();
        mRecipeRepository = recipeRepository;
        Timber.e("Repository hashCode: %d", mRecipeRepository.hashCode());
        mChosenRecipe = new MediatorLiveData<>();
        mChosenRecipe.addSource(
                Transformations.switchMap(mRecipeId, mRecipeRepository::findOneById),
                mChosenRecipe::setValue);
    }

    public LiveData<List<RecipeView>> getAllRecipes() {
        return mRecipeRepository.findAll();
    }

    public LiveData<RecipeView> getChosenRecipe() {
        return mChosenRecipe;
    }

    public void setChosenRecipe(Long recipeId) {
        if (mRecipeId.getValue() == null) {
            mRecipeId.setValue(recipeId);
        }
    }

    public void setChosenStep(Step step) {
        if (!step.equals(mChosenStep.getValue())){
            mChosenStep.setValue(step);
        } else {
            Timber.e("Steps are identical; not updating");
        }
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

    public void goToNextStep(StepDetails stepDetails) {
        goToNextOrPrevious(NEXT, stepDetails);
    }

    public void goToPreviousStep(StepDetails stepDetails) {
        goToNextOrPrevious(PREVIOUS, stepDetails);
    }

    private void goToNextOrPrevious(int nextOrPrevious, StepDetails stepDetails) {
        RecipeView recipeView = mChosenRecipe.getValue();
        Step chosenStep = mChosenStep.getValue();

        // Account for when app has been killed in the background in phone mode w/ detailsfragment active
        if (chosenStep == null) {
            chosenStep = recipeView.mSteps.get(stepDetails.stepIndex);
        }

        if (recipeView != null && recipeView.mSteps != null) {
            int index = recipeView.getIndexOfStep(chosenStep);

            if (nextOrPrevious == NEXT && index < (recipeView.mSteps.size() - 1)) {
                mChosenStep.setValue(recipeView.mSteps.get(index + 1));
            } else if (nextOrPrevious == PREVIOUS && index > 0) {
                mChosenStep.setValue(recipeView.mSteps.get(index - 1));
            }
        }
    }
}
