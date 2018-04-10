package me.worric.bakingtime.ui.detail;

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
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.ui.GlideApp;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private MasterFragment.StepClickListener mListener;
    private List<Step> mSteps;

    public StepsAdapter(MasterFragment.StepClickListener listener) {
        mListener = listener;
    }

    public void swapSteps(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_master, parent, false);
        return new StepsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        final Step step = mSteps.get(position);
        holder.mShortDesc.setText(step.getShortDescription());
        String stepText = "Step " + position;
        holder.mStepNumber.setText(stepText);
        GlideApp.with(holder.itemView.getContext())
                .load(step.getThumbnailURL())
                .into(holder.mThumbnail);
        holder.itemView.setOnClickListener(v -> mListener.onStepClick(step));
    }

    @Override
    public int getItemCount() {
        return mSteps != null ? mSteps.size() : 0;
    }

    static class StepsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_master_step_short_desc)
        TextView mShortDesc;
        @BindView(R.id.iv_master_thumb)
        ImageView mThumbnail;
        @BindView(R.id.tv_master_step_number)
        TextView mStepNumber;

        public StepsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
