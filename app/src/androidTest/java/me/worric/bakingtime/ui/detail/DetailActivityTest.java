package me.worric.bakingtime.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.worric.bakingtime.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static me.worric.bakingtime.ui.detail.DetailActivity.EXTRA_RECIPE_ID;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    public static final long RECIPE_ID = 0L;

    @Rule
    public ActivityTestRule<DetailActivity> mActivityTestRule =
            new ActivityTestRule<DetailActivity>(DetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, DetailActivity.class);
                    result.putExtra(EXTRA_RECIPE_ID, RECIPE_ID);
                    return result;
                }
            };

    @Test
    public void showMasterAndDetailFragmentOnLaunch() {
        onView(withId(R.id.rv_master_fragment_steps)).check(matches(isDisplayed()));
        onView(withId(R.id.detail_exoplayer)).check(matches(isDisplayed()));
    }

    @Test
    public void onLaunch_showFirstRecipe() {
        onView(withId(R.id.rv_master_fragment_steps)).check(matches(hasMinimumChildCount(0)));
    }
}