package com.github.devjn.githubsearch.service

import GetPinnedReposQuery
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.github.devjn.currencyobserver.utils.NativeUtils
import com.github.devjn.githubsearch.common.BuildConfig
import io.reactivex.Observable
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File


/**
 * Created by @author Jahongir on 09-Jul-18
 * devjn@jn-arts.com
 * GithubGraphQL
 */
object GithubGraphQL {

    var GRAPHQL_BASE_URL = "https://api.github.com/graphql"
        internal set

    private val okHttp: OkHttpClient

    private val apolloBuilder: ApolloClient.Builder

    var apolloClient: ApolloClient
        private set

    init {
        val httpCacheDirectory = File(NativeUtils.resolver.cacheDir, "responses")
        val cacheSize = 10L * 1024 * 1024 // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize)

        okHttp = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val builder = original.newBuilder().method(original.method(), original.body())
                    builder.addHeader("Authorization", "Bearer " + BuildConfig.AUTH_TOKEN)
                    chain.proceed(builder.build())
                }
                .addInterceptor(GithubService.provideOfflineCacheInterceptor())
                .addNetworkInterceptor(GithubService.provideCacheInterceptor())
                .cache(cache)
                .build()

        apolloBuilder = ApolloClient.builder()
                .serverUrl(GRAPHQL_BASE_URL)
                .okHttpClient(okHttp)

        apolloClient = apolloBuilder.build()
    }


    @JvmStatic
    fun changeApiGraphQLBaseUrl(newApiPinBaseUrl: String) {
        GRAPHQL_BASE_URL = newApiPinBaseUrl

        apolloBuilder.serverUrl(newApiPinBaseUrl)
        apolloClient = apolloBuilder.build()
    }

    fun getPinnedRepos(user: String): Observable<List<GetPinnedReposQuery.Edge>> {
        val queryCall = GetPinnedReposQuery.builder()
                .login(user)
                .build()

        return Rx2Apollo.from(apolloClient.query(queryCall)).map { response -> response.data()?.repositoryOwner()?.pinnedRepositories()?.edges() }
    }

}