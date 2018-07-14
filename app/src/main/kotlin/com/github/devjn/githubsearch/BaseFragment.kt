package com.github.devjn.githubsearch

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * BaseFragment
 */
abstract class BaseFragment : Fragment(), BaseActivity.OnBackPressedListener {

    private val disposables = CompositeDisposable()
    lateinit var baseActivity: BaseActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity !is BaseActivity) throw RuntimeException("Activity holding this fragment should extend BaseActivity")
        this.baseActivity = activity as BaseActivity
        this.baseActivity.onBackListener = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    protected fun addDisposable(disposable: Disposable) = disposables.add(disposable)

    override fun onBackPressedCaptured(): Boolean = false

}