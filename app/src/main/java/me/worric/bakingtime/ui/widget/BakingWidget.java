package me.worric.bakingtime.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.detail.DetailActivity;

public class BakingWidget extends AppWidgetProvider {

    public static final String WIDGET_ID = "me.worric.bakingtime.widget_id";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        Intent remoteServiceIntent = new Intent(context, BakingRemoteViewsService.class);
        remoteServiceIntent.putExtra(WIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_ingredients_list, remoteServiceIntent);

        views.setTextViewText(R.id.widget_cake_name, "TITLE");

        Intent recipeDetailIntent = new Intent(context, DetailActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, recipeDetailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_ingredients_list, pi);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager,
                                     int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetUpdateService.startUpdateWidget(context);
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}
}

