package com.github.devjn.githubsearch.viewmodel

import GetPinnedReposQuery
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableInt
import android.util.Log
import android.widget.Toast
import com.github.devjn.githubsearch.App
import com.github.devjn.githubsearch.R
import com.github.devjn.githubsearch.UserDetailsActivity
import com.github.devjn.githubsearch.db.dao.containsUser
import com.github.devjn.githubsearch.utils.GitHubApi
import com.github.devjn.githubsearch.utils.GithubGraphQL
import com.github.devjn.githubsearch.utils.GithubService
import com.github.devjn.githubsearch.utils.User


/**
 * Created by @author Jahongir on 11-Aug-18
 * devjn@jn-arts.com
 * UserDetailsViewModel
 */
class UserDetailsViewModel(user: User) : BaseViewModel() {

    val bookmarkIcon: ObservableInt = ObservableInt(R.drawable.ic_bookmark)
    val gitHubApi = GithubService.createService(GitHubApi::class.java)

    var user: User = user
        set(value) {
            field = value
            userLive.value = value
        }
    val userLive = MutableLiveData<User>()
    val pinnedRepos = MutableLiveData<List<GetPinnedReposQuery.Edge>>()
    var isBookmarked = false
        private set

    /**
     * @param notify if true shows toast to user
     */
    fun setBookmarked(bookmarked: Boolean, notify: Boolean = false) {
        if (isBookmarked == bookmarked) return
        isBookmarked = bookmarked
        bookmarkIcon.set(if (bookmarked) R.drawable.ic_bookmarked else R.drawable.ic_bookmark)
        if (bookmarked) {
            Log.i(UserDetailsActivity.TAG, "adding user to bookmarks: $user")
            scheduleDirect { App.appDatabase.userDao.insertAll(user) }
        } else {
            Log.i(UserDetailsActivity.TAG, "removing user from bookmarks: $user")
            scheduleDirect { App.appDatabase.userDao.remove(user) }
        }

        if (notify)
            Toast.makeText(App.appContext, App.appContext.getString(
                    if (bookmarked) R.string.user_bookmarked else R.string.user_unbookmarked, user.login),
                    Toast.LENGTH_SHORT).show()
    }


    fun setup() {
        runAsync({ App.appDatabase.userDao.containsUser(user) },
                { bookmarked -> setBookmarked(bookmarked) })

        subscribe(gitHubApi.getUser(user.login), { user ->
            user.isDetailed = true
            this.user = user
        }, { e ->
            user.isDetailed = true
            user = user
            toastNetError()
            Log.e(UserDetailsActivity.TAG, "Error while getting data", e)
        })

        subscribe(GithubGraphQL.getPinnedRepos(user.login), { list ->
            pinnedRepos.value = list
        }, { e ->
            toastNetError()
            Log.e(UserDetailsActivity.TAG, "Error while getting data", e)
        })
    }

    private fun toastNetError() = Toast.makeText(App.appContext, R.string.net_problem, Toast.LENGTH_SHORT).show()

}