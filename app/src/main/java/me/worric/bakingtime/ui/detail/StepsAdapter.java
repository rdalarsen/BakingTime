package me.worric.bakingtime.ui.detail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.ui.GlideApp;
import timber.log.Timber;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private List<Step> mSteps;

    public StepsAdapter() {
    }

    public void swapSteps(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_master, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_master_ingredients, parent, false);
                break;
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
        return new StepsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                Step step = mSteps.get(position);
                holder.mShortDesc.setText(step.getShortDescription());
                String stepText = "Step " + position;
                holder.mStepNumber.setText(stepText);
                GlideApp.with(holder.itemView.getContext())
                        .load(step.getThumbnailURL())
                        .into(holder.mThumbnail);
                break;
            case 1:
                holder.itemView.setOnClickListener(v -> Timber.d("Ingredients clicked"));
                break;
            default:
                throw new IllegalArgumentException("Unknown view type: " + holder.getItemViewType());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return mSteps != null ? mSteps.size() : 0;
    }

    static class StepsViewHolder extends RecyclerView.ViewHolder {

        @Nullable @BindView(R.id.tv_master_step_short_desc)
        TextView mShortDesc;
        @Nullable @BindView(R.id.iv_master_thumb)
        ImageView mThumbnail;
        @Nullable @BindView(R.id.tv_master_step_number)
        TextView mStepNumber;

        public StepsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
