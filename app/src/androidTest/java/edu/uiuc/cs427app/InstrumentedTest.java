package edu.uiuc.cs427app;

// Import necessary packages
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// Specify the test runner
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    // Specify the activity to be launched for the test
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);
    //public ActivityScenarioRule<RegisterActivity> activityRule = new ActivityScenarioRule<>(RegisterActivity.class);
    private IdlingResource idlingResource;

    String loginEmail = "test1@gmail.com";
    String loginPassword = "123123";

    // Initialize Espresso Intents before each test
    @Before
    public void setup() {
        Intents.init();

    }

    // Release Espresso Intents after each test
    @After
    public void tearDown() {
        Intents.release();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    // IdlingResource that waits for a specified time
    public static class ElapsedTimeIdlingResource implements IdlingResource {
        private final long startTime;
        private final long waitingTime;
        private ResourceCallback resourceCallback;

        ElapsedTimeIdlingResource(long waitingTime) {
            this.startTime = System.currentTimeMillis();
            this.waitingTime = waitingTime;
        }

        @Override
        public String getName() {
            return ElapsedTimeIdlingResource.class.getName();
        }

        @Override
        public boolean isIdleNow() {
            long elapsed = System.currentTimeMillis() - startTime;
            boolean idle = (elapsed >= waitingTime);

            if (idle && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }

            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }
    }

    // test for login functionality
    @Test
    public void testUserLogin() {

        performLogin(loginEmail, loginPassword);

        // Verify that the expected intent was sent to launch MainActivity
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));

        // Verify that the expected intent was sent to launch MainActivity
        Intents.intended(
                Matchers.allOf(
                        IntentMatchers.hasComponent(MainActivity.class.getName()),
                        IntentMatchers.hasExtraWithKey("user")
                )
        );

        // Extract the intent
        Intent intent = Intents.getIntents().get(0);

        // Extract the User object from the intent
        User user = (User) intent.getSerializableExtra("user");

        // You can also add assertions to check if other UI elements in MainActivity are visible
        Espresso.onView(ViewMatchers.withId(R.id.mainActivityLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.logout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // For example, check if the MainActivity is displayed after successful login
        Espresso.onView(ViewMatchers.withId(R.id.welcomeNote))
                .check(ViewAssertions.matches(ViewMatchers.withText("WELCOME BACK, " + user.getName() + "!")));
    }

    // test for logout functionality
    @Test
    public void testUserLogout() {
        //login at first
        performLogin(loginEmail, loginPassword);

        Espresso.onView(ViewMatchers.withId(R.id.logout)).perform(ViewActions.click());
        //IdlingRegistry.getInstance().register(idlingResource);

        Espresso.onView(ViewMatchers.withId(R.id.loginActivityLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testRegister() {

    }


    // helper function to login
    private void performLogin(String username, String password) {
        Espresso.onView(ViewMatchers.withId(R.id.loginEmailBox))
                .perform(ViewActions.typeText(username));

        Espresso.onView(ViewMatchers.withId(R.id.loginPasswordBox))
                .perform(ViewActions.typeText(password));

        Espresso.onView(ViewMatchers.withId(R.id.loginUser))
                .perform(ViewActions.click());

        idlingResource = new ElapsedTimeIdlingResource(2000); // Adjust timeout as needed
        IdlingRegistry.getInstance().register(idlingResource);

    }
}

