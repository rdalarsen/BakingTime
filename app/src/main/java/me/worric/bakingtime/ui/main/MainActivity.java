package me.worric.bakingtime.ui.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_recipe_list) protected RecyclerView mRecipeList;

    @Inject
    protected ViewModelProvider.Factory mViewModelFactory;
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
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(BakingViewModel.class);
        mViewModel.getRecipes().observe(this, recipesViews -> {
            Timber.e("Recipes %s", recipesViews == null ? "NULL" : "NOT NULL");
            mAdapter.swapData(recipesViews);
        });
    }

    private void setupRecyclerView() {
        mRecipeList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        mAdapter = new RecipeAdapter();
        mRecipeList.setAdapter(mAdapter);
    }

}
