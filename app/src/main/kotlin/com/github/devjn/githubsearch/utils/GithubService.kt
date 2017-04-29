package com.github.devjn.githubsearch.utils

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by @author Jahongir on 27-Apr-17
 * devjn@jn-arts.com
 * GithubService
 */
object GithubService {

    private val BASE_URL = "https://api.github.com/"

    private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    fun <S> createService(serviceClass: Class<S>): S {
        val retrofit = builder.build()
        return retrofit.create(serviceClass)
    }

}