package com.github.devjn.githubsearch.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.github.devjn.githubsearch.R
import com.github.devjn.githubsearch.databinding.FragmentBookmarksBinding
import com.github.devjn.githubsearch.databinding.ListItemUserBinding
import com.github.devjn.githubsearch.model.entities.User
import com.github.devjn.githubsearch.viewmodel.BookmarksViewModel
import com.minimize.android.rxrecycleradapter.RxDataSource
import io.reactivex.subjects.PublishSubject


/**
 * Created by @author Jahongir on 30-Apr-17
 * devjn@jn-arts.com
 * BookmarksFragment
 */
class BookmarksFragment : BaseFragment<FragmentBookmarksBinding, BookmarksViewModel>() {

    private lateinit var rxDataSource: RxDataSource<User>
    private val onClickSubject = PublishSubject.create<ListItemUserBinding>()

    override fun provideViewModel() = ViewModelProviders.of(this).get(BookmarksViewModel::class.java)
    override fun provideLayoutId() = R.layout.fragment_bookmarks

    override fun setupLayout() {
        binding.list.apply {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rxDataSource = RxDataSource(emptyList())
        val dataSource = rxDataSource.bindRecyclerView<ListItemUserBinding>(binding.list, R.layout.list_item_user)
        dataSource.subscribe { viewHolder ->
            viewHolder.viewDataBinding.apply {
                user = viewHolder.item
                root.setOnClickListener { onClickSubject.onNext(this) }
            }
        }.disp()
        onClickSubject.subscribe { bind -> viewModel.onListItemClick(baseActivity, bind) }.disp()

        viewModel.usersLiveData.observe(this, Observer {
            rxDataSource.updateDataSet(it).updateAdapter()
        })
    }


}