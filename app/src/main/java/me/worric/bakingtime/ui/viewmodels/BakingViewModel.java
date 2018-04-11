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
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.ActivityScope;
import timber.log.Timber;

@ActivityScope
public class BakingViewModel extends ViewModel {

    private final MutableLiveData<Long> mRecipeId;
    private final MutableLiveData<Step> mChosenStep;
    private final Repository<RecipeView> mRecipeRepository;

    // private final LiveData<RecipeView> mActiveRecipe;
    private final MediatorLiveData<RecipeView> mActiveRecipeMediator;

    @Inject
    public BakingViewModel(Repository<RecipeView> recipeRepository) {
        mRecipeId = new MutableLiveData<>();
        mChosenStep = new MutableLiveData<>();
        mRecipeRepository = recipeRepository;
        mActiveRecipeMediator = new MediatorLiveData<>();
        mActiveRecipeMediator.addSource(
                Transformations.switchMap(mRecipeId,
                        recipeId -> mRecipeRepository.findOneById(recipeId)),
                recipeView -> {
                    Timber.d("Observation updated. Id of activeRecipe: %d", recipeView.mRecipe.getId());
                    mActiveRecipeMediator.setValue(recipeView);
                }
        );
        /*mActiveRecipe = Transformations.switchMap(mRecipeId,
                recipeId -> mRecipeRepository.findOneById(recipeId));
        mActiveRecipe.observeForever(recipeView -> {
            Timber.d("Observation updated. Id of activeRecipe: %d", recipeView.mRecipe.getId());
        });*/
    }

    public LiveData<List<RecipeView>> getRecipes() {
        return mRecipeRepository.findAll();
    }

    public LiveData<RecipeView> getChosenRecipe() {
        return mActiveRecipeMediator;
                //Transformations.switchMap(mRecipeId,
                //recipeId -> mRecipeRepository.findOneById(recipeId));
    }

    public void setChosenRecipe(Long recipeId) {
        mRecipeId.setValue(recipeId);
    }

    public void setChosenRecipe(RecipeView recipeView) {
        setChosenRecipe(recipeView.mRecipe.getId());
    }

    public void getNext() {
        RecipeView recipeView = mActiveRecipeMediator.getValue();
        Step chosenStep = mChosenStep.getValue();
        if (recipeView != null && recipeView.mSteps != null && chosenStep != null) {
            int index = recipeView.mSteps.indexOf(chosenStep);
            if (index < (recipeView.mSteps.size() - 1)) {
                mChosenStep.setValue(recipeView.mSteps.get(index + 1));
            }
        }
    }

    public void setChosenStep(Step step) {
        mChosenStep.setValue(step);
    }

    public LiveData<Step> getChosenStep() {
        return mChosenStep;
    }
}
