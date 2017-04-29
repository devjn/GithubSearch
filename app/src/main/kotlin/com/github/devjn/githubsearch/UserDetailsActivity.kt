package com.github.devjn.githubsearch

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.devjn.githubsearch.databinding.ActivityUserDetailsBinding
import com.github.devjn.githubsearch.utils.GitHubApi
import com.github.devjn.githubsearch.utils.GithubService
import com.github.devjn.githubsearch.utils.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.*


class UserDetailsActivity : AppCompatActivity() {
    val TAG = UserDetailsActivity::class.java.simpleName

    private lateinit var binding: ActivityUserDetailsBinding
    var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        setupTransition()
        setSupportActionBar(binding.toolbar)
        val colors = intArrayOf(R.drawable.gradient_01, R.drawable.gradient_02, R.drawable.gradient_03, R.drawable.gradient_04)
        binding.toolbarLayout.setBackgroundResource(colors[Random().nextInt(4)])
        binding.fab.setOnClickListener { view ->
            isChecked = !isChecked
            var drawable: Drawable
            if (isChecked) {
                drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmarked, null)!!
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.star_pressed))
            } else drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmark, null)!!
            binding.fab.setImageDrawable(drawable)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupTransition() {
        supportPostponeEnterTransition()

        val extras = intent.extras
        val data: User = extras.getParcelable(SearchFragment.EXTRA_DATA)
        this.binding.user = data

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val imageTransitionName = extras.getString(SearchFragment.EXTRA_IMAGE_TRANSITION_NAME)
            binding.imageProfile.transitionName = imageTransitionName
        }

        Glide.with(this).load(data.avatar_url).asBitmap().listener(object : RequestListener<String, Bitmap> {
            override fun onResourceReady(resource: Bitmap?, model: String?, target: Target<Bitmap>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                supportStartPostponedEnterTransition();
                return false
            }

            override fun onException(e: Exception?, model: String?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                supportStartPostponedEnterTransition();
                Log.w(TAG, "image is not loaded: " + e)
                return false
            }
        }).into(binding.imageProfile)

        val gitHubApi = GithubService.createService(GitHubApi::class.java)
        gitHubApi.getUser(data.login).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    binding.content.user = user
                }, { e ->
                    Log.e(TAG, "Error while getting data", e)
                })
    }

}
