package me.worric.bakingtime.ui.util;

import android.content.Context;
import android.util.DisplayMetrics;

public final class UiUtils {

    private UiUtils() {
    }

    public static int calculateSpanCount(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpWidth = (metrics.widthPixels / metrics.density);
        int scalingFactor = 210;
        int columns = (int) (dpWidth / scalingFactor);
        return columns < 2 ? 2 : columns;
    }

}
