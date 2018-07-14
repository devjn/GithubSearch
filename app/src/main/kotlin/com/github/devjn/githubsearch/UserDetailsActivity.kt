package com.github.devjn.githubsearch

import GetPinnedReposQuery
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.devjn.githubsearch.databinding.FragmentUserDetailsBinding
import com.github.devjn.githubsearch.db.DataProvider
import com.github.devjn.githubsearch.model.entities.UserEntity
import com.github.devjn.githubsearch.utils.*
import com.github.devjn.githubsearch.views.PinnedCell
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.*


class UserDetailsActivity : BaseActivity() {

    companion object {
        val TAG = UserDetailsActivity::class.java.simpleName!!

        fun start(activity: Activity, imageView: View, user: User?) {
            // Pass data object in the bundle and populate details activity.
            val intent = Intent(activity, UserDetailsActivity::class.java).apply {
                putExtra(SearchFragment.EXTRA_DATA, user)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    imageView, activity.getString(R.string.transition_profile_image))
            activity.startActivity(intent, options.toBundle())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_container)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, UserDetailsFragment())
                    .commit()
    }


    class UserDetailsFragment : BaseFragment() {

        private val colors = intArrayOf(R.drawable.gradient_01, R.drawable.gradient_02, R.drawable.gradient_03, R.drawable.gradient_04)
        private val color = colors[Random().nextInt(4)]

        private lateinit var binding: FragmentUserDetailsBinding
        private lateinit var mUser: User

        private var isBookmarked = false
            set(bookmarked) {
                field = bookmarked
                var drawable: Drawable
                if (bookmarked) {
                    drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmarked, null)!!
                    drawable = DrawableCompat.wrap(drawable)
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(context!!, R.color.star_pressed))
                } else drawable = VectorDrawableCompat.create(resources, R.drawable.ic_bookmark, null)!!
                binding.fab.setImageDrawable(drawable)

                if (isVisible) if (bookmarked) {
                    Log.i(TAG, "adding user to bookmarks: $mUser")
                    DataProvider.insertUser(context!!, mUser)
                    Toast.makeText(context!!, getString(R.string.user_bookmarked, mUser.login), Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "removing user from bookmarks: $mUser")
                    DataProvider.removeUser(context!!, mUser)
                    Toast.makeText(context!!, getString(R.string.user_unbookmarked, mUser.login), Toast.LENGTH_SHORT).show()
                }
            }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val extras = baseActivity.intent.extras
            if (extras.containsKey(SearchFragment.EXTRA_IMAGE_TRANSITION_NAME)) {
                //Postpone transition until image is loaded
                postponeEnterTransition()
            }

            mUser = extras.getSerializable(SearchFragment.EXTRA_DATA) as User
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            super.onCreateView(inflater, container, savedInstanceState)
            this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false)
            baseActivity.setSupportActionBar(binding.toolbar)
            baseActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.toolbarLayout.setBackgroundResource(color)
            binding.fab.setOnClickListener { isBookmarked = !isBookmarked }

            Glide.with(this).load(mUser.avatar_url).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().dontTransform().listener(object : RequestListener<String, Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: String?, target: Target<Bitmap>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onException(e: Exception?, model: String?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    startPostponedEnterTransition()
                    Log.w(TAG, "image is not loaded: ", e)
                    return false
                }
            }).into(binding.imageProfile)

            return binding.root
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            this.binding.user = mUser
            setup()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
            // Back transition on the action bar's Up/Home button
                android.R.id.home -> {
                    baseActivity.supportFinishAfterTransition()
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }


        private fun setup() {
            val cursor = baseActivity.contentResolver.query(Uri.withAppendedPath(DataProvider.CONTENT_URI_BOOKMARKS, mUser.id.toString()),
                    arrayOf(UserEntity.Tags.ID.fieldName), null, null, null)
            if (cursor != null && cursor.moveToFirst()) cursor.use { c ->
                val userId = c.getLong(c.getColumnIndex(UserEntity.Tags.ID.fieldName))
                if (userId == mUser.id) {
                    isBookmarked = true
                }
                Log.i(TAG, "user_id: $userId")
            }

            val gitHubApi = GithubService.createService(GitHubApi::class.java)
            addDisposable(gitHubApi.getUser(mUser.login).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ user ->
                        user.isDetailed = true
                        binding.content!!.user = user
                        user?.let { mUser = it }
                    }, { e ->
                        mUser.isDetailed = true
                        binding.content!!.user = mUser
                        toastNetError()
                        Log.e(TAG, "Error while getting data", e)
                    }))

            addDisposable(GithubGraphQL.getPinnedRepos(mUser.login)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ list ->
                        binding.content?.apply {
                            pinnedCard.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                            grid.adapter = ReposAdapter(context!!, list)
                            pinnedProgress.visibility = View.GONE
                        }
                    }, { e ->
                        binding.content?.pinnedCard?.visibility = View.GONE
                        toastNetError()
                        Log.e(TAG, "Error while getting data", e)
                    }))
        }

        private fun toastNetError() = Toast.makeText(baseActivity, R.string.net_problem, Toast.LENGTH_SHORT).show()


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


}
