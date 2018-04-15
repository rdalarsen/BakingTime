package me.worric.bakingtime.ui.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import me.worric.bakingtime.R;
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;

import static me.worric.bakingtime.ui.util.UiUtils.EXTRA_LAYOUT_MANAGER_STATE;

public class MasterFragment extends Fragment {

    private Unbinder mUnbinder;

    @BindView(R.id.rv_master_fragment_steps) protected RecyclerView mStepsList;

    @Inject
    protected ViewModelProvider.Factory mFactory;
    private BakingViewModel mViewModel;
    private StepsAdapter mAdapter;
    private LinearLayoutManager mManager;
    private StepClickListener mListener;

    public MasterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        try {
            mListener = (StepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement StepClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_master, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        setupViewModel(savedInstanceState);
    }

    private void setupViewModel(final Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenRecipe().observe(this, recipeView -> {
            mAdapter.swapSteps(recipeView.mSteps);
            restoreLayoutManagerState(savedInstanceState);

            // If we're in tablet mode, load the first step of the recipe automatically
            final boolean isTabletMode = getContext().getResources().getBoolean(R.bool.tablet_mode);
            if (isTabletMode) mViewModel.setChosenStep(recipeView.mSteps.get(0));
        });
    }

    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mStepsList.setLayoutManager(mManager);

        mStepsList.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        mAdapter = new StepsAdapter((step, position) -> {
            mViewModel.setChosenStep(step);
            mListener.onStepClick(step, position);
        });
        mStepsList.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveLayoutManagerState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void saveLayoutManagerState(final Bundle outState) {
        Parcelable savedLayoutManagerState = mManager.onSaveInstanceState();
        outState.putParcelable(EXTRA_LAYOUT_MANAGER_STATE, savedLayoutManagerState);
    }

    private void restoreLayoutManagerState(final Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        Parcelable savedLayoutManagerState = savedInstanceState
                .getParcelable(EXTRA_LAYOUT_MANAGER_STATE);
        mManager.onRestoreInstanceState(savedLayoutManagerState);
    }

    public interface StepClickListener {
        void onStepClick(Step step, int position);
    }

}
