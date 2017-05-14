package com.github.devjn.githubsearch

import apple.NSObject
import apple.foundation.NSDictionary
import apple.uikit.UIApplication
import apple.uikit.UIWindow
import apple.uikit.c.UIKit
import apple.uikit.protocol.UIApplicationDelegate
import com.github.devjn.githubsearch.db.SQLiteDatabaseHelper
import com.github.devjn.githubsearch.model.db.DataSource

import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.objc.ann.Selector

@RegisterOnStartup
class Main protected constructor(peer: Pointer) : NSObject(peer), UIApplicationDelegate {

    private var window: UIWindow? = null

    override fun applicationDidFinishLaunchingWithOptions(application: UIApplication?, launchOptions: NSDictionary<*, *>?): Boolean {
        return true
    }

    override fun setWindow(value: UIWindow?) {
        window = value
    }

    override fun window(): UIWindow? {
        return window
    }

    companion object {

        val dataSource = DataSource(SQLiteDatabaseHelper())

        init {
            dataSource.open()
        }

        @JvmStatic fun main(args: Array<String>) {
            UIKit.UIApplicationMain(0, null, null, Main::class.java.getName())
        }

        @Selector("alloc")
        @JvmStatic external fun alloc(): Main
    }
}
