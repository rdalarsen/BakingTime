package me.worric.bakingtime.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import me.worric.bakingtime.data.models.Step;

@Dao
public interface StepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Step> steps);

    @Query("SELECT * FROM steps WHERE recipe_id = :recipeId AND step_id = :stepId")
    LiveData<Step> findOneById(long recipeId, long stepId);

}
