package me.worric.bakingtime.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.detail.DetailActivity;
import timber.log.Timber;

public class BakingWidgetProvider extends AppWidgetProvider {

    private static final int REQUEST_CODE = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Timber.e("Updating widget with ID of: %d", appWidgetId);

        Intent remoteServiceIntent = new Intent(context, BakingRemoteViewsService.class);
        remoteServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        remoteServiceIntent.setData(Uri.parse(remoteServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        rv.setRemoteAdapter(R.id.widget_ingredients_list, remoteServiceIntent);

        String recipeName = WidgetConfigActivity.loadName(context, appWidgetId);
        rv.setTextViewText(R.id.widget_cake_name, recipeName);

        Intent recipeDetailIntent = new Intent(context, DetailActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, REQUEST_CODE,
                recipeDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.widget_ingredients_list, pi);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    public static void updateWidgets(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, manager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetUpdateService.startUpdateWidget(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Timber.d("Deleting widget with ID: %d", appWidgetId);
            WidgetConfigActivity.deleteIdAndName(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}

}

