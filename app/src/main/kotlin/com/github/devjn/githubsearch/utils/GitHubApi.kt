package com.github.devjn.githubsearch.utils

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by @author Jahongir on 27-Apr-17
 * devjn@jn-arts.com
 * GitHubApi
 */
interface GitHubApi {

    @GET("/search/users?per_page=50")
    fun getUsers(@Query("q") keyword: String): Observable<GitData<User>>

    @GET("/search/repositories")
    fun getRepositories(@Query("q") keyword: String): Observable<GitData<Repository>>

}

interface PinnedReposApi {

    @GET("")
    fun getPinnedRepos(@Query("username") username: String): Observable<List<PinnedRepo>>

}