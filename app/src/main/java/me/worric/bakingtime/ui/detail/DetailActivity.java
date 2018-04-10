package me.worric.bakingtime.ui.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private static final String EXTRA_RECIPE_ID = "me.worric.bakingtime.extra_recipe_id";

    @Inject
    protected ViewModelProvider.Factory mFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    private boolean mTabletMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1L);
        BakingViewModel mViewModel = ViewModelProviders.of(this, mFactory)
                .get(BakingViewModel.class);
        mViewModel.setChosenRecipe(recipeId);

        mTabletMode = getResources().getBoolean(R.bool.tablet_mode);

        Timber.d("TwoPaneMode? %s", mTabletMode ? "YES" : "NO");

        if (savedInstanceState == null && !mTabletMode) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, new MasterFragment())
                    .addToBackStack(null)
                    .commit();
        }
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
    public void onStepClick(Step step, int position) {
        if (!mTabletMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, new DetailFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
