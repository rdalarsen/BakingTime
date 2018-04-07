package me.worric.bakingtime.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.worric.bakingtime.R;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_recipe, parent, false);
        return new RecipeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        String titleText = "Position: " + position + ", holderHash: " + holder.hashCode();
        holder.title.setText(titleText);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class RecipeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_recipe_title)
        TextView title;

        RecipeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
