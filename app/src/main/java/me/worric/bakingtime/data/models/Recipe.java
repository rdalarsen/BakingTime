
package me.worric.bakingtime.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Recipe {

    @SerializedName("id")
    private Long mId;
    @SerializedName("image")
    private String mImage;
    @SerializedName("ingredients")
    private List<Ingredient> mIngredients;
    @SerializedName("name")
    private String mName;
    @SerializedName("servings")
    private Long mServings;
    @SerializedName("steps")
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
