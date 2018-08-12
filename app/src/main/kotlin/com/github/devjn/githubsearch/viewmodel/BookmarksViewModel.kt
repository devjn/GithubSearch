package com.github.devjn.githubsearch.viewmodel

import android.app.Activity
import android.arch.lifecycle.ViewModel
import com.github.devjn.githubsearch.App
import com.github.devjn.githubsearch.UserDetailsActivity
import com.github.devjn.githubsearch.databinding.ListItemUserBinding


/**
 * Created by @author Jahongir on 10-Aug-18
 * devjn@jn-arts.com
 * BookmarksViewModel
 */
class BookmarksViewModel : ViewModel() {

    val usersLiveData = App.appDatabase.userDao.getAllUsersLive()

    fun onListItemClick(activity: Activity, bind: ListItemUserBinding) = UserDetailsActivity.start(activity, bind.imageUser, bind.user)

}