package me.worric.bakingtime.ui.main;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.detail.DetailActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private static final Long RECIPE_ID_NUTELLA_PIE = 1L;
    private static final String PACKAGE = "me.worric.bakingtime";
    private static final int MIN_NUM_RECIPES = 1;
    private static final int FIRST_POSITION = 0;

    private IdlingResource mIdlingResource;

    @Rule
    public final IntentsTestRule<MainActivity> mIntentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mIntentsTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void onLaunch_recipeListIsDisplayed() {
        onView(withId(R.id.rv_recipe_list))
                .check(matches(isDisplayed()));
    }

    @Test
    public void onLaunch_recipeListHasMinimumOneRecipe() {
        onView(withId(R.id.rv_recipe_list))
                .check(matches(hasMinimumChildCount(MIN_NUM_RECIPES)));
    }

    @Test
    public void onClick_firstEntrySendsCorrectIntent() {
        onView(withId(R.id.rv_recipe_list))
                .perform(actionOnItemAtPosition(FIRST_POSITION, click()));

        intended(allOf(
                hasExtra(DetailActivity.EXTRA_RECIPE_ID, RECIPE_ID_NUTELLA_PIE),
                toPackage(PACKAGE)
        ));
    }

    @Test
    public void onClick_firstEntryNavigatesToDetailActivityWithMasterFragment() {
        onView(withId(R.id.rv_recipe_list))
                .perform(actionOnItemAtPosition(FIRST_POSITION, click()));

        onView(withId(R.id.rv_master_fragment_steps))
                .check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }
}