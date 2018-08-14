package com.github.devjn.githubsearch.view

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.devjn.githubsearch.BR
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * BaseFragment
 */
abstract class BaseFragment<BINDING : ViewDataBinding, VIEWMODEL : ViewModel> : Fragment(), BaseActivity.OnBackPressedListener {

    private val disposables = CompositeDisposable()
    protected lateinit var baseActivity: BaseActivity

    protected val viewModel: VIEWMODEL by lazy { provideViewModel() }
    protected lateinit var binding: BINDING
        private set

    abstract fun provideViewModel(): VIEWMODEL
    @LayoutRes
    abstract fun provideLayoutId(): Int

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity !is BaseActivity) throw RuntimeException("Activity holding this fragment should extend BaseActivity")
        this.baseActivity = activity as BaseActivity
        this.baseActivity.onBackListener = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, provideLayoutId(), container, false);
        binding.setLifecycleOwner(this)
        binding.setVariable(BR.viewModel, viewModel)
        setupLayout()
        return binding.root
    }

    abstract fun setupLayout()

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun onBackPressedCaptured(): Boolean = false

    /**
     * Add to disposables to dispose during destroy
     */
    fun Disposable.disp() {
        disposables.add(this)
    }

}