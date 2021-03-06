package com.github.devjn.githubsearch;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.devjn.githubsearch.model.entities.GitData;
import com.github.devjn.githubsearch.service.GitHubApi;
import com.github.devjn.githubsearch.model.entities.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static com.github.devjn.githubsearch.MockGithubService.getTestUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by @author Jahongir on 30-Apr-17
 * devjn@jn-arts.com
 * GithubApiMockAdapterTest
 */

@RunWith(AndroidJUnit4.class)
public class GithubApiMockAdapterTest {
    private MockRetrofit mockRetrofit;

    @Before
    public void setUp() throws Exception {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://test.com")
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
    }


    @SmallTest
    @Test
    public void testUsersRetrieval() throws Exception {
        BehaviorDelegate<GitHubApi> delegate = mockRetrofit.create(GitHubApi.class);
        GitHubApi mockGitService = new MockGithubService(delegate);

        //Actual Test
        Single<GitData<User>> observerData = mockGitService.getUsers("devjn", 1);
        GitData<User> data = observerData.blockingGet();
        assertFalse(data.getIncomplete_results());
        assertEquals(3, data.getTotal_count());
        List<User> users = data.getItems();
        assertNotNull(users);
        User expectedUser = getTestUser();
        User receivedUser = users.get(0);
        assertEquals(expectedUser.getLogin(), receivedUser.getLogin());
        assertEquals(expectedUser.getUrl(), receivedUser.getUrl());
        assertEquals(expectedUser.getAvatar_url(), receivedUser.getAvatar_url());
    }

    @SmallTest
    @Test
    public void testUserRetrieval() throws Exception {
        BehaviorDelegate<GitHubApi> delegate = mockRetrofit.create(GitHubApi.class);
        GitHubApi mockGitService = new MockGithubService(delegate);

        //Actual Test
        Single<User> observerData = mockGitService.getUser("devjn");
        User user = observerData.blockingGet();
        assertNotNull(user);
        assertEquals("John", user.getName());
        assertEquals("Some bio", user.getBio());
        assertEquals("Finland", user.getLocation());
        assertEquals("Some company", user.getCompany());
    }

}