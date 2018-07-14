package com.github.devjn.githubsearch

import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatDelegate
import com.github.devjn.githubsearch.utils.AndroidUtils

/**
 * Created by @author Jahongir on 24-Apr-17
 * devjn@jn-arts.com
 * App.java
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        AndroidUtils.setup(applicationContext)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    companion object {
        const val TAG = "GithubSearch"

        lateinit var appContext: Context
            private set
    }

}
