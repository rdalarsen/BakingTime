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
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

public class MasterFragment extends Fragment {

    private Unbinder mUnbinder;

    @BindView(R.id.rv_master_fragment_steps) protected RecyclerView mStepsList;

    @Inject
    protected ViewModelProvider.Factory mFactory;
    private BakingViewModel mViewModel;
    private StepsAdapter mAdapter;
    private LinearLayoutManager mManager;

    public MasterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
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
            Timber.d("Recipe id: %d", recipeView.mRecipe.getId());
            restoreLayoutManagerState(savedInstanceState);
        });
    }

    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mStepsList.setLayoutManager(mManager);

        mStepsList.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        mAdapter = new StepsAdapter();
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
        Timber.d("on SAVE: Parcelable is: %s", savedLayoutManagerState == null ? "NULL" : "NOT NULL");
        outState.putParcelable("kaffe", savedLayoutManagerState);
//        mViewModel.setLayoutManagerState(savedLayoutManagerState);
    }

    private void restoreLayoutManagerState(final Bundle savedInstanceState) {
//        Parcelable savedLayoutManagerState = mViewModel.getLayoutManagerState().getValue();
        if (savedInstanceState == null) return;
        Parcelable savedLayoutManagerState = savedInstanceState.getParcelable("kaffe");
        mManager.onRestoreInstanceState(savedLayoutManagerState);
    }

}
