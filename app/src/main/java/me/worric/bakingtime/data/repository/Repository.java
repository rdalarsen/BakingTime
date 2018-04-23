package me.worric.bakingtime.data.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.models.Step;

public interface Repository {

    LiveData<List<RecipeView>> findAllRecipes();

    LiveData<RecipeView> findRecipeById(Long id);

    RecipeView findRecipeByIdSync(Long id);

    LiveData<Step> findStepById(Long recipeId, Long stepId);

}
