package com.github.devjn.githubsearch.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.github.devjn.githubsearch.App
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by @author Jahongir on 11-Aug-18
 * devjn@jn-arts.com
 * BaseViewModel
 */
abstract class BaseViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    fun scheduleDirect(run: () -> Unit) = (Schedulers.io().scheduleDirect(run))

    fun <T> runAsync(callable: () -> T, onSuccess: (T) -> Unit, onError: (Throwable) -> Unit = errorConsumer) = disposables.add(Single.fromCallable(callable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)
    )

    fun <T> subscribe(single: Single<T>, onSuccess: (T) -> Unit, onError: (Throwable) -> Unit = errorConsumer) = disposables.add(single
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)
    )

    fun <T> subscribe(single: Observable<T>, onSuccess: (T) -> Unit, onError: (Throwable) -> Unit = errorConsumer) = disposables.add(single
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)
    )

    /**
     * Add to disposables to dispose during destroy
     */
    fun Disposable.disposeOnClear() {
        disposables.add(this)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }


    companion object {
        private val errorConsumer: (Throwable) -> Unit = { throwable: Throwable ->
            Log.w(App.TAG, "Error while doing work in background", throwable)
        }
    }

}