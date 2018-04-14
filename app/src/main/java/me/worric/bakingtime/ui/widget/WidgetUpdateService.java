package me.worric.bakingtime.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import me.worric.bakingtime.R;
import me.worric.bakingtime.di.AppContext;
import timber.log.Timber;

public class WidgetUpdateService extends JobIntentService {

    private static final String ACTION_UPDATE_WIDGET = "me.worric.bakingtime.action_update_widget";
    private static final int JOB_ID = 909;

    @Inject @AppContext
    protected Context mContext;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    public static void startUpdateWidget(Context context) {
        Intent starterIntent = new Intent(context, WidgetUpdateService.class);
        starterIntent.setAction(ACTION_UPDATE_WIDGET);
        enqueueWork(context, WidgetUpdateService.class, JOB_ID, starterIntent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) throw new IllegalArgumentException("Mission action");
        switch (action) {
            case ACTION_UPDATE_WIDGET:
                handleUpdateWidget();
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    private void handleUpdateWidget() {
        Timber.d("Starting update action. Context hashCode: %s", mContext.hashCode());

        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName cn = new ComponentName(this, BakingWidget.class);
        int[] appWidgetIds = manager.getAppWidgetIds(cn);

        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_ingredients_list);

        BakingWidget.updateWidgets(this, manager, appWidgetIds);
    }

}
