package me.worric.bakingtime.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.worric.bakingtime.R;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    private static final Long RECIPE_ID_NUTELLA_PIE = 1L;
    private static final String INSTRUCTION_INTRO_TEXT = "Recipe Introduction";
    private static final String STEP_3_INSTRUCTIONS =
            "3. Press the cookie crumb mixture into the prepared pie pan and bake for 12 minutes. Let crust cool to room temperature.";
    private static final int TARGET_COLOR = R.color.colorAccent_40;
    private static final String STEP_ENTRY_0 = "Step 0";
    private static final String STEP_ENTRY_3 = "Step 3";
    private static final int FIRST_POSITION = 0;
    private static final int LAST_POSITION = 6;

    private boolean mIsTablet;

    @Rule
    public final ActivityTestRule<DetailActivity> mActivityTestRule =
            new ActivityTestRule<DetailActivity>(DetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry
                            .getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, DetailActivity.class);
                    result.putExtra(DetailActivity.EXTRA_RECIPE_ID, RECIPE_ID_NUTELLA_PIE);
                    return result;
                }
            };

    @Before
    public void checkTabletOrPhoneMode() {
        mIsTablet = mActivityTestRule.getActivity().isTabletMode();
    }

    @Test
    public void onLaunch_masterAndDetailFragmentAreVisible() {
        Assume.assumeTrue(mIsTablet);

        onView(withId(R.id.rv_master_fragment_steps))
                .check(matches(isDisplayed()));
        onView(withId(R.id.detail_exoplayer))
                .check(matches(isDisplayed()));
    }

    @Test
    public void onLaunch_listOfStepsIsPopulated() {
        onView(withId(R.id.rv_master_fragment_steps))
                .check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void onLaunch_stepButtonHasCorrectText() {
        onView(withId(R.id.btn_detail_swap_steps_ingredients))
                .check(matches(withText(R.string.master_frag_btn_show_ingredients)));
    }

    @Test
    public void onClick_stepButtonChangesTextCorrectly() {
        Assume.assumeTrue(mIsTablet);

        onView(withId(R.id.btn_detail_swap_steps_ingredients))
                .perform(click());

        onView(withId(R.id.btn_detail_swap_steps_ingredients))
                .check(matches(withText(R.string.master_frag_btn_show_steps)));

        onView(withId(R.id.btn_detail_swap_steps_ingredients))
                .perform(click());

        onView(withId(R.id.btn_detail_swap_steps_ingredients))
                .check(matches(withText(R.string.master_frag_btn_show_ingredients)));
    }

    @Test
    public void onClick_stepListEntryChangesStepInstructions() {
        Assume.assumeTrue(mIsTablet);

        onView(withId(R.id.tv_detail_step_instructions))
                .check(matches(withText(INSTRUCTION_INTRO_TEXT)));

        onView(withId(R.id.rv_master_fragment_steps))
                .perform(RecyclerViewActions.actionOnItem(withChild(withText(STEP_ENTRY_3)), click()));

        onView(withId(R.id.tv_detail_step_instructions))
                .check(matches(withText(STEP_3_INSTRUCTIONS)));
    }

    @Test
    public void onLaunch_firstStepEntryHasTargetBackgroundColor() {
        Assume.assumeTrue(mIsTablet);

        onView(withChild(withText(STEP_ENTRY_0)))
                .check(matches(withBackgroundColor(TARGET_COLOR)));
    }

    @Test
    public void onClick_stepListEntryChangesColor() {
        Assume.assumeTrue(mIsTablet);

        onView(withChild(withText(STEP_ENTRY_3)))
                .check(matches(not(withBackgroundColor(TARGET_COLOR))));

        onView(withId(R.id.rv_master_fragment_steps))
                .perform(RecyclerViewActions.actionOnItem(withChild(withText(STEP_ENTRY_3)), click()));

        onView(withChild(withText(STEP_ENTRY_3)))
                .check(matches(withBackgroundColor(TARGET_COLOR)));
    }

    @Test
    public void onClick_firstStepListEntryDisplaysDetailFragment() {
        Assume.assumeFalse(mIsTablet);

        onView(withId(R.id.rv_master_fragment_steps))
                .perform(actionOnItemAtPosition(FIRST_POSITION, click()));

        onView(withId(R.id.rv_master_fragment_steps))
                .check(doesNotExist());
        onView(withId(R.id.detail_exoplayer))
                .check(matches(isDisplayed()));
    }

    @Test
    public void onClick_firstStepListEntry_navButtonPreviousIsDisabled() {
        Assume.assumeFalse(mIsTablet);

        onView(withId(R.id.rv_master_fragment_steps))
                .perform(actionOnItemAtPosition(FIRST_POSITION, click()));

        onView(withId(R.id.btn_detail_previous))
                .check(matches(not(isEnabled())));
    }

    @Test
    public void onClick_lastStepListEntry_navButtonNextIsDisabled() {
        Assume.assumeFalse(mIsTablet);

        onView(withId(R.id.rv_master_fragment_steps))
                .perform(actionOnItemAtPosition(LAST_POSITION, click()));

        onView(withId(R.id.btn_detail_next))
                .check(matches(not(isEnabled())));
    }

    @Test
    public void onClick_navButtonNextEnablesNavButtonPrevious() {
        Assume.assumeFalse(mIsTablet);

        onView(withId(R.id.rv_master_fragment_steps))
                .perform(actionOnItemAtPosition(FIRST_POSITION, click()));

        onView(withId(R.id.btn_detail_previous))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.btn_detail_next))
                .perform(click());

        onView(withId(R.id.btn_detail_previous))
                .check(matches(isEnabled()));
    }

    /*
    * Custom matcher; adapted from: https://stackoverflow.com/a/47143659
    */
    public static Matcher<View> withBackgroundColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, ConstraintLayout>(ConstraintLayout.class) {
            @Override
            public boolean matchesSafely(ConstraintLayout layout) {
                Drawable background = layout.getBackground();
                if (background instanceof ColorDrawable) {
                    ColorDrawable colorDrawable = (ColorDrawable) background;
                    return ContextCompat.getColor(getTargetContext(),color) == colorDrawable.getColor();
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