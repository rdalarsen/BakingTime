package me.worric.bakingtime.data.db.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

import me.worric.bakingtime.data.models.Ingredient;
import me.worric.bakingtime.data.models.Recipe;
import me.worric.bakingtime.data.models.Step;

public class RecipeView {

    @Embedded
    public Recipe mRecipe;

    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    public List<Step> mSteps;

    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    public List<Ingredient> mIngredients;

}
