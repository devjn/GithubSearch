package com.github.devjn.githubsearch

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.devjn.githubsearch.databinding.ActivityUserDetailsBinding
import com.github.devjn.githubsearch.utils.User
import java.lang.Exception




class UserDetailsActivity : AppCompatActivity() {
    val TAG = UserDetailsActivity::class.java.simpleName

    private lateinit var binding: ActivityUserDetailsBinding
    var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        setupTransition()
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            isChecked = !isChecked
            val stateSet = intArrayOf(android.R.attr.state_checked * if (isChecked) 1 else -1)
            binding.fab.setImageState(stateSet, true)
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
    }

}
