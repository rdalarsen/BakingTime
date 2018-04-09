
package me.worric.bakingtime.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Entity(tableName = "ingredients")
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    private Long mLocalId;

    @ColumnInfo(name = "recipe_id")
    private Long mRecipeId;

    @SerializedName("ingredient")
    @ColumnInfo(name = "ingredients")
    private String mIngredient;

    @SerializedName("measure")
    @ColumnInfo(name = "measure")
    private String mMeasure;

    @SerializedName("quantity")
    @ColumnInfo(name = "quantity")
    private Double mQuantity;

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

    public String getIngredient() {
        return mIngredient;
    }

    public void setIngredient(String ingredient) {
        mIngredient = ingredient;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public void setMeasure(String measure) {
        mMeasure = measure;
    }

    public Double getQuantity() {
        return mQuantity;
    }

    public void setQuantity(Double quantity) {
        mQuantity = quantity;
    }

    public static void setAllRecipeIds(List<Ingredient> ingredients, Long recipeId) {
        for (Ingredient ingredient : ingredients) {
            ingredient.setRecipeId(recipeId);
        }
    }

}
