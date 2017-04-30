package com.github.devjn.githubsearch

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewCompat.getTransitionName
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.github.devjn.githubsearch.databinding.FragmentMainBinding
import com.github.devjn.githubsearch.databinding.ListItemRepositoryBinding
import com.github.devjn.githubsearch.databinding.ListItemUserBinding
import com.github.devjn.githubsearch.utils.*
import com.github.devjn.githubsearch.views.EndlessRecyclerViewScrollListener
import com.minimize.android.rxrecycleradapter.RxDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


@Suppress("UNCHECKED_CAST")
/**
 * Created by @author Jahongir on 27-Apr-17
 * devjn@jn-arts.com
 * SearchFragment
 */
class SearchFragment<T : GitObject>() : BaseFragment() {

    val TAG = SearchFragment::class.simpleName

    private var mData: ArrayList<T> = ArrayList()
    private var mType: Int = TYPE_USERS

    private lateinit var binding: FragmentMainBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mSearchView: FloatingSearchView

    private lateinit var rxDataSource: RxDataSource<T>
    private val onClickSubject = PublishSubject.create<ViewDataBinding>()
    private val gitHubApi = GithubService.createService(GitHubApi::class.java)

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var suggestions: SearchRecentSuggestions
    private var mLastGitData: GitData<T>? = null
    private var mLastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val args = arguments
        args?.let {
            if (it.containsKey(KEY_TYPE))
                mType = args.getInt(KEY_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater!!, R.layout.fragment_main, container, false)
        mRecyclerView = binding.list
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        val mDividerItemDecoration = DividerItemDecoration(mRecyclerView.context, mLayoutManager.orientation)
        mRecyclerView.addItemDecoration(mDividerItemDecoration)
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadMore(page, totalItemsCount)
            }
        }
        mRecyclerView.addOnScrollListener(scrollListener)
        binding.emptyText.text = getString(if (mType == TYPE_USERS) R.string.find_users else R.string.find_repos)
        mSearchView = binding.floatingSearchView
        setupSearchView()
        checkEmptyView()
        return binding.root
    }

    private fun setupSearchView() {
        val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        suggestionAdapter = SuggestionAdapter(activity, searchManager)

        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                mSearchView.clearSuggestions()
            } else {
                //this shows the top left circular progress
                mSearchView.showProgress()
                val list = suggestionAdapter.getSuggestions(newQuery, 5)
                //this will swap the data and render the collapse/expand animations as necessary
                list?.let { mSearchView.swapSuggestions(list) } ?: mSearchView.clearSuggestions()
                mSearchView.hideProgress()
            }
        }

        mSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                mLastQuery = searchSuggestion.body
                search(mLastQuery)
                Log.d(TAG, "onSuggestionClicked()")
            }

            override fun onSearchAction(query: String) {
                mLastQuery = query
                search(query)
                Log.d(TAG, "onSearchAction, query: $query")
            }
        })

        mSearchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {
                //show history when search bar gains focus
                val list = suggestionAdapter.getSuggestions("", 3)
                list?.let { mSearchView.swapSuggestions(list) }
            }

            override fun onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchText(mLastQuery) //BarTitle(mLastQuery)
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rxDataSource = RxDataSource(mData)
        val dataSource = if (mType == TYPE_USERS)
            rxDataSource.bindRecyclerView<ListItemUserBinding>(mRecyclerView, R.layout.list_item_user)
        else rxDataSource.bindRecyclerView<ListItemRepositoryBinding>(mRecyclerView, R.layout.list_item_repository)
        dataSource.subscribe { viewHolder ->
            val b: ViewDataBinding = viewHolder.viewDataBinding
            val data = viewHolder.item
            if (mType == TYPE_USERS)
                (b as ListItemUserBinding).user = data as User
            else
                (b as ListItemRepositoryBinding).repo = data as Repository
            b.root.setOnClickListener { onClickSubject.onNext(b) }
        }
        onClickSubject.subscribe { bind ->
            if (mType != TYPE_USERS) return@subscribe
            val intent = Intent(context, UserDetailsActivity::class.java)
            // Pass data object in the bundle and populate details activity.
            val imageView = (bind as ListItemUserBinding).imageUser
            intent.putExtra(EXTRA_DATA, bind.user)
            intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, getTransitionName(imageView))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    imageView, ViewCompat.getTransitionName(imageView))
            startActivity(intent, options.toBundle())
        }
        suggestions = SearchRecentSuggestions(context, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
    }


    private fun search(query: String) {
        // reset infinite scroll
        scrollListener.resetState()
        suggestions.saveRecentQuery(query, null)
        binding.progressBar.visibility = View.VISIBLE

        val api: Observable<GitData<T>> =
                (if (mType == TYPE_USERS) gitHubApi.getUsers(query) else gitHubApi.getRepositories(query)) as Observable<GitData<T>>
        api.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ gitData ->
                    mData.clear()
                    gitData.items?.let { mData.addAll(it) }
                    rxDataSource.updateDataSet(mData).updateAdapter()
                    binding.progressBar.visibility = View.GONE
                    mLastGitData = gitData
                    checkEmptyView()
                }, { e ->
                    Snackbar.make(binding.root, R.string.connection_problem, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, { search(query) }).show()
                    binding.progressBar.visibility = View.GONE
                    Log.e(TAG, "Error while getting data", e)
                })
    }

    private fun loadMore(page: Int, count: Int) {
        if (mLastGitData?.total_count == count) return
        val api: Observable<GitData<T>> = if (mType == TYPE_USERS) gitHubApi.getUsers(mLastQuery, page) as Observable<GitData<T>>
        else gitHubApi.getRepositories(mLastQuery, page) as Observable<GitData<T>>
        api.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ gitData ->
                    gitData.items?.let { mData.addAll(it) }
                    rxDataSource.updateDataSet(mData).updateNotifyInsertedAdapter(count, mData.size)
                }, { e ->
                    Snackbar.make(binding.root, R.string.connection_problem, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, { search(mLastQuery) }).show()
                    Log.e(TAG, "Error while getting data", e)
                })
    }

    private fun checkEmptyView() {
        if (mData.isEmpty())
            binding.emptyText.visibility = View.VISIBLE
        else binding.emptyText.visibility = View.GONE
    }

    override fun onBackPressedCaptured(): Boolean {
        if (mSearchView.setSearchFocused(false)) {
            return true
        }
        return super.onBackPressedCaptured()
    }

    companion object {

        val KEY_TYPE = "TYPE"
        val TYPE_USERS = 0
        val TYPE_REPOSITORIES = 1

        val EXTRA_DATA = "data"
        val EXTRA_IMAGE_TRANSITION_NAME = "transition_name"

        fun newInstance(type: Int): Fragment {
            val args: Bundle = Bundle()
            args.putInt(KEY_TYPE, type)
            val fragment = if (type == TYPE_USERS) SearchFragment<User>() else SearchFragment<Repository>()
            fragment.arguments = args
            return fragment
        }

    }

}
