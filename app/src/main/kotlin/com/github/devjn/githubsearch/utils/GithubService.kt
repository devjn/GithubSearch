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
    private val PIN_BASE_URL = "https://gh-pinned-repos.now.sh/"


//    val client : OkHttpClient

//    init {
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
//        client = OkHttpClient.Builder().addInterceptor(interceptor).build()
//    }

    private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private val pinBuilder = Retrofit.Builder()
            .baseUrl(PIN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    val pinnedService: PinnedReposApi by lazy { pinBuilder.build().create(PinnedReposApi::class.java) }

    fun <S> createService(serviceClass: Class<S>): S {
        val retrofit = builder.build()
        return retrofit.create(serviceClass)
    }


}