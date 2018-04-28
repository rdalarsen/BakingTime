package me.worric.bakingtime.ui.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.main.RecipeAdapter;
import me.worric.bakingtime.ui.util.UiUtils;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;

@SuppressWarnings("WeakerAccess")
public class WidgetConfigActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "me.worric.bakingtime.ui.BakingWidgetProvider";
    private static final String PREF_PREFIX_KEY = "bakingwidget_";
    private static final String PREF_TYPE_NAME = "name_";
    private static final String PREF_TYPE_RECIPE_ID = "id_";

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.rv_recipe_list) protected RecyclerView mRecipeList;

    public WidgetConfigActivity() {
        super();
    }

    @Inject
    protected ViewModelProvider.Factory mFactory;
    private RecipeAdapter mAdapter;

    @Override
    public void onCreate(Bundle icicle) {
        AndroidInjection.inject(this);
        super.onCreate(icicle);

        // Set default result used if user cancels
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_widget_config);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setupRecyclerView();

        setupViewModel();
    }

    private void setupViewModel() {
        BakingViewModel viewModel = ViewModelProviders.of(this, mFactory).get(BakingViewModel.class);
        viewModel.getAllRecipes().observe(this, recipesViews ->
                mAdapter.swapData(recipesViews));
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
            final Context context = WidgetConfigActivity.this;

            // Store name and id in prefs
            saveIdAndName(context, mAppWidgetId, recipeView.mRecipe.getId(),
                    recipeView.mRecipe.getName());

            // Update widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BakingWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Pass appWidgetId back to caller
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        });
        mRecipeList.setAdapter(mAdapter);
    }

    static void saveIdAndName(Context context, int appWidgetId, long id, String name) {
        SharedPreferences.Editor prefs = context
                .getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putLong(PREF_PREFIX_KEY + PREF_TYPE_RECIPE_ID + appWidgetId, id);
        prefs.putString(PREF_PREFIX_KEY + PREF_TYPE_NAME + appWidgetId, name);
        prefs.apply();
    }

    static long loadId(Context context, int appWidgetId) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(PREF_PREFIX_KEY + PREF_TYPE_RECIPE_ID + appWidgetId, -1L);
    }

    static String loadName(Context context, int appWidgetId) {
        SharedPreferences prefs = context
                .getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(PREF_PREFIX_KEY + PREF_TYPE_NAME + appWidgetId, "");
    }

    static void deleteIdAndName(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context
                .getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.remove(PREF_PREFIX_KEY + PREF_TYPE_RECIPE_ID + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY + PREF_TYPE_NAME + appWidgetId);
        prefs.apply();
    }

}
