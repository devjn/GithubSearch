package com.github.devjn.githubsearch.service

import com.github.devjn.githubsearch.model.entities.GitData
import com.github.devjn.githubsearch.model.entities.Repository
import com.github.devjn.githubsearch.model.entities.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by @author Jahongir on 27-Apr-17
 * devjn@jn-arts.com
 * GitHubApi
 */
interface GitHubApi {

    @GET("/search/users?per_page=50")
    fun getUsers(@Query("q") keyword: String, @Query("page") page: Int = 1): Single<GitData<User>>

    @GET("/search/repositories")
    fun getRepositories(@Query("q") keyword: String, @Query("page") page: Int = 1): Single<GitData<Repository>>

    @GET("/users/{username}")
    fun getUser(@Path("username") username: String): Single<User>

}