package com.github.devjn.githubsearch

import android.app.Application
import android.content.Context
import com.github.devjn.githubsearch.utils.Utils

/**
 * Created by @author Jahongir on 24-Apr-17
 * devjn@jn-arts.com
 * App.java
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Companion.applicationContext = applicationContext
        Utils.setup(applicationContext)
    }

    companion object {

        val TAG = "SimpleFileManager"
        @Volatile lateinit var applicationContext: Context

    }

}
