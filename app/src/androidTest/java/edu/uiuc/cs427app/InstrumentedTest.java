package edu.uiuc.cs427app;

// Import necessary packages

import android.content.Intent;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.AndroidJUnit4;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;
import org.hamcrest.Matcher;


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

    String registerEmail = "registertest@gmail.com";
    String registerPassword = "123123";
    String registerName = "TESTER X";
    String addCity1 = "New York";
    String addCity2 = "Chicago";

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

        //click register button
        Espresso.onView(ViewMatchers.withId(R.id.register)).perform(ViewActions.click());

        // type in user info
        Espresso.onView(ViewMatchers.withId(R.id.registerEmailBox))
                .perform(ViewActions.typeText(registerEmail));
        Espresso.onView(ViewMatchers.withId(R.id.registerPasswordBox))
                .perform(ViewActions.typeText(registerPassword));
        Espresso.onView(ViewMatchers.withId(R.id.registerNameBox))
                .perform(ViewActions.typeText(registerName), closeSoftKeyboard());

        // open Spinner dropdown
//        Espresso.onView(ViewMatchers.withId(R.id.registerThemeBox)).perform(ViewActions.click());
//        Espresso.onData(withItemValue("Your Desired Value")).perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.registerUser)).perform(ViewActions.click());

        idlingResource = new ElapsedTimeIdlingResource(2000); // Adjust timeout as needed
        IdlingRegistry.getInstance().register(idlingResource);

        Espresso.onView(ViewMatchers.withId(R.id.welcomeNote))
                .check(ViewAssertions.matches(ViewMatchers.withText("WELCOME BACK, " + registerName + "!")));

        IdlingRegistry.getInstance().unregister(idlingResource);

        // logout then login with the new account
        Espresso.onView(ViewMatchers.withId(R.id.logout)).perform(ViewActions.click());
        performLogin(registerEmail, registerPassword);
        Espresso.onView(ViewMatchers.withId(R.id.welcomeNote))
                .check(ViewAssertions.matches(ViewMatchers.withText("WELCOME BACK, " + registerName + "!")));

        // delete the new registered account after test
        deleteUser();

    }
    @Test
    public void testAddCity() throws InterruptedException {
        //login at first
        performLogin(loginEmail,loginPassword);

        //emulate to add a location by clicking the"AddLocation"button
        Espresso.onView(ViewMatchers.withId(R.id.buttonAddLocation)).perform(ViewActions.click());
        //check that the app is now redirected to the searchlayout page
        Espresso.onView(ViewMatchers.withId(R.id.searchLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        //next we will emulate the search of 'newyork' city and click on the search button
        Espresso.onView(ViewMatchers.withId(R.id.searchTextView))
                .perform(ViewActions.typeText(addCity1));

        Espresso.onView(ViewMatchers.withId(R.id.searchButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.scrollToPosition(0));

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // check city added
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(hasDescendant(ViewMatchers.withText(addCity1))));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRemoveCity(){
        //login at first
        performLogin(loginEmail,loginPassword);

        // add before delete
        //emulate to add a location by clicking the"AddLocation"button
        Espresso.onView(ViewMatchers.withId(R.id.buttonAddLocation)).perform(ViewActions.click());
        //check that the app is now redirected to the searchlayout page
        Espresso.onView(ViewMatchers.withId(R.id.searchLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        //next we will emulate the search of 'newyork' city and click on the search button
        Espresso.onView(ViewMatchers.withId(R.id.searchTextView))
                .perform(ViewActions.typeText(addCity1));

        Espresso.onView(ViewMatchers.withId(R.id.searchButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.scrollToPosition(0));

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // check city deleted
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(hasDescendant(ViewMatchers.withText(addCity1))));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //emulate the action of clicking into list management
        Espresso.onView(ViewMatchers.withId(R.id.listManagementButton)).perform(ViewActions.click());

        //check that the app is now redirected to the delete Laout page
        Espresso.onView(ViewMatchers.withId(R.id.deleteLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        //emulate city deletion
        Espresso.onView(ViewMatchers.withId(R.id.cityDeleteButton)).perform(ViewActions.click());

        // Emulate clicking "No" button in the AlertDialog
        Espresso.onView(ViewMatchers.withText("No"))
                .perform(ViewActions.click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //asserting that choosing "No" to delete the city means the city is still there in the list
        Espresso.onView(ViewMatchers.withId(R.id.deleteLayout))
                .check(ViewAssertions.matches(hasDescendant(ViewMatchers.withText(addCity1))));

        //emulate city deletion once more
        Espresso.onView(ViewMatchers.withId(R.id.cityDeleteButton)).perform(ViewActions.click());

        // Emulate clicking "Yes" button in the AlertDialog
        Espresso.onView(ViewMatchers.withText("Yes"))
                .perform(ViewActions.click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //asserting that choosing "Yes" to delete the city means the city is no longer in the list
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(not(hasDescendant(ViewMatchers.withText(addCity1)))));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testCheckWeather() throws InterruptedException {
        //login at first
        performLogin(loginEmail,loginPassword);

        //add the first city
        Espresso.onView(ViewMatchers.withId(R.id.buttonAddLocation)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.searchTextView))
                .perform(ViewActions.typeText(addCity1));
        Espresso.onView(ViewMatchers.withId(R.id.searchButton))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.scrollToPosition(0));
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //add the second city
        Espresso.onView(ViewMatchers.withId(R.id.buttonAddLocation)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.searchTextView))
                .perform(ViewActions.typeText(addCity2));
        Espresso.onView(ViewMatchers.withId(R.id.searchButton))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.scrollToPosition(0));
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //click on weather button in the first city in the list
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.Weathebutton)));
        //make sure weather layout is displayed
        Espresso.onView(ViewMatchers.withId(R.id.weatherActivityLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        //make sure right city is displayed
        Espresso.onView(ViewMatchers.withId(R.id.CityName))
                        .check(ViewAssertions.matches(ViewMatchers.withText(addCity1)));

        //click on weather button in the second city in the list
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, clickChildViewWithId(R.id.Weathebutton)));
        //make sure weather layout is displayed
        Espresso.onView(ViewMatchers.withId(R.id.weatherActivityLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        //make sure right city is displayed
        Espresso.onView(ViewMatchers.withId(R.id.CityName))
                .check(ViewAssertions.matches(ViewMatchers.withText(addCity2)));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCheckMap() throws InterruptedException {
        //login at first
        performLogin(loginEmail,loginPassword);

        //add the first city
        Espresso.onView(ViewMatchers.withId(R.id.buttonAddLocation)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.searchTextView))
                .perform(ViewActions.typeText(addCity1));
        Espresso.onView(ViewMatchers.withId(R.id.searchButton))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.scrollToPosition(0));
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //add the second city
        Espresso.onView(ViewMatchers.withId(R.id.buttonAddLocation)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.searchTextView))
                .perform(ViewActions.typeText(addCity2));
        Espresso.onView(ViewMatchers.withId(R.id.searchButton))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.scrollToPosition(0));
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView2))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //click on map button in the first city in the list
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.mapButton)));
        //make sure map activity layout is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapActivityLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        //make sure right city is displayed
        Espresso.onView(ViewMatchers.withId(R.id.cityNameMap))
                .check(ViewAssertions.matches(ViewMatchers.withText(addCity1)));

        //click on map button in the second city in the list
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, clickChildViewWithId(R.id.mapButton)));
        //make sure map activity layout is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapActivityLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        //make sure right city is displayed
        Espresso.onView(ViewMatchers.withId(R.id.cityNameMap))
                .check(ViewAssertions.matches(ViewMatchers.withText(addCity2)));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

    private void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid()).delete();

        user.delete();
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

}

