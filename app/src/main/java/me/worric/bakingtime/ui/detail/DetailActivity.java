package me.worric.bakingtime.ui.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import me.worric.bakingtime.R;
import me.worric.bakingtime.data.models.Step;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        MasterFragment.StepClickListener {

    private static final String EXTRA_STEP_ID = "me.worric.bakingtime.extra_step_id";
    private static final long INVALID_STEP_ID = -1L;
    public static final String EXTRA_RECIPE_ID = "me.worric.bakingtime.extra_recipe_id";
    private static final long INVALID_RECIPE_ID = -1L;

    @Inject
    protected ViewModelProvider.Factory mFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    private boolean mTabletMode;
    private BakingViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mTabletMode = getResources().getBoolean(R.bool.tablet_mode);

        Long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, INVALID_RECIPE_ID);
        mViewModel = ViewModelProviders.of(this, mFactory)
                .get(BakingViewModel.class);
        mViewModel.getChosenRecipe().observe(this, recipeView -> {
            if (recipeView != null) getSupportActionBar().setTitle(recipeView.mRecipe.getName());
        });
        Timber.i("Setting chosen recipe with ID: %d", recipeId);
        mViewModel.setChosenRecipe(recipeId);

        // If app process has been killed in the background, we should restore ViewModel
        if (savedInstanceState != null) {
            if (mViewModel.getStep().getValue() == null) {
                Long stepId = savedInstanceState.getLong(EXTRA_STEP_ID, INVALID_STEP_ID);
                Timber.i("Step ID: %d, %s restoring chosen step!", stepId, stepId == INVALID_STEP_ID ? "NOT" : "");
                if (stepId != INVALID_STEP_ID) {
                    mViewModel.setStep(stepId);
                }
            }
        }

        if (savedInstanceState == null && !mTabletMode) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, new MasterFragment())
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Step step = mViewModel.getStep().getValue();
        if (step != null) outState.putLong(EXTRA_STEP_ID, step.getId());
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    public static Intent newIntent(@NonNull Context context, @NonNull Long recipeId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    public void onStepClick(Step step) {
        if (!mTabletMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, new DetailFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @VisibleForTesting
    public boolean getIsTablet() {
        return mTabletMode;
    }

}
