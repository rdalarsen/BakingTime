package me.worric.bakingtime.ui.util;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

import me.worric.bakingtime.data.models.Step;

public final class UiUtils {

    private UiUtils() {
    }

    public static void correctMissingSteps(List<Step> steps) {
        if (steps == null) return;

        List<Long> mMissingSteps = new ArrayList<>(1);
        for(int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            if (step.getId() != (i + mMissingSteps.size())) {
                mMissingSteps.add((long) i);
            }
        }

        if (!mMissingSteps.isEmpty()) {
            for (long i : mMissingSteps) {
                Step step = new Step();
                step.setId(-1L);
                steps.add((int) i, step);
            }
        }
    }

    public static int calculateSpanCount(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpWidth = (metrics.widthPixels / metrics.density);
        int scalingFactor = 210;
        int columns = (int) (dpWidth / scalingFactor);
        return columns < 2 ? 2 : columns;
    }

}
