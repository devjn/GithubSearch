package com.github.devjn.githubsearch;

import com.github.devjn.githubsearch.model.entities.GitData;
import com.github.devjn.githubsearch.service.GitHubApi;
import com.github.devjn.githubsearch.model.entities.Repository;
import com.github.devjn.githubsearch.model.entities.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by @author Jahongir on 30-Apr-17
 * devjn@jn-arts.com
 * MockGithubService
 */

public class MockGithubService implements GitHubApi {

    private final BehaviorDelegate<GitHubApi> delegate;

    MockGithubService(BehaviorDelegate<GitHubApi> service) {
        this.delegate = service;
    }

    @NotNull
    @Override
    public Single<GitData<User>> getUsers(@NotNull String keyword, int page) {
        List<User> users = new ArrayList<>(3);
        users.add(getTestUser());
        users.add(getTestUser());
        users.add(getTestUser());
        GitData<User> data = new GitData<>(3, false, users);
        return delegate.returningResponse(data).getUsers("devjn", 1);
    }

    @NotNull
    @Override
    public Single<GitData<Repository>> getRepositories(@NotNull String keyword, int page) {
        return null;
    }

    @NotNull
    @Override
    public Single<User> getUser(@NotNull String username) {
        User user = getTestUser();
        user.setName("John");
        user.setBio("Some bio");
        user.setLocation("Finland");
        user.setCompany("Some company");
        return delegate.returningResponse(user).getUser("devjn");
    }


    protected static User getTestUser() {
        return new User(17936372, "devjn", "https://api.github.com/users/devjn", "https://avatars2.githubusercontent.com/u/17936372?v=3");
    }

}
