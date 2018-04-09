
package me.worric.bakingtime.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Entity(
        tableName = "recipes"
        /*foreignKeys = {
                @ForeignKey(
                        entity = Step.class,
                        parentColumns = "id",
                        childColumns = "recipe_id"
                ),
                @ForeignKey(
                        entity = Ingredient.class,
                        parentColumns = "id",
                        childColumns = "recipe_id"
                )
        }*/
)
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Recipe {

    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Long mId;

    @SerializedName("image")
    @ColumnInfo(name = "image")
    private String mImage;

    @SerializedName("ingredients")
    @Ignore
    private List<Ingredient> mIngredients;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String mName;

    @SerializedName("servings")
    @ColumnInfo(name = "servings")
    private Long mServings;

    @SerializedName("steps")
    @Ignore
    private List<Step> mSteps;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getServings() {
        return mServings;
    }

    public void setServings(Long servings) {
        mServings = servings;
    }

    public List<Step> getSteps() {
        return mSteps;
    }

    public void setSteps(List<Step> steps) {
        mSteps = steps;
    }

}
