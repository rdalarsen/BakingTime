package me.worric.bakingtime.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import me.worric.bakingtime.data.models.Recipe;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes")
    List<Recipe> loadAllRecipes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Recipe> recipes);

}
