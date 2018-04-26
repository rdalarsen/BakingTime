package me.worric.bakingtime.ui.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.worric.bakingtime.R;
import me.worric.bakingtime.data.models.Ingredient;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    private List<Ingredient> mIngredients;

    public IngredientsAdapter() {}

    public void swapIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new IngredientsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        final Ingredient ingredient = mIngredients.get(position);
        Context context = holder.itemView.getContext();

        String ingredientString = context.getString(R.string.widget_ingredient_format_string, ingredient.getIngredient(), Double.toString(ingredient.getQuantity()), ingredient.getMeasure());
        holder.mIngredients.setText(ingredientString);
    }

    @Override
    public int getItemCount() {
        return mIngredients != null ? mIngredients.size() : 0;
    }

    static class IngredientsViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        TextView mIngredients;

        IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
