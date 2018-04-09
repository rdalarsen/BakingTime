package me.worric.bakingtime.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import me.worric.bakingtime.data.db.dao.IngredientDao;
import me.worric.bakingtime.data.db.dao.RecipeDao;
import me.worric.bakingtime.data.db.dao.RecipeViewDao;
import me.worric.bakingtime.data.db.dao.StepDao;
import me.worric.bakingtime.data.models.Ingredient;
import me.worric.bakingtime.data.models.Recipe;
import me.worric.bakingtime.data.models.Step;

@Database(entities = {Recipe.class, Step.class, Ingredient.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RecipeDao recipeDao();

    public abstract IngredientDao ingredientDao();

    public abstract StepDao stepDao();

    public abstract RecipeViewDao recipeViewDao();

}
