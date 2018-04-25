package me.worric.bakingtime.ui.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.detail.DetailActivity;
import me.worric.bakingtime.ui.util.UiUtils;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;

import static me.worric.bakingtime.ui.util.UiUtils.EXTRA_LAYOUT_MANAGER_STATE;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_recipe_list) protected RecyclerView mRecipeList;

    @Inject
    protected ViewModelProvider.Factory mFactory;
    private BakingViewModel mViewModel;
    private RecipeAdapter mAdapter;

    // Testing
    private CountingIdlingResource mIdlingResource =
            new CountingIdlingResource(MainActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecyclerView();

        setupViewModel(savedInstanceState);
    }

    private void setupViewModel(final Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, mFactory).get(BakingViewModel.class);

        mIdlingResource.increment();

        mViewModel.getAllRecipes().observe(this, recipesViews -> {
            if (recipesViews == null || recipesViews.size() == 0) return;

            mAdapter.swapData(recipesViews);
            restoreLayoutManagerState(savedInstanceState);

            mIdlingResource.decrement();
        });
    }

    @VisibleForTesting
    public CountingIdlingResource getIdlingResource() {
        return mIdlingResource;
    }

    private void setupRecyclerView() {
        final boolean tabletMode = getResources().getBoolean(R.bool.tablet_mode);
        if (tabletMode) {
            mRecipeList.setLayoutManager(new GridLayoutManager(this,
                    UiUtils.calculateSpanCount(this)));
        } else {
            mRecipeList.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));
        }

        mAdapter = new RecipeAdapter(recipeView -> {
            Intent intent = DetailActivity.newIntent(this, recipeView.mRecipe.getId());
            startActivity(intent);
        });
        mRecipeList.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveLayoutManagerState(outState);
    }

    private void saveLayoutManagerState(final Bundle outState) {
        Parcelable savedLayoutManagerState = mRecipeList.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(EXTRA_LAYOUT_MANAGER_STATE, savedLayoutManagerState);
    }

    private void restoreLayoutManagerState(final Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        Parcelable savedLayoutManagerState = savedInstanceState
                .getParcelable(EXTRA_LAYOUT_MANAGER_STATE);
        mRecipeList.getLayoutManager().onRestoreInstanceState(savedLayoutManagerState);
    }

}
