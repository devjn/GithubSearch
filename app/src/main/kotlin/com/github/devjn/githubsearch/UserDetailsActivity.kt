package com.github.devjn.githubsearch

import GetPinnedReposQuery
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
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.devjn.githubsearch.databinding.ActivityUserDetailsBinding
import com.github.devjn.githubsearch.db.DataProvider
import com.github.devjn.githubsearch.model.entities.UserEntity
import com.github.devjn.githubsearch.utils.*
import com.github.devjn.githubsearch.views.PinnedCell
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.*


val TAG = UserDetailsActivity::class.java.simpleName!!

class UserDetailsActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var mUser: User
    private var isCreated = false

    private var isBookmarked = false
        set(bookmarked) {
            field = bookmarked
            var drawable: Drawable
            if (bookmarked) {
                drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmarked, null)!!
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.star_pressed))
            } else drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmark, null)!!
            binding.fab.setImageDrawable(drawable)

            if (isCreated) if (bookmarked) {
                Log.i(TAG, "adding user to bookmarks: $mUser")
                DataProvider.insertUser(this, mUser)
                Toast.makeText(this, getString(R.string.user_bookmarked, mUser.login), Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "removing user from bookmarks: $mUser")
                DataProvider.removeUser(this, mUser)
                Toast.makeText(this, getString(R.string.user_unbookmarked, mUser.login), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        setSupportActionBar(binding.toolbar)
        val colors = intArrayOf(R.drawable.gradient_01, R.drawable.gradient_02, R.drawable.gradient_03, R.drawable.gradient_04)
        binding.toolbarLayout.setBackgroundResource(colors[Random().nextInt(4)])
        binding.fab.setOnClickListener { isBookmarked = !isBookmarked }

        mUser = setup()
        val cursor = contentResolver.query(Uri.withAppendedPath(DataProvider.CONTENT_URI_BOOKMARKS, mUser.id.toString()),
                arrayOf(UserEntity.Tags.ID.fieldName), null, null, null)
        if (cursor != null && cursor.moveToFirst()) cursor.use { c ->
            val userId = c.getLong(c.getColumnIndex(UserEntity.Tags.ID.fieldName))
            if (userId == mUser.id) {
                isBookmarked = true
            }
            Log.i(TAG, "user_id: $userId")
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        isCreated = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Back transition on the action bar's Up/Home button
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    private fun setup(): User {
        val extras = intent.extras
        if (extras.containsKey(SearchFragment.EXTRA_IMAGE_TRANSITION_NAME)) {
            //Postpone transition until image is loaded
            supportPostponeEnterTransition()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val imageTransitionName = extras.getString(SearchFragment.EXTRA_IMAGE_TRANSITION_NAME)
                binding.imageProfile.transitionName = imageTransitionName
            }
        }

        val data: User = extras.getSerializable(SearchFragment.EXTRA_DATA) as User
        this.binding.user = data


        Glide.with(this).load(data.avatar_url).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().dontTransform().listener(object : RequestListener<String, Bitmap> {
            override fun onResourceReady(resource: Bitmap?, model: String?, target: Target<Bitmap>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                supportStartPostponedEnterTransition()
                return false
            }

            override fun onException(e: Exception?, model: String?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                supportStartPostponedEnterTransition()
                Log.w(TAG, "image is not loaded: ", e)
                return false
            }
        }).into(binding.imageProfile)

        val gitHubApi = GithubService.createService(GitHubApi::class.java)
        disposables.add(gitHubApi.getUser(data.login).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    user.isDetailed = true
                    binding.content!!.user = user
                    user?.let { mUser = it }
                }, { e ->
                    data.isDetailed = true
                    binding.content!!.user = data
                    toastNetError()
                    Log.e(TAG, "Error while getting data", e)
                }))

        disposables.add(GithubGraphQL.getPinnedRepos(data.login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    if (list.isNotEmpty()) binding.content?.pinnedCard!!.visibility = View.VISIBLE
                    else binding.content!!.pinnedCard.visibility = View.GONE
                    binding.content!!.grid.adapter = ReposAdapter(this@UserDetailsActivity, list)
                    binding.content!!.pinnedProgress.visibility = View.GONE
                }, { e ->
                    binding.content!!.pinnedCard.visibility = View.GONE
                    toastNetError()
                    Log.e(TAG, "Error while getting data", e)
                }))

        return data
    }

    private fun toastNetError() = Toast.makeText(this, R.string.net_problem, Toast.LENGTH_SHORT).show()


    inner class ReposAdapter(private val context: Context, private val products: List<GetPinnedReposQuery.Edge>) : BaseAdapter() {

        override fun getCount(): Int = products.size

        override fun getItem(position: Int): Any = products[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun areAllItemsEnabled(): Boolean = false

        override fun isEnabled(position: Int): Boolean = false

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: PinnedCell

            if (convertView == null) {
                view = PinnedCell(context)
                view.setData(products[position].node()!!)
            } else {
                view = convertView as PinnedCell
            }
            view.id = position
            val lang = products[position].node()?.primaryLanguage()?.name()
            lang?.let {
                val color: Int? = AndroidUtils.colors[it]
                if (color != null)
                    view.nameTextView.setTextColor(color)
            }
            return view
        }
    }

}
