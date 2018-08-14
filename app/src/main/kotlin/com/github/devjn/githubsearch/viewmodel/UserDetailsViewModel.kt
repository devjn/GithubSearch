package com.github.devjn.githubsearch.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.util.Log
import android.widget.Toast
import com.github.devjn.githubsearch.App
import com.github.devjn.githubsearch.R
import com.github.devjn.githubsearch.db.dao.containsUser
import com.github.devjn.githubsearch.model.entities.PinnedRepo
import com.github.devjn.githubsearch.model.entities.User
import com.github.devjn.githubsearch.service.GitHubApi
import com.github.devjn.githubsearch.service.GithubGraphQL
import com.github.devjn.githubsearch.service.GithubService
import com.github.devjn.githubsearch.view.UserDetailsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by @author Jahongir on 11-Aug-18
 * devjn@jn-arts.com
 * UserDetailsViewModel
 */
class UserDetailsViewModel(initUser: User) : BaseViewModel() {

    val gitHubApi = GithubService.createService(GitHubApi::class.java)
    val pinnedRepos = MutableLiveData<List<PinnedRepo>>()
    val isBookmarked = ObservableBoolean(false)

    var user: User = initUser
        set(value) {
            field = value
            userLive.value = value
        }
    val userLive = MutableLiveData<User>()

    init {
        runAsync({ App.appDatabase.userDao.containsUser(user) },
                { bookmarked -> isBookmarked.set(bookmarked) })

        subscribe(gitHubApi.getUser(user.login), { user ->
            user.isDetailed = true
            this.user = user
        }, { e ->
            user.isDetailed = true
            user = user
            toastNetError()
            Log.e(UserDetailsActivity.TAG, "Error while getting data", e)
        })

        GithubGraphQL.getPinnedRepos(user.login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val list = ArrayList<PinnedRepo>(it.size)
                    for (edge in it) {
                        list.add(PinnedRepo.fromEdge(edge))
                    }
                    list
                }.subscribe({ list ->
                    pinnedRepos.value = list
                }, { e ->
                    toastNetError()
                    Log.e(UserDetailsActivity.TAG, "Error while getting data", e)
                }).disposeOnClear()
    }

    fun toggleBookmarked() {
        val bookmarked = !isBookmarked.get()
        isBookmarked.set(bookmarked)

        if (bookmarked) {
            Log.i(UserDetailsActivity.TAG, "adding user to bookmarks: $user")
            scheduleDirect { App.appDatabase.userDao.insertAll(user) }
        } else {
            Log.i(UserDetailsActivity.TAG, "removing user from bookmarks: $user")
            scheduleDirect { App.appDatabase.userDao.remove(user) }
        }

        Toast.makeText(App.appContext, App.appContext.getString(
                if (bookmarked) R.string.user_bookmarked else R.string.user_unbookmarked, user.login),
                Toast.LENGTH_SHORT).show()
    }

    private fun toastNetError() = Toast.makeText(App.appContext, R.string.net_problem, Toast.LENGTH_SHORT).show()

}