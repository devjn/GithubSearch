package com.github.devjn.githubsearch

import android.os.Bundle
import android.support.v4.app.Fragment


/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * BaseFragment
 */
abstract class BaseFragment : Fragment(), BaseActivity.OnBackPressedListener {

    lateinit var baseActivity : BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.baseActivity = activity as BaseActivity
        this.baseActivity.onBackListener = this
    }

    override fun onBackPressedCaptured(): Boolean = false

}