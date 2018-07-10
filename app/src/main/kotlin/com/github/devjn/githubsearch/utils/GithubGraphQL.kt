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

    val okHttp = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method(),
                        original.body())
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
    fun changeApiGradpQLBaseUrl(newApiPinBaseUrl: String) {
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

        return Rx2Apollo.from(apolloClient.query(queryCall)).map { data ->  data.data()?.repositoryOwner()?.pinnedRepositories()?.edges()};
/*        apolloClient.query(queryCall).enqueue(object : ApolloCall.Callback<GetPinnedReposQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                completion(Pair(null, Error(e.message)))
            }

            override fun onResponse(response: com.apollographql.apollo.api.Response<GetPinnedReposQuery.Data>) {
                val errors = response.errors()
                if (!errors.isEmpty()) {
                    val message = errors[0]?.message() ?: ""
                    completion(Pair(null, Error(message)))
                } else {
                    completion(Pair(response.data()?.repositoryOwner()?.pinnedRepositories()?.edges(), null))
                }
            }
        })*/
    }

}