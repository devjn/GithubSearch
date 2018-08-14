package com.github.devjn.githubsearch.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableInt
import android.view.View
import com.github.devjn.githubsearch.model.entities.GitData
import com.github.devjn.githubsearch.model.entities.GitObject
import com.github.devjn.githubsearch.service.GitHubApi
import com.github.devjn.githubsearch.service.GithubService
import com.github.devjn.githubsearch.view.SearchFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers


/**
 * Created by @author Jahongir on 11-Aug-18
 * devjn@jn-arts.com
 * SearchViewModel
 */
class SearchViewModel<T : GitObject> : BaseViewModel() {

    private val gitHubApi = GithubService.createService(GitHubApi::class.java)
    val progressBarVisibility = ObservableInt(View.GONE)

    val data = MutableLiveData<List<T>>().apply { value = emptyList() }
    var type: Int = SearchFragment.TYPE_USERS

    var lastGitData: GitData<T>? = null
    var lastQuery = ""

    fun search(query: String, onError: Consumer<Throwable>) {
        getApi(query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisibility.set(View.VISIBLE) }
                .doFinally { progressBarVisibility.set(View.GONE) }
                .subscribe(Consumer<GitData<T>> { gitData ->
                    data.value = gitData.items ?: emptyList()
                    lastGitData = gitData
                }, onError).disposeOnClear()
    }

    fun loadMore(page: Int, count: Int, onError: Consumer<Throwable>) {
        if (lastGitData?.total_count == count) return
        getApi(lastQuery, page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisibility.set(View.VISIBLE) }
                .doFinally { progressBarVisibility.set(View.GONE) }
                .subscribe(Consumer<GitData<T>> { gitData ->
                    gitData.items?.let { data.value =  ArrayList(data.value).apply { addAll(it) } }
                    data.value = gitData.items ?: emptyList()
                }, onError).disposeOnClear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getApi(query: String, page: Int = 1): Single<GitData<T>> =
            (if (type == SearchFragment.TYPE_USERS) gitHubApi.getUsers(query, page)
            else gitHubApi.getRepositories(query, page)) as Single<GitData<T>>

}