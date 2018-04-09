
package me.worric.bakingtime.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Entity(tableName = "steps")
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Step {

    @PrimaryKey(autoGenerate = true)
    private Long mLocalId;

    @ColumnInfo(name = "recipe_id")
    private Long mRecipeId;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String mDescription;

    @SerializedName("id")
    @ColumnInfo(name = "step_id")
    private Long mId;

    @SerializedName("shortDescription")
    @ColumnInfo(name = "short_description")
    private String mShortDescription;

    @SerializedName("thumbnailURL")
    @ColumnInfo(name = "thumb_url")
    private String mThumbnailURL;

    @SerializedName("videoURL")
    @ColumnInfo(name = "video_url")
    private String mVideoURL;

    public Long getLocalId() {
        return mLocalId;
    }

    public void setLocalId(Long localId) {
        mLocalId = localId;
    }

    public void setRecipeId(Long recipeId) {
        mRecipeId = recipeId;
    }

    public Long getRecipeId() {
        return mRecipeId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        mShortDescription = shortDescription;
    }

    public String getThumbnailURL() {
        return mThumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        mThumbnailURL = thumbnailURL;
    }

    public String getVideoURL() {
        return mVideoURL;
    }

    public void setVideoURL(String videoURL) {
        mVideoURL = videoURL;
    }

    public static void setAllRecipeIds(List<Step> steps, Long recipeId) {
        for (Step step : steps) {
            step.setRecipeId(recipeId);
        }
    }

}
