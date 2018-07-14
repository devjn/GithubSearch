package com.github.devjn.githubsearch

import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.devjn.githubsearch.databinding.FragmentBookmarksBinding
import com.github.devjn.githubsearch.databinding.ListItemUserBinding
import com.github.devjn.githubsearch.db.AndroidCursor
import com.github.devjn.githubsearch.db.DataProvider
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.model.entities.UserEntity
import com.github.devjn.githubsearch.utils.User
import com.minimize.android.rxrecycleradapter.RxDataSource
import io.reactivex.subjects.PublishSubject


/**
 * Created by @author Jahongir on 30-Apr-17
 * devjn@jn-arts.com
 * BookmarksFragment
 */
class BookmarksFragment : BaseFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var binding: FragmentBookmarksBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager

    private var mData: ArrayList<User> = ArrayList()
    private lateinit var rxDataSource: RxDataSource<User>
    private val onClickSubject = PublishSubject.create<ViewDataBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmarks, container, false)
        mRecyclerView = binding.list
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context, mLayoutManager.orientation))
        binding.emptyText.text = getString(R.string.empty_bookmarks)
        checkEmptyView()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rxDataSource = RxDataSource(mData)
        val dataSource = rxDataSource.bindRecyclerView<ListItemUserBinding>(mRecyclerView, R.layout.list_item_user)
        dataSource.subscribe { viewHolder ->
            val b: ListItemUserBinding = viewHolder.viewDataBinding
            b.user = viewHolder.item as User
            b.root.setOnClickListener { onClickSubject.onNext(b) }
        }
        onClickSubject.subscribe { bind ->
            val imageView = (bind as ListItemUserBinding).imageUser
            // Pass data object in the bundle and populate details activity.
            val intent = Intent(context, UserDetailsActivity::class.java).apply {
                putExtra(SearchFragment.EXTRA_DATA, bind.user)
                putExtra(SearchFragment.EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView))
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!,
                    imageView, ViewCompat.getTransitionName(imageView))
            startActivity(intent, options.toBundle())
        }
        loaderManager.initLoader(1, null, this);
    }

    override fun onDestroy() {
        super.onDestroy()
        loaderManager.destroyLoader(1)
    }

    private fun checkEmptyView() {
        if (mData.isEmpty())
            binding.emptyText.visibility = View.VISIBLE
        else binding.emptyText.visibility = View.GONE
    }

    //  -------    Loader  ----------------

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mData.clear()
        rxDataSource.updateDataSet(mData).updateAdapter()
        binding.progressBar.visibility = View.GONE
        checkEmptyView()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val loader = CursorLoader(activity!!,
                DataProvider.CONTENT_URI_BOOKMARKS,
                null, null, null,
                UserEntity.Tags.LOGIN.fieldName + " ASC");
        return loader;
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mData.clear()
        mData.addAll(DataSource.usersFromCursor(AndroidCursor(data), false))
        rxDataSource.updateDataSet(mData).updateAdapter()
        binding.progressBar.visibility = View.GONE
        checkEmptyView()
    }


}