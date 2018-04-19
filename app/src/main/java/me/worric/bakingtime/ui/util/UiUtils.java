package me.worric.bakingtime.ui.util;

import android.content.Context;
import android.util.DisplayMetrics;

import me.worric.bakingtime.R;
import timber.log.Timber;

public final class UiUtils {

    public static final String EXTRA_LAYOUT_MANAGER_STATE = "me.worric.bakingtime.extra_layout_manager_state";

    private UiUtils() {
    }

    public static int calculateSpanCount(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpWidth = (metrics.widthPixels / metrics.density);
        int scalingFactor = 250;
        int columns = (int) (dpWidth / scalingFactor);
        return columns < 2 ? 2 : columns;
    }

    public static boolean isPhoneLandscape(Context context) {
        final boolean isLandscapeMode = context.getResources().getBoolean(R.bool.landscape_mode);
        final boolean isTabletMode = context.getResources().getBoolean(R.bool.tablet_mode);

        Timber.d("Landscape mode: %s, tablet mode is: %s", isLandscapeMode, isTabletMode);

        return isLandscapeMode && !isTabletMode;
    }

}
