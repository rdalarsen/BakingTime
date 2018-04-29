package me.worric.bakingtime.ui;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.InstrumentationRegistry.getTargetContext;

public final class TestUtils {

    private TestUtils() {}

    /*
     * Custom background color matcher; adapted from: https://stackoverflow.com/a/47143659
     */
    public static Matcher<View> withBackgroundColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, ConstraintLayout>(ConstraintLayout.class) {
            @Override
            public boolean matchesSafely(ConstraintLayout layout) {
                Drawable background = layout.getBackground();
                if (background instanceof ColorDrawable) {
                    ColorDrawable colorDrawable = (ColorDrawable) background;
                    return ContextCompat.getColor(getTargetContext(), color) == colorDrawable.getColor();
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: ");
            }
        };
    }

}
