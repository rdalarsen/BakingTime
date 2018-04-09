package me.worric.bakingtime.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import me.worric.bakingtime.data.models.Ingredient;

@Dao
public interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Ingredient> ingredients);

    @Query("SELECT * FROM ingredients")
    List<Ingredient> findAll();

}
