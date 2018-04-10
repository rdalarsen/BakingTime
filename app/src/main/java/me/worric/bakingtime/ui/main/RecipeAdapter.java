package me.worric.bakingtime.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.worric.bakingtime.R;
import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.ui.GlideApp;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    private List<RecipeView> mRecipes;
    private RecipeClickListener mListener;

    public RecipeAdapter(@NonNull RecipeClickListener listener) {
        mListener = listener;
    }

    public void swapData(List<RecipeView> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_recipe, parent, false);
        return new RecipeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        RecipeView recipeView = mRecipes.get(position);
        Context context = holder.itemView.getContext();

        long numServings = recipeView.mRecipe.getServings();
        String servingsString = context.getResources()
                .getQuantityString(R.plurals.list_item_servings, (int) numServings, numServings);
        holder.title.setText(recipeView.mRecipe.getName());
        holder.servings.setText(servingsString);
        GlideApp.with(context)
                .load(recipeView.mRecipe.getImage())
                .fitCenter()
                .into(holder.header);
        holder.itemView.setOnClickListener(v -> mListener.onRecipeClicked(recipeView));
    }

    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.size() : 0;
    }

    static class RecipeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_recipe_header)
        ImageView header;
        @BindView(R.id.list_item_recipe_title)
        TextView title;
        @BindView(R.id.list_item_servings)
        TextView servings;

        RecipeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface RecipeClickListener {
        void onRecipeClicked(RecipeView recipeView);
    }

}
