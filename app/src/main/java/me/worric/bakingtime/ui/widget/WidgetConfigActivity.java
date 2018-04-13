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
import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.ui.main.RecipeAdapter;
import me.worric.bakingtime.ui.util.UiUtils;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;

public class WidgetConfigActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "me.worric.bakingtime.ui.BakingWidget";
    private static final String PREF_PREFIX_KEY = "bakingwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public WidgetConfigActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static void saveIdPref(Context context, int appWidgetId, long id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, id);
        prefs.apply();
    }

    static long loadIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        long idValue = prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1L);
        return idValue;
    }

    static void deleteIdPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "EXAMPLE";
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @BindView(R.id.rv_recipe_list) protected RecyclerView mRecipeList;

    @Inject
    protected ViewModelProvider.Factory mFactory;
    private RecipeAdapter mAdapter;
    private BakingViewModel mViewModel;

    @Override
    public void onCreate(Bundle icicle) {
        AndroidInjection.inject(this);
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_widget_config);
        ButterKnife.bind(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setupRecyclerView();

        setupViewModel();
        //mAppWidgetText.setText(loadTitlePref(this, mAppWidgetId));
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mFactory).get(BakingViewModel.class);
        mViewModel.getRecipes().observe(this, recipesViews -> {
            mAdapter.swapData(recipesViews);
        });
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

        WidgetRecipeClickListener listener = recipeView -> {
            final Context context = WidgetConfigActivity.this;

            // When the button is clicked, store the string locally
            saveIdPref(context, mAppWidgetId, recipeView.mRecipe.getId());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BakingWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        };
        mAdapter = new RecipeAdapter(listener);
        mRecipeList.setAdapter(mAdapter);
    }

    public interface WidgetRecipeClickListener {
        void onRecipeClicked(RecipeView recipe);
    }
}
