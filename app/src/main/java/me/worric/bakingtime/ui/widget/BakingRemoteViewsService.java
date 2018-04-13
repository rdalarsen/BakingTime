package me.worric.bakingtime.ui.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.android.AndroidInjection;
import me.worric.bakingtime.AppExecutors;
import me.worric.bakingtime.R;
import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.models.Ingredient;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.di.AppContext;
import me.worric.bakingtime.ui.detail.DetailActivity;
import timber.log.Timber;

public class BakingRemoteViewsService extends RemoteViewsService {

    @Inject
    protected BakingRemoteViewsFactory mFactory;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWIdgetId = intent.getIntExtra(BakingWidget.WIDGET_ID, -1);
        long recipeId = WidgetConfigActivity.loadIdPref(getApplicationContext(), appWIdgetId);
        mFactory.setId(recipeId);
        return mFactory;
    }

    @Singleton
    public static class BakingRemoteViewsFactory implements RemoteViewsFactory {

        private final Repository<RecipeView> mRepository;
        private final Context mContext;
        private final AppExecutors mExecutors;
        private RecipeView mRecipeView;
        private Observer<RecipeView> mRecipeObserver;
        private long mRecipeId = 0L;

        @Inject
        public BakingRemoteViewsFactory(@AppContext Context context,
                                        Repository<RecipeView> recipeRepository,
                                        AppExecutors executors) {
            mRepository = recipeRepository;
            mContext = context;
            mExecutors = executors;
            mRecipeObserver  = recipeView -> {
                mRecipeView = recipeView;
                AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
                ComponentName cn = new ComponentName(mContext, BakingWidget.class);
                int[] widgets = manager.getAppWidgetIds(cn);
                manager.notifyAppWidgetViewDataChanged(widgets, R.id.widget_recipe_list);
            };
        }

        void setId(long recipeId) {
            mRecipeId = recipeId;
        }

        @Override
        public void onDataSetChanged() {
            Timber.d("onDataSetChanged: called");
        }

        @Override
        public RemoteViews getViewAt(int position) {

            Ingredient ingredient = mRecipeView.mIngredients.get(position);

            RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                    android.R.layout.simple_list_item_1);
            rv.setTextViewText(android.R.id.text1, ingredient.getIngredient());

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(DetailActivity.EXTRA_RECIPE_ID, mRecipeView.mRecipe.getId());
            rv.setOnClickFillInIntent(android.R.id.text1, fillInIntent);

            return rv;
        }

        @Override
        public void onCreate() {
            Timber.d("OnCreate: called");
            mExecutors.mainThread().execute(() ->
                    mRepository.findOneById(mRecipeId).observeForever(mRecipeObserver));
        }

        @Override
        public void onDestroy() {
            Timber.d("onDestroy: called");
            mExecutors.mainThread().execute(() ->
                    mRepository.findOneById(mRecipeId).removeObserver(mRecipeObserver));
        }

        @Override
        public int getCount() {
            return mRecipeView == null ? 0 : mRecipeView.mIngredients.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }
    }

}
