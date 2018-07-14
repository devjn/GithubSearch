package com.github.devjn.githubsearch

import android.support.v7.app.AppCompatActivity


/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * BaseActivity
 */
abstract class BaseActivity : AppCompatActivity() {

    interface OnBackPressedListener {
        fun onBackPressedCaptured(): Boolean
    }

    var onBackListener : BaseActivity.OnBackPressedListener? = null

    override fun onBackPressed() {
        onBackListener?.let {
            if (!it.onBackPressedCaptured())
                super.onBackPressed()
        } ?: super.onBackPressed()
    }

}