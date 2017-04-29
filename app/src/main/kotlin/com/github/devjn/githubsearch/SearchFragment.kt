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
import android.support.v7.widget.*
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.devjn.githubsearch.databinding.FragmentMainBinding
import com.github.devjn.githubsearch.databinding.ListItemRepositoryBinding
import com.github.devjn.githubsearch.databinding.ListItemUserBinding
import com.github.devjn.githubsearch.utils.*
import com.minimize.android.rxrecycleradapter.RxDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


@android.databinding.BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url).into(imageView)
}

@Suppress("UNCHECKED_CAST")
/**
 * Created by @author Jahongir on 27-Apr-17
 * devjn@jn-arts.com
 * SearchFragment
 */
class SearchFragment<T : GitObject> : BaseFragment() {

    val TAG = SearchFragment::class.simpleName

    private var mData: ArrayList<T> = ArrayList<T>()
    private var mType: Int = TYPE_USERS

    private lateinit var binding: FragmentMainBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mSearchView: SearchView

    private lateinit var rxDataSource: RxDataSource<T>
    private val onClickSubject = PublishSubject.create<ViewDataBinding>()

    private lateinit var suggestions: SearchRecentSuggestions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

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
        binding.emptyText.text = getString(if (mType == TYPE_USERS) R.string.find_users else R.string.find_repos)
        checkEmptyView()
        return binding.root
    }

    private fun setupSearchView() {
        val searchManager: SearchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager;
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName));

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                Log.d(TAG, "onSearchAction, query: $query")
                suggestions.saveRecentQuery(query, null);
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                adapter.getFilter().filter(newText)
                return false
            }
        })
//        mSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
//            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
//
//            }
//
//            override fun onSearchAction(query: String) {
//                search(query)
//                Log.d(TAG, "onSearchAction, query: $query")
//            }
//        })
    }

    lateinit var searchItem: MenuItem

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu);
        val searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            mSearchView = searchItem.actionView as SearchView;
        }
        if (mSearchView != null) {
            setupSearchView()
//            searchItem.expandActionView();
//            this.searchItem = searchItem;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_search ->
                // Not implemented here
                return false
        }
        return super.onOptionsItemSelected(item)
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
            intent.putExtra(EXTRA_DATA, bind.user);
            intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, getTransitionName(imageView));
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    imageView, ViewCompat.getTransitionName(imageView));
            startActivity(intent, options.toBundle());
        }
        suggestions = SearchRecentSuggestions(context, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
//        baseActivity.findViewById(R.id.search_action)?.setOnClickListener {
//            searchItem.expandActionView()
//        }
    }


    private fun search(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        val gitHubApi = GithubService.createService(GitHubApi::class.java)
        val api: Observable<GitData<T>> = if (mType == TYPE_USERS) gitHubApi.getUsers(query) as Observable<GitData<T>>
        else gitHubApi.getRepositories(query) as Observable<GitData<T>>
        api.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ users ->
                    mData.clear()
                    users.items?.let { mData.addAll(it) }
                    rxDataSource.updateDataSet(mData).updateAdapter()
                    binding.progressBar.visibility = View.GONE
                    checkEmptyView()
                }, { e ->
                    Snackbar.make(baseActivity.getRoot(), "Cannot connect", Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, { search(query) }).show()
                    binding.progressBar.visibility = View.GONE
                    Log.e(TAG, "Error fetching data: " + e)
                })
    }

    private fun checkEmptyView() {
        if (mData.isEmpty())
            binding.emptyText.visibility = View.VISIBLE
        else binding.emptyText.visibility = View.GONE
    }

    override fun onBackPressedCaptured(): Boolean {
//        if (!mSearchView.setSearchFocused(false)) {
//            return false
//        }
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
