package com.github.devjn.githubsearch

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.support.v7.app.AppCompatDelegate
import com.github.devjn.githubsearch.db.AppDatabase
import com.github.devjn.githubsearch.utils.AndroidUtils

/**
 * Created by @author Jahongir on 24-Apr-17
 * devjn@jn-arts.com
 * App.java
 */

class App : Application() {

    companion object {
        const val TAG = "GithubSearch"
        val appDatabase by lazy { Room.databaseBuilder(appContext, AppDatabase::class.java, "database-name").build() }

        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        AndroidUtils.setup(applicationContext)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }


}
