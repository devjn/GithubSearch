package com.github.devjn.githubsearch.view

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.github.devjn.githubsearch.R
import com.github.devjn.githubsearch.databinding.FragmentMainBinding
import com.github.devjn.githubsearch.databinding.ListItemRepositoryBinding
import com.github.devjn.githubsearch.databinding.ListItemUserBinding
import com.github.devjn.githubsearch.model.entities.GitObject
import com.github.devjn.githubsearch.model.entities.Repository
import com.github.devjn.githubsearch.model.entities.User
import com.github.devjn.githubsearch.utils.*
import com.github.devjn.githubsearch.viewmodel.SearchViewModel
import com.github.devjn.githubsearch.widgets.EndlessRecyclerViewScrollListener
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
class SearchFragment<T : GitObject> : BaseFragment<FragmentMainBinding, SearchViewModel<T>>() {

    private lateinit var mSearchView: FloatingSearchView

    private lateinit var rxDataSource: RxDataSource<T>
    private val onClickSubject = PublishSubject.create<ViewDataBinding>()

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var suggestions: SearchRecentSuggestions

    private var isSearchIntent = false

    override fun provideViewModel(): SearchViewModel<T> = ViewModelProviders.of(this).get(SearchViewModel::class.java) as SearchViewModel<T>
    override fun provideLayoutId() = R.layout.fragment_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            viewModel.type = args.getInt(KEY_TYPE, TYPE_USERS)
            args.getString(SearchManager.QUERY)?.let {
                viewModel.lastQuery = it
                isSearchIntent = true
            }
        }
    }

    override fun setupLayout() {
        mSearchView = binding.floatingSearchView

        val linearLayoutManager = LinearLayoutManager(activity)
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                viewModel.loadMore(page + 1, totalItemsCount, onError)
            }
        }
        binding.list.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(scrollListener)
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
        }

        setupSearchView()
        checkEmptyView()
    }


    private fun setupSearchView() {
        val searchManager = requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        suggestionAdapter = SuggestionAdapter(activity!!, searchManager)

        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                suggestionAdapter.getSuggestions("", 3)?.let { mSearchView.swapSuggestions(it) }
            } else {
                Single.fromCallable { suggestionAdapter.getSuggestions(newQuery, 5) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { mSearchView.showProgress() } //this shows the top left circular progress
                        .doFinally { mSearchView.hideProgress() }
                        //this will swap the data and render the collapse/expand animations as necessary
                        .subscribe({ list -> mSearchView.swapSuggestions(list) },
                                { mSearchView.clearSuggestions(); }
                        ).disp()
            }
        }

        mSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                viewModel.lastQuery = searchSuggestion.body
                search(viewModel.lastQuery)
            }

            override fun onSearchAction(query: String) {
                viewModel.lastQuery = query
                search(query)
                Log.d(TAG, "onSearchAction, query: $query")
            }
        })

        mSearchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {
                //show history when search bar gains focus
                suggestionAdapter.getSuggestions("", 3)?.let { mSearchView.swapSuggestions(it) }
            }

            override fun onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchText(viewModel.lastQuery)
            }
        })

        mSearchView.setOnBindSuggestionCallback { _, leftIcon, _, _, _ ->
            leftIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_history_black_24dp, null))
            leftIcon.alpha = .36f
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        suggestions = SearchRecentSuggestions(context, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
        rxDataSource = RxDataSource(viewModel.data.value)
        val dataSource = if (viewModel.type == TYPE_USERS)
            rxDataSource.bindRecyclerView<ListItemUserBinding>(binding.list, R.layout.list_item_user)
        else rxDataSource.bindRecyclerView<ListItemRepositoryBinding>(binding.list, R.layout.list_item_repository)
        dataSource.subscribe { viewHolder ->
            val b: ViewDataBinding = viewHolder.viewDataBinding
            val data = viewHolder.item
            if (viewModel.type == TYPE_USERS)
                (b as ListItemUserBinding).user = data as User
            else
                (b as ListItemRepositoryBinding).repo = data as Repository
            b.root.setOnClickListener { onClickSubject.onNext(b) }
        }.disp()
        onClickSubject.subscribe { bind ->
            when (viewModel.type) {
                TYPE_REPOSITORIES -> AndroidUtils.startCustomTab(activity!!, (bind as ListItemRepositoryBinding).repo!!.html_url)
                TYPE_USERS -> UserDetailsActivity.start(baseActivity, (bind as ListItemUserBinding).imageUser, bind.user)
            }
        }.disp()

        viewModel.data.observe(this, Observer {
            if (it == null) return@Observer
            rxDataSource.updateDataSet(it).updateAdapter()
            checkEmptyView()
        })

        if (isSearchIntent) {
            isSearchIntent = false
            search(viewModel.lastQuery)
        }
    }

    private fun search(query: String) {
        // reset infinite scroll
        scrollListener.resetState()
        suggestions.saveRecentQuery(query, null)

        viewModel.search(query, onError)
    }

    private fun checkEmptyView() {
        binding.emptyText.text = getString(if (viewModel.lastGitData != null) R.string.nothing_found else
            if (viewModel.type == TYPE_USERS) R.string.find_users else R.string.find_repos)
        binding.emptyText.visibility = if (viewModel.data.value!!.isEmpty()) View.VISIBLE else View.GONE
    }


    override fun onBackPressedCaptured(): Boolean {
        if (mSearchView.setSearchFocused(false)) {
            return true
        }
        return super.onBackPressedCaptured()
    }

    private val onError: Consumer<Throwable> by lazy {
        Consumer<Throwable> { e ->
            Snackbar.make(binding.root, R.string.connection_problem, SNACKBAR_LENGTH)
                    .setAction(R.string.retry) { search(viewModel.lastQuery) }.show()
            Log.e(TAG, "Error while getting data", e)
        }
    }


    companion object {
        val TAG = SearchFragment::class.java.simpleName!!

        const val KEY_TYPE = "TYPE"
        const val TYPE_USERS = 0
        const val TYPE_REPOSITORIES = 1

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
