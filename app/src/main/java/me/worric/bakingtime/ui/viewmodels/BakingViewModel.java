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
import me.worric.bakingtime.data.models.StepWithSteps;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;
import timber.log.Timber;

@ActivityScope
public class BakingViewModel extends ViewModel {

    private static final int NEXT = 0;
    private static final int PREVIOUS = 1;

    private final MutableLiveData<Long> mRecipeId;
    private final MutableLiveData<Step> mChosenStep;
    private final MutableLiveData<Boolean> mIsClicked;
    private final MediatorLiveData<RecipeView> mChosenRecipe;
    private final Repository<RecipeView> mRecipeRepository;

    @Inject
    public BakingViewModel(Repository<RecipeView> recipeRepository) {
        mRecipeId = new MutableLiveData<>();
        mChosenStep = new MutableLiveData<>();
        mIsClicked = new MutableLiveData<>();
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

    public void setIsClicked(boolean isClicked) {
        mIsClicked.setValue(isClicked);
    }

    public LiveData<Boolean> getIsClicked() {
        return mIsClicked;
    }

    public LiveData<StepWithSteps> getChosenStepWithSteps() {
        return Transformations.switchMap(mChosenStep, (Step step) ->
                Transformations.map(mChosenRecipe, (RecipeView recepe) ->
                        StepWithSteps.newInstance(step, recepe.mSteps)));
    }

    public void goToNextStep() {
        goToNextOrPrevious(NEXT);
    }

    public void goToPreviousStep() {
        goToNextOrPrevious(PREVIOUS);
    }

    private void goToNextOrPrevious(int nextOrPrevious) {
        RecipeView recipeView = mChosenRecipe.getValue();
        Step chosenStep = mChosenStep.getValue();

        if (recipeView != null && recipeView.mSteps != null && chosenStep != null) {
            int index = recipeView.getIndexOfStep(chosenStep);

            if (nextOrPrevious == NEXT && index < (recipeView.mSteps.size() - 1)) {
                mChosenStep.setValue(recipeView.mSteps.get(index + 1));
            } else if (nextOrPrevious == PREVIOUS && index > 0) {
                mChosenStep.setValue(recipeView.mSteps.get(index - 1));
            }
        }
    }
}
