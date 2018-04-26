package me.worric.bakingtime.ui.detail;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import me.worric.bakingtime.data.models.StepDetails;
import me.worric.bakingtime.ui.GlideApp;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private final boolean mIsTabletMode;
    private final MasterFragment.StepClickListener mListener;
    private List<Step> mSteps;
    private StepDetails mStepDetails;
    @ColorInt private final int mHighlightColor;
    @ColorInt private final int mRestoreColor;

    public StepsAdapter(MasterFragment.StepClickListener listener, boolean isTabletMode, Context context) {
        mListener = listener;
        mIsTabletMode = isTabletMode;
        mHighlightColor = ContextCompat.getColor(context, R.color.colorAccent_40);
        mRestoreColor = ContextCompat.getColor(context, android.R.color.transparent);
    }

    public void swapSteps(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    public void setCurrentStep(StepDetails stepDetails) {
        int oldStepIndex = mStepDetails == null ? -1 : mStepDetails.stepIndex;
        mStepDetails = stepDetails;
        notifyItemChanged(mStepDetails.stepIndex);
        if (oldStepIndex != -1) notifyItemChanged(oldStepIndex);
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
        Context context = holder.itemView.getContext();

        String stepString = context.getString(R.string.list_item_step_num, step.getId());
        holder.mStepNumber.setText(stepString);
        holder.mShortDesc.setText(step.getShortDescription());
        GlideApp.with(holder.itemView.getContext())
                .load(step.getThumbnailURL())
                .into(holder.mThumbnail);
        holder.itemView.setOnClickListener(v -> mListener.onStepClick(step));

        if (mIsTabletMode && mStepDetails != null) {
            if (position == mStepDetails.stepIndex) {
                holder.itemView.setBackgroundColor(mHighlightColor);
            } else {
                holder.itemView.setBackgroundColor(mRestoreColor);
            }
        }
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

        StepsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
