package me.worric.bakingtime.ui.main;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.worric.bakingtime.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void recipeListIsDisplayedOnStartup() {
        onView(withId(R.id.rv_recipe_list)).check(matches(isDisplayed()));
    }
}