package me.worric.bakingtime.ui.main;

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
import me.worric.bakingtime.data.db.models.RecipeView;

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
        //String titleText = "Position: " + position + ", holderHash: " + holder.hashCode();
        RecipeView recipeView = mRecipes.get(position);
        holder.title.setText(recipeView.mRecipe.getName());
        holder.itemView.setOnClickListener(v -> mListener.onRecipeClicked(recipeView));
    }

    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.size() : 0;
    }

    static class RecipeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_recipe_title)
        TextView title;

        RecipeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface RecipeClickListener {
        void onRecipeClicked(RecipeView recipeView);
    }

}
