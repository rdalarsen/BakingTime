package me.worric.bakingtime.ui.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import me.worric.bakingtime.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_recipe_list) protected RecyclerView mRecipeList;

    private RecipeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mRecipeList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        mAdapter = new RecipeAdapter();
        mRecipeList.setAdapter(mAdapter);
    }

}
