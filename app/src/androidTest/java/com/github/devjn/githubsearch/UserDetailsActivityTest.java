package com.github.devjn.githubsearch;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import com.github.devjn.githubsearch.utils.GithubService;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.devjn.githubsearch.MockGithubService.getTestUser;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by @author Jahongir on 01-May-17
 * devjn@jn-arts.com
 * UserDetailsActivityTest
 */

@RunWith(AndroidJUnit4.class)
public class UserDetailsActivityTest {

    @Rule
    public ActivityTestRule<UserDetailsActivity> mActivityRule = new ActivityTestRule<>(UserDetailsActivity.class, true, false);

    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        GithubService.changeApiBaseUrl(server.url("/").toString());
        GithubService.changeApiPinnedBaseUrl(server.url("/").toString());
    }

    @Test
    public void testUserIsDisplayed() throws Exception {
        String fileName = "user_devjn.json";
        String fileName2 = "repos_devjn.json";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName2)));

        Intent intent = new Intent();
        intent.putExtra(SearchFragment.Companion.getEXTRA_DATA(), getTestUser());
        intent.putExtra(SearchFragment.Companion.getEXTRA_IMAGE_TRANSITION_NAME(), "some");
        mActivityRule.launchActivity(intent);

        // This part some times not passing
        onView(withText(R.string.empty_info)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.pinned_card)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));

        onView(withText("John")).check(matches(isDisplayed()));
        onView(withId(R.id.grid)).check(matches(allOf(
                isDisplayed(),
                hasChildren(is(6))
        )));
    }


    @Test
    public void testUserDetailsWhenError() throws Exception {

        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(""));
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(""));

        Intent intent = new Intent();
        intent.putExtra(SearchFragment.Companion.getEXTRA_DATA(), getTestUser());
        intent.putExtra(SearchFragment.Companion.getEXTRA_IMAGE_TRANSITION_NAME(), "some");
        mActivityRule.launchActivity(intent);

        onView(withText(R.string.empty_info)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.pinned_card)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }


    public static Matcher<View> hasChildren(final Matcher<Integer> numChildrenMatcher) {
        return new TypeSafeMatcher<View>() {

            /**
             * matching with viewgroup.getChildCount()
             */
            @Override
            public boolean matchesSafely(View view) {
                return view instanceof ViewGroup && numChildrenMatcher.matches(((ViewGroup)view).getChildCount());
            }

            /**
             * gets the description
             */
            @Override
            public void describeTo(Description description) {
                description.appendText(" a view with # children is ");
                numChildrenMatcher.describeTo(description);
            }
        };
    }

}
