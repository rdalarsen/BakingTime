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

public class MasterFragment extends BaseFragment {

    private static final String EXTRA_SHOW_INGREDIENTS = "me.worric.bakingtime.extra_show_ingredients";
    private static final String EXTRA_STEPS_LIST_STATE = "me.worric.bakingtime.extra_steps_list_state";
    private static final String EXTRA_INGREDIENTS_LIST_STATE = "me.worric.bakingtime.extra_ingredients_list_state";

    @BindView(R.id.rv_master_fragment_steps) protected RecyclerView mContentList;
    @BindView(R.id.btn_detail_swap_steps_ingredients) protected Button mToggleContentButton;

    private BakingViewModel mViewModel;
    private StepsAdapter mStepsAdapter;
    private IngredientsAdapter mIngredientsAdapter;
    private LinearLayoutManager mManager;
    private StepClickListener mListener;

    private boolean mIsShowingIngredients = false;
    private Parcelable mStepListState;
    private Parcelable mIngredientsListState;

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
            mIngredientsListState = savedInstanceState.getParcelable(EXTRA_INGREDIENTS_LIST_STATE);
            mStepListState = savedInstanceState.getParcelable(EXTRA_STEPS_LIST_STATE);
        }

        updateButtonText();

        setupRecyclerView();

        setupViewModel(savedInstanceState == null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("onSaveInstanceState: called");
        Parcelable currentState = mManager.onSaveInstanceState();
        if (mIsShowingIngredients) {
            outState.putParcelable(EXTRA_INGREDIENTS_LIST_STATE, currentState);
            if (mStepListState != null) {
                outState.putParcelable(EXTRA_STEPS_LIST_STATE, mStepListState);
            }
        } else {
            outState.putParcelable(EXTRA_STEPS_LIST_STATE, currentState);
            if (mIngredientsListState != null) {
                outState.putParcelable(EXTRA_INGREDIENTS_LIST_STATE, mIngredientsListState);
            }
        }
        outState.putBoolean(EXTRA_SHOW_INGREDIENTS, mIsShowingIngredients);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroy: called");
    }

    // Helper/onClick methods

    @OnClick(R.id.btn_detail_swap_steps_ingredients)
    public void handleToggleIngredientsClick(View view) {
        if (mIsShowingIngredients) {
            mIngredientsListState = mManager.onSaveInstanceState();
            mContentList.setAdapter(mStepsAdapter);
            if (mStepListState != null) mManager.onRestoreInstanceState(mStepListState);
            mIsShowingIngredients = false;
            updateButtonText();
        } else {
            mStepListState = mManager.onSaveInstanceState();
            mContentList.setAdapter(mIngredientsAdapter);
            if (mIngredientsListState != null) mManager.onRestoreInstanceState(mIngredientsListState);
            mIsShowingIngredients = true;
            updateButtonText();
        }
    }

    private void updateButtonText() {
        if (mIsShowingIngredients) {
            mToggleContentButton.setText(R.string.btn_detail_show_steps);
        } else {
            mToggleContentButton.setText(R.string.btn_detail_show_ingredients);
        }
    }

    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mContentList.setLayoutManager(mManager);

        mContentList.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        mIngredientsAdapter = new IngredientsAdapter();
        mStepsAdapter = new StepsAdapter((step) -> {
            mViewModel.setIsClicked(true);
            mViewModel.setChosenStep(step);
            mStepListState = mManager.onSaveInstanceState();
            mListener.onStepClick(step);
        });

        if (mIsShowingIngredients) {
            mContentList.setAdapter(mIngredientsAdapter);
        } else {
            mContentList.setAdapter(mStepsAdapter);
        }
    }

    private void setupViewModel(final boolean firstRun) {
        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenRecipe().observe(this, recipeView -> {
            Timber.d("calling observe method");
            if (recipeView == null) return;

            mStepsAdapter.swapSteps(recipeView.mSteps);
            mIngredientsAdapter.swapIngredients(recipeView.mIngredients);

            if (mIsShowingIngredients) {
                if (mIngredientsListState != null) mManager.onRestoreInstanceState(mIngredientsListState);
            } else {
                if (mStepListState != null) mManager.onRestoreInstanceState(mStepListState);
            }

            // If we're in tablet mode, load the first step of the recipe automatically
            if (mIsTabletMode && firstRun) mViewModel.setChosenStep(recipeView.mSteps.get(0));
        });
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
        void onStepClick(Step step);
    }

}
