package com.github.devjn.githubsearch;

import com.github.devjn.githubsearch.utils.GitData;
import com.github.devjn.githubsearch.utils.GitHubApi;
import com.github.devjn.githubsearch.utils.Repository;
import com.github.devjn.githubsearch.utils.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by @author Jahongir on 30-Apr-17
 * devjn@jn-arts.com
 * MockGithubService
 */

public class MockGithubService implements GitHubApi {

    private final BehaviorDelegate<GitHubApi> delegate;

    public MockGithubService(BehaviorDelegate<GitHubApi> service) {
        this.delegate = service;
    }

    @NotNull
    @Override
    public Observable<GitData<User>> getUsers(@NotNull String keyword, int page) {
        List<User> users = new ArrayList<>(3);
        users.add(new User("devjn", "https://api.github.com/users/devjn", "https://avatars2.githubusercontent.com/u/17936372?v=3"));
        users.add(new User("devjn", "https://api.github.com/users/devjn", "https://avatars2.githubusercontent.com/u/17936372?v=3"));
        users.add(new User("devjn", "https://api.github.com/users/devjn", "https://avatars2.githubusercontent.com/u/17936372?v=3"));
        GitData<User> data = new GitData<>(3, false, users);
        return delegate.returningResponse(data).getUsers("devjn", 1);
    }

    @NotNull
    @Override
    public Observable<GitData<Repository>> getRepositories(@NotNull String keyword, int page) {
        return null;
    }

    @NotNull
    @Override
    public Observable<User> getUser(@NotNull String username) {
        User user = new User("devjn", "https://api.github.com/users/devjn", "https://avatars2.githubusercontent.com/u/17936372?v=3");
        user.setName("John");
        user.setBio("Some bio");
        user.setLocation("Finland");
        user.setCompany("Some company");
        return delegate.returningResponse(user).getUser("devjn");
    }
}
