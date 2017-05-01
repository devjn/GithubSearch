package com.github.devjn.githubsearch

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.devjn.githubsearch.databinding.ActivityUserDetailsBinding
import com.github.devjn.githubsearch.utils.*
import com.github.devjn.githubsearch.views.PinnedCell
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.*


class UserDetailsActivity : AppCompatActivity() {
    val TAG = UserDetailsActivity::class.java.simpleName

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var mUser: User
    private var bookmark = false
    private var isBookmarked
        get() = bookmark;
        set(bookmarked) {
            bookmark = bookmarked
            var drawable: Drawable
            if (bookmarked) {
                drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmarked, null)!!
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.star_pressed))
            } else drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmark, null)!!
            binding.fab.setImageDrawable(drawable)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        mUser = setup()
        setSupportActionBar(binding.toolbar)
        val colors = intArrayOf(R.drawable.gradient_01, R.drawable.gradient_02, R.drawable.gradient_03, R.drawable.gradient_04)
        binding.toolbarLayout.setBackgroundResource(colors[Random().nextInt(4)])
        binding.fab.setOnClickListener { view ->
            isBookmarked = !isBookmarked
            if (isBookmarked) {
                Log.i(TAG, "adding user to bookmarks: " + mUser)
                DataProvider.insertUser(this, mUser)
            } else {
                Log.i(TAG, "removing user from bookmarks: " + mUser)
                DataProvider.removeUser(this, mUser)
            }
        }
        val c = contentResolver.query(Uri.withAppendedPath(DataProvider.CONTENT_URI_BOOKMARKS, mUser.id.toString()),
                arrayOf(DataProvider.BookmarkTags.USER_ID), null, null, null)
        if (c != null && c.moveToFirst()) c.use { c ->
            val user_id = c.getLong(c.getColumnIndex(DataProvider.BookmarkTags.USER_ID));
            if (user_id == mUser.id) {
                isBookmarked = true
            }
            Log.i(TAG, "user_id: " + user_id)
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

    fun setup(): User {
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
                Log.w(TAG, "image is not loaded: ", e)
                return false
            }
        }).into(binding.imageProfile)

        val gitHubApi = GithubService.createService(GitHubApi::class.java)
        gitHubApi.getUser(data.login).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    binding.content.user = user
                    user?.let { mUser = it }
                }, { e ->
                    Log.e(TAG, "Error while getting data", e)
                })

        GithubService.pinnedService.getPinnedRepos(data.login).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    if (list.isNotEmpty()) binding.content.pinnedRoot.visibility = View.VISIBLE
                    else binding.content.pinnedRoot.visibility = View.GONE
                    binding.content.grid.adapter = ReposAdapter(this@UserDetailsActivity, list)
                    binding.content.pinnedProgress.visibility = View.GONE
                }, { e ->
                    binding.content.pinnedRoot.visibility = View.GONE
                    Log.e(TAG, "Error while getting data", e)
                })
        return data
    }

    inner class ReposAdapter(private val context: Context, private val products: List<PinnedRepo>) : BaseAdapter() {

        override fun getCount(): Int = products.size

        override fun getItem(position: Int): Any = products[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun areAllItemsEnabled(): Boolean =  false

        override fun isEnabled(position: Int): Boolean = false

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: PinnedCell

            if (convertView == null) {
                view = PinnedCell(context)
                view.setData(products[position])
            } else {
                view = convertView as PinnedCell
            }
            view.id = position
            val lang = products[position].language
            lang?.let {
                val color: Int? = Utils.colors[it]
                if (color != null)
                    view.nameTextView.setTextColor(color)
            }
            return view
        }
    }

}
