package me.worric.bakingtime.ui.detail;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
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

    private static final int INVALID_STEP_INDEX = -1;

    private final boolean mIsTabletMode;
    private final MasterFragment.StepClickListener mListener;
    @ColorInt private final int mHighlightColor;
    @ColorInt private final int mRestoreColor;
    private List<Step> mSteps;
    private StepDetails mStepDetails;

    StepsAdapter(MasterFragment.StepClickListener listener, boolean isTabletMode, Context context) {
        mListener = listener;
        mIsTabletMode = isTabletMode;
        mHighlightColor = ContextCompat.getColor(context, R.color.colorAccent_100);
        mRestoreColor = ContextCompat.getColor(context, android.R.color.transparent);
    }

    public void swapSteps(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    public void setCurrentStep(StepDetails stepDetails) {
        int oldStepIndex = mStepDetails == null ? INVALID_STEP_INDEX : mStepDetails.stepIndex;
        mStepDetails = stepDetails;
        notifyItemChanged(mStepDetails.stepIndex);
        if (oldStepIndex != INVALID_STEP_INDEX) notifyItemChanged(oldStepIndex);
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

        String stepString = context.getString(R.string.master_frag_list_item_step_num, step.getId());
        holder.mStepNumber.setText(stepString);
        holder.mShortDesc.setText(step.getShortDescription());
        holder.itemView.setOnClickListener(v -> mListener.onStepClick(step));

        if (mIsTabletMode && mStepDetails != null) {
            if (position == mStepDetails.stepIndex) {
                holder.itemView.setBackgroundColor(mHighlightColor);
                loadWithGlide(R.drawable.ic_cake_error_white, holder, step);
            } else {
                holder.itemView.setBackgroundColor(mRestoreColor);
                loadWithGlide(R.drawable.ic_cake_error, holder, step);
            }
        } else if (!mIsTabletMode) {
            loadWithGlide(R.drawable.ic_cake_error, holder, step);
        }
    }

    private void loadWithGlide(@DrawableRes int errorDrawable, StepsViewHolder viewHolder, Step step) {
        GlideApp.with(viewHolder.itemView.getContext())
                .load(step.getThumbnailURL())
                .error(errorDrawable)
                .into(viewHolder.mThumbnail);
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
