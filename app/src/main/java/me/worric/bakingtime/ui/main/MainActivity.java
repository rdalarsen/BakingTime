package me.worric.bakingtime.ui.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.detail.DetailActivity;
import me.worric.bakingtime.ui.util.UiUtils;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_recipe_list) protected RecyclerView mRecipeList;

    @Inject
    protected ViewModelProvider.Factory mFactory;
    private BakingViewModel mViewModel;
    private RecipeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecyclerView();

        setupViewModel();
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mFactory).get(BakingViewModel.class);
        mViewModel.getRecipes().observe(this, recipesViews -> {
            Timber.e("Recipes %s", recipesViews == null ? "NULL" : "NOT NULL");
            mAdapter.swapData(recipesViews);
        });
    }

    private void setupRecyclerView() {
        mRecipeList.setLayoutManager(new GridLayoutManager(this,
                UiUtils.calculateSpanCount(this)));

        mAdapter = new RecipeAdapter(recipeView -> {
            Intent intent = DetailActivity.newIntent(this, recipeView.mRecipe.getId());
            startActivity(intent);
        });
        mRecipeList.setAdapter(mAdapter);
    }

}
