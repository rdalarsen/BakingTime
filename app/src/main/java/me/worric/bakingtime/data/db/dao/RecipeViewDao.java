package me.worric.bakingtime.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import me.worric.bakingtime.data.db.models.RecipeView;

@Dao
public interface RecipeViewDao {

    @Transaction
    @Query("SELECT * FROM recipes")
    LiveData<List<RecipeView>> findAll();

    @Transaction
    @Query("SELECT * FROM recipes WHERE id IS :id")
    LiveData<RecipeView> findOneById(Long id);

}
