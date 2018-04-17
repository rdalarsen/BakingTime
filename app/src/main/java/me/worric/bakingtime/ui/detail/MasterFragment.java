package me.worric.bakingtime.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import me.worric.bakingtime.R;
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.ui.common.BaseFragment;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

import static me.worric.bakingtime.ui.util.UiUtils.EXTRA_LAYOUT_MANAGER_STATE;

public class MasterFragment extends BaseFragment {

    private static final String EXTRA_SHOW_INGREDIENTS = "me.worric.bakingtime.extra_show_ingredients";

    @BindView(R.id.rv_master_fragment_steps) protected RecyclerView mStepsList;
    @BindView(R.id.btn_detail_swap_steps_ingredients) protected Button mToggleIngredients;

    private BakingViewModel mViewModel;
    private StepsAdapter mStepsAdapter;
    private IngredientsAdapter mIngredientsAdapter;
    private LinearLayoutManager mManager;
    private StepClickListener mListener;
    private boolean mIsShowingIngredients = false;

    // Lifecycle callbacks

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (StepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement StepClickListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("OnViewCreated: called");
        if (savedInstanceState != null) {
            mIsShowingIngredients = savedInstanceState.getBoolean(EXTRA_SHOW_INGREDIENTS, false);
        }

        updateButtonText();

        setupRecyclerView();

        setupViewModel(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveLayoutManagerState(outState);
        outState.putBoolean(EXTRA_SHOW_INGREDIENTS, mIsShowingIngredients);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroy: called");
    }

    // Helper/onClick methods

    @OnClick(R.id.btn_detail_swap_steps_ingredients)
    public void handleToggleIngredientsClick(View view) {
        if (mIsShowingIngredients) {
            mStepsList.setAdapter(mStepsAdapter);
            mIsShowingIngredients = false;
            updateButtonText();
        } else {
            mStepsList.setAdapter(mIngredientsAdapter);
            mIsShowingIngredients = true;
            updateButtonText();
        }
    }

    private void updateButtonText() {
        if (mIsShowingIngredients) {
            mToggleIngredients.setText(R.string.btn_detail_show_steps);
        } else {
            mToggleIngredients.setText(R.string.btn_detail_show_ingredients);
        }
    }

    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mStepsList.setLayoutManager(mManager);

        mStepsList.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        mIngredientsAdapter = new IngredientsAdapter();
        mStepsAdapter = new StepsAdapter((step, position) -> {
            mViewModel.setChosenStep(step);
            mListener.onStepClick(step, position);
        });

        if (mIsShowingIngredients) {
            mStepsList.setAdapter(mIngredientsAdapter);
        } else {
            mStepsList.setAdapter(mStepsAdapter);
        }
    }

    private void setupViewModel(final Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenRecipe().observe(this, recipeView -> {
            Timber.d("calling observe method");
            if (recipeView == null) return;

            mStepsAdapter.swapSteps(recipeView.mSteps);
            mIngredientsAdapter.swapIngredients(recipeView.mIngredients);
            restoreLayoutManagerState(savedInstanceState);

            // If we're in tablet mode, load the first step of the recipe automatically
            if (mIsTabletMode && savedInstanceState == null) mViewModel.setChosenStep(recipeView.mSteps.get(0));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume: called");
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

    // Base class methods

    @Override
    protected int getLayout() {
        return R.layout.fragment_master;
    }

    // Constructors

    public MasterFragment() {
        // Required empty public constructor
    }

    // Interfaces/classes

    public interface StepClickListener {
        void onStepClick(Step step, int position);
    }

}
