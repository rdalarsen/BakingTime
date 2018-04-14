package me.worric.bakingtime.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import me.worric.bakingtime.data.db.models.RecipeView;
import me.worric.bakingtime.data.models.Ingredient;
import me.worric.bakingtime.data.repository.Repository;
import me.worric.bakingtime.ui.detail.DetailActivity;
import timber.log.Timber;

public class BakingRemoteViewsService extends RemoteViewsService {

    @Inject
    protected Repository<RecipeView> recipeRepository;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWIdgetId = intent.getIntExtra(BakingWidget.WIDGET_ID, -1);
        long recipeId = WidgetConfigActivity.loadIdPref(getApplicationContext(), appWIdgetId);
        return new BakingRemoteViewsFactory(getApplicationContext(), recipeRepository, recipeId);
    }

    public static class BakingRemoteViewsFactory implements RemoteViewsFactory {

        private final Repository<RecipeView> mRepository;
        private final Context mContext;
        private final long mRecipeId;
        private RecipeView mRecipeView;

        public BakingRemoteViewsFactory(Context context, Repository<RecipeView> recipeRepository,
                                        long recipeId) {
            mRepository = recipeRepository;
            mContext = context;
            mRecipeId = recipeId;
        }

        @Override
        public void onDataSetChanged() {
            Timber.d("onDataSetChanged: called");
            mRecipeView = mRepository.findOneByIdNonReactive(mRecipeId);
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
        }

        @Override
        public void onDestroy() {
            Timber.d("onDestroy: called");
        }

        @Override
        public int getCount() {
            return (mRecipeView == null || mRecipeView.mIngredients == null) ? 0 : mRecipeView.mIngredients.size();
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
