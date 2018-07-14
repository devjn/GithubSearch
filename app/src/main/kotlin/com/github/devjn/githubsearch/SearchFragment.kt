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
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
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
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


/**
 * Created by @author Jahongir on 27-Apr-17
 * devjn@jn-arts.com
 * SearchFragment
 */
@Suppress("UNCHECKED_CAST")
class SearchFragment<T : GitObject>() : BaseFragment() {

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
    private var isSearchIntent = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(KEY_TYPE))
                mType = it.getInt(KEY_TYPE)
            if (it.containsKey(SearchManager.QUERY)) {
                mLastQuery = it.getString(SearchManager.QUERY)
                isSearchIntent = true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        mRecyclerView = binding.list
        mSearchView = binding.floatingSearchView
        mLayoutManager = LinearLayoutManager(activity)
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadMore(page + 1, totalItemsCount)
            }
        }
        mRecyclerView.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(scrollListener)
            addItemDecoration(DividerItemDecoration(mRecyclerView.context, mLayoutManager.orientation))
        }
        binding.emptyText.text = getString(if (mType == TYPE_USERS) R.string.find_users else R.string.find_repos)

        setupSearchView()
        checkEmptyView()
        return binding.root
    }


    private fun setupSearchView() {
        val searchManager = requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        suggestionAdapter = SuggestionAdapter(activity!!, searchManager)

        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                suggestionAdapter.getSuggestions("", 3)?.let { mSearchView.swapSuggestions(it) }
            } else {
                //this shows the top left circular progress
                addDisposable(Single.fromCallable { suggestionAdapter.getSuggestions(newQuery, 5) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { mSearchView.showProgress() }
                        .doFinally { mSearchView.hideProgress() }
                        //this will swap the data and render the collapse/expand animations as necessary
                        .subscribe({ list -> mSearchView.swapSuggestions(list) },
                                { mSearchView.clearSuggestions(); }
                        )
                )
            }
        }

        mSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                mLastQuery = searchSuggestion.body
                search(mLastQuery)
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
                suggestionAdapter.getSuggestions("", 3)
                        ?.let { mSearchView.swapSuggestions(it) }
            }

            override fun onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchText(mLastQuery) //BarTitle(mLastQuery)
            }
        })

        mSearchView.setOnBindSuggestionCallback { view, leftIcon, _, _, _ ->
            leftIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_history_black_24dp, null))
            leftIcon.alpha = .36f
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        suggestions = SearchRecentSuggestions(context, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
        rxDataSource = RxDataSource(mData)
        val dataSource = if (mType == TYPE_USERS)
            rxDataSource.bindRecyclerView<ListItemUserBinding>(mRecyclerView, R.layout.list_item_user)
        else rxDataSource.bindRecyclerView<ListItemRepositoryBinding>(mRecyclerView, R.layout.list_item_repository)
        addDisposable(dataSource.subscribe { viewHolder ->
            val b: ViewDataBinding = viewHolder.viewDataBinding
            val data = viewHolder.item
            if (mType == TYPE_USERS)
                (b as ListItemUserBinding).user = data as User
            else
                (b as ListItemRepositoryBinding).repo = data as Repository
            b.root.setOnClickListener { onClickSubject.onNext(b) }
        })
        addDisposable(onClickSubject.subscribe { bind ->
            when (mType) {
                TYPE_REPOSITORIES -> AndroidUtils.startCustomTab(activity!!, (bind as ListItemRepositoryBinding).repo!!.html_url)
                TYPE_USERS -> UserDetailsActivity.start(baseActivity, (bind as ListItemUserBinding).imageUser, bind.user)
            }
        })
        if (isSearchIntent) {
            isSearchIntent = false
            search(mLastQuery)
        }
    }

    private fun search(query: String) {
        // reset infinite scroll
        scrollListener.resetState()
        suggestions.saveRecentQuery(query, null)

        addDisposable(getApi(query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { binding.progressBar.visibility = View.VISIBLE }
                .doFinally { binding.progressBar.visibility = View.GONE }
                .subscribe(Consumer<GitData<T>> { gitData ->
                    mData.clear()
                    gitData.items?.let { mData.addAll(it) }
                    rxDataSource.updateDataSet(mData).updateAdapter()
                    mLastGitData = gitData
                    if (mData.isEmpty())
                        binding.emptyText.text = getString(R.string.nothing_found)
                    checkEmptyView()
                }, onError))
    }


    private fun loadMore(page: Int, count: Int) {
        if (mLastGitData?.total_count == count) return
        addDisposable(getApi(mLastQuery, page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { binding.scrollProgress.visibility = View.VISIBLE }
                .doFinally { binding.scrollProgress.visibility = View.GONE }
                .subscribe(Consumer<GitData<T>> { gitData ->
                    gitData.items?.let { mData.addAll(it) }
                    rxDataSource.updateDataSet(mData).updateNotifyInsertedAdapter(count, mData.size)
                }, onError))
    }

    private fun checkEmptyView() {
        binding.emptyText.visibility = if (mData.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onBackPressedCaptured(): Boolean {
        if (mSearchView.setSearchFocused(false)) {
            return true
        }
        return super.onBackPressedCaptured()
    }

    private fun getApi(query: String, page: Int = 1): Single<GitData<T>> =
            (if (mType == TYPE_USERS) gitHubApi.getUsers(query, page)
            else gitHubApi.getRepositories(query, page)) as Single<GitData<T>>

    private val onError: Consumer<Throwable> by lazy {
        Consumer<Throwable> { e ->
            Snackbar.make(binding.root, R.string.connection_problem, SNACKBAR_LENGTH)
                    .setAction(R.string.retry) { search(mLastQuery) }.show()
            Log.e(TAG, "Error while getting data", e)
        }
    }


    companion object {
        val TAG = SearchFragment::class.java.simpleName!!

        const val KEY_TYPE = "TYPE"
        const val TYPE_USERS = 0
        const val TYPE_REPOSITORIES = 1

        const val EXTRA_DATA = "data"
        const val EXTRA_IMAGE_TRANSITION_NAME = "transition_name"

        const val SNACKBAR_LENGTH = 6000

        fun newInstance(type: Int): Fragment = newInstance(type, null)

        fun newInstance(type: Int, bundle: Bundle?): Fragment {
            val args = Bundle()
            args.putInt(KEY_TYPE, type)
            bundle?.let { args.putAll(it) }
            val fragment = if (type == TYPE_USERS) SearchFragment<User>() else SearchFragment<Repository>()
            fragment.arguments = args
            return fragment
        }
    }

}
