package com.github.devjn.githubsearch.view

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityOptionsCompat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.devjn.githubsearch.R
import com.github.devjn.githubsearch.databinding.FragmentUserDetailsBinding
import com.github.devjn.githubsearch.model.entities.PinnedRepo
import com.github.devjn.githubsearch.model.entities.User
import com.github.devjn.githubsearch.utils.AndroidUtils
import com.github.devjn.githubsearch.viewmodel.UserDetailsViewModel
import com.github.devjn.githubsearch.widgets.PinnedCell
import java.lang.Exception
import java.util.*


class UserDetailsActivity : BaseActivity() {

    companion object {
        val TAG: String = UserDetailsActivity::class.java.simpleName

        fun start(activity: Activity, imageView: View, user: User?) {
            // Pass data object in the bundle and populate details activity.
            val intent = Intent(activity, UserDetailsActivity::class.java).apply {
                putExtra(UserDetailsFragment.EXTRA_DATA, user)
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
                    .add(R.id.container, UserDetailsFragment.newInstance(intent.extras))
                    .commit()
    }


    class UserDetailsFragment : BaseFragment<FragmentUserDetailsBinding, UserDetailsViewModel>() {

        companion object {
            const val EXTRA_DATA = "data"
            const val EXTRA_IMAGE_TRANSITION_NAME = "transition_name"
            private val colors = intArrayOf(R.drawable.gradient_01, R.drawable.gradient_02, R.drawable.gradient_03, R.drawable.gradient_04)

            fun newInstance(args: Bundle?) = UserDetailsFragment().apply { arguments = args }
        }

        private val color = colors[Random().nextInt(4)]

        override fun provideViewModel() = ViewModelProviders.of(this, Factory(arguments!!.getSerializable(EXTRA_DATA) as User)).get(UserDetailsViewModel::class.java)
        override fun provideLayoutId() = R.layout.fragment_user_details

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
                if (it.containsKey(EXTRA_IMAGE_TRANSITION_NAME)) {
                    //Postpone transition until image is loaded
                    postponeEnterTransition()
                }
            }
        }

        override fun setupLayout() {
            baseActivity.setSupportActionBar(binding.toolbar)
            baseActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.toolbarLayout.setBackgroundResource(color)
            binding.fab.setOnClickListener { viewModel.toggleBookmarked() }

            Glide.with(this).load(viewModel.user.avatar_url).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().dontTransform().listener(object : RequestListener<String, Bitmap> {
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
        }


        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            viewModel.pinnedRepos.observe(this, android.arch.lifecycle.Observer {
                if (it == null) return@Observer
                binding.grid.adapter = ReposAdapter(context!!, it)
                binding.pinnedProgress.visibility = View.GONE
            })
        }

        override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            // Back transition on the action bar's Up/Home button
            android.R.id.home -> {
                baseActivity.supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        override fun onBackPressedCaptured(): Boolean {
            // Hiding fab to remove flickering
            val params = binding.fab.layoutParams as CoordinatorLayout.LayoutParams;
            params.behavior = null;
            binding.fab.requestLayout();
            binding.fab.visibility = View.GONE;
            return super.onBackPressedCaptured()
        }


        inner class ReposAdapter(private val context: Context, private val products: List<PinnedRepo>) : BaseAdapter() {

            override fun getCount(): Int = products.size

            override fun getItem(position: Int): Any = products[position]

            override fun getItemId(position: Int): Long = position.toLong()

            override fun areAllItemsEnabled(): Boolean = false

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
                products[position].language?.let {
                    val color: Int? = AndroidUtils.colors[it]
                    if (color != null)
                        view.nameTextView.setTextColor(color)
                }
                return view
            }
        }
    }

    private class Factory(val user: User) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = UserDetailsViewModel(user) as T
    }

}
