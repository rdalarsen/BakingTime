
package me.worric.bakingtime.data.models;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Ingredient {

    @SerializedName("ingredient")
    private String mIngredient;
    @SerializedName("measure")
    private String mMeasure;
    @SerializedName("quantity")
    private Double mQuantity;

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

}
