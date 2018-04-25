package me.worric.bakingtime.ui.main;

import android.app.Activity;
import android.app.Instrumentation;
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
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    public static final String PACKAGE = "me.worric.bakingtime";

    private IdlingResource mIdlingResource;

    @Rule
    public IntentsTestRule<MainActivity> mIntentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Before
    public void setUp() {
        mIdlingResource = mIntentsTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void recipeList_isDisplayedOnStartup() {
        onView(withId(R.id.rv_recipe_list)).check(matches(isDisplayed()));
    }

    @Test
    public void recipeList_hasExactlyFourRecipes() {
        onView(withId(R.id.rv_recipe_list)).check(matches(hasChildCount(4)));
    }

    @Test
    public void clickOnFirstEntry_sendsCorrectIntent() {
        onView(withId(R.id.rv_recipe_list)).perform(actionOnItemAtPosition(0, click()));

        intended(allOf(
                hasExtraWithKey(DetailActivity.EXTRA_RECIPE_ID),
                toPackage(PACKAGE)
        ));
    }

    @Test
    public void clickOnFirstEntry_navigatesToDetailActivityWithMasterFragment() {
        onView(withId(R.id.rv_recipe_list)).perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.rv_master_fragment_steps)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }
}