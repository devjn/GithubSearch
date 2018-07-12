package com.github.devjn.githubsearch.utils

import GetPinnedReposQuery
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.github.devjn.githubsearch.BuildConfig
import io.reactivex.Observable
import okhttp3.OkHttpClient


/**
 * Created by @author Jahongir on 09-Jul-18
 * devjn@jn-arts.com
 * GithubGraphQL
 */
object GithubGraphQL {

    var GRAPHQL_BASE_URL = "https://api.github.com/graphql"
        internal set

    private val okHttp: OkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method(), original.body())
                builder.addHeader("Authorization", "Bearer " + BuildConfig.AUTH_TOKEN)
                chain.proceed(builder.build())
            }
            .build()

    private var apolloBuilder = ApolloClient.builder()
            .serverUrl(GRAPHQL_BASE_URL)
            .okHttpClient(okHttp)

    var apolloClient: ApolloClient = apolloBuilder.build()
        private set


    @JvmStatic
    fun changeApiGraphQLBaseUrl(newApiPinBaseUrl: String) {
        GRAPHQL_BASE_URL = newApiPinBaseUrl

        apolloBuilder = ApolloClient.builder()
                .serverUrl(GRAPHQL_BASE_URL)
                .okHttpClient(okHttp)
        apolloClient = apolloBuilder.build();
    }

    fun getPinnedRepos(user: String): Observable<List<GetPinnedReposQuery.Edge>> {
        val queryCall = GetPinnedReposQuery
                .builder()
                .login(user)
                .build()

        return Rx2Apollo.from(apolloClient.query(queryCall)).map { response -> response.data()?.repositoryOwner()?.pinnedRepositories()?.edges() };
    }

}