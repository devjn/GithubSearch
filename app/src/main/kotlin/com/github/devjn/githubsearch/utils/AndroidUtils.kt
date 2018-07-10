package com.github.devjn.githubsearch.utils

import android.app.Activity
import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.Color
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.devjn.githubsearch.App
import com.github.devjn.githubsearch.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.nio.charset.Charset


@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(imageView)
}

@BindingAdapter("drawableLang")
fun setLangImage(textView: TextView, lang: String?) {
    lang?.let {
        val color: Int? = AndroidUtils.colors[it]
        if (color != null) {
            var drawable = ContextCompat.getDrawable(textView.context, R.drawable.round_point)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, color)
            drawable.setBounds(0, 0, AndroidUtils.dp(8f), AndroidUtils.dp(8f))
            textView.setCompoundDrawablesRelative(drawable, null, null, null)
            textView.compoundDrawablePadding = AndroidUtils.dp(2f)
        }
    } ?: textView.setCompoundDrawables(null, null, null, null)
}


/**
 * Created by @author Jahongir on 30-Apr-17
 * devjn@jn-arts.com
 * Utils
 */
object AndroidUtils {

    val TAG = AndroidUtils::class.simpleName

    var density = 1f

    lateinit var colors: HashMap<String, Int>

    fun setup(context: Context) {
        density = context.resources.displayMetrics.density;
        colors = HashMap()
        loadColors()
    }

    fun dp(value: Float) = Math.ceil((density * value).toDouble()).toInt()

    private fun loadColors() {
        val json: String?
        try {
            val ins = App.applicationContext.resources.openRawResource(R.raw.colors)
            val size = ins.available()
            val buffer = ByteArray(size)
            ins.read(buffer)
            ins.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            Log.e(TAG, "Error loading colors", ex)
            return
        }

        val gson: Gson = GsonBuilder().create()
        val type = object : TypeToken<Map<String, String>>() {}.type
        val map: Map<String, String> = gson.fromJson(json, type)

        Observable.just(map)
                .map { stringObjectAMap -> stringObjectAMap.entries }
                .flatMapIterable { entries -> entries }
                .map { v -> colors.put(v.key, Color.parseColor(v.value)); v }
                .subscribeOn(Schedulers.computation())
                .subscribe({ v ->
                    // Log.i(TAG, "Color parsed, " + v)
                }, { e ->
                    Log.e(TAG, "Error parsing color", e)
                })
    }

    fun <T : Any, R : Any> whenAllNotNull(vararg options: T?, block: (List<T>) -> R) {
        if (options.all { it != null }) {
            block(options.filterNotNull())
        }
    }

    fun <T : Any, R : Any> whenAnyNotNull(vararg options: T?, block: (List<T>) -> R) {
        if (options.any { it != null }) {
            block(options.filterNotNull())
        }
    }

    fun startCustomTab(activity: Activity, url: String) {
        val uri = Uri.parse(url)

        // create an intent builder
        val intentBuilder = CustomTabsIntent.Builder()

        // Customizing
        intentBuilder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))

        // build custom tabs intent
        val customTabsIntent = intentBuilder.build()

        // launch the url
        customTabsIntent.launchUrl(activity, uri)
    }

}