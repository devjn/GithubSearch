package com.github.devjn.githubsearch.ui

import android.util.Log
import apple.coregraphics.c.CoreGraphics.CGRectMake
import apple.foundation.NSOperationQueue
import apple.foundation.enums.NSQualityOfService
import apple.uikit.*
import apple.uikit.enums.NSTextAlignment
import apple.uikit.enums.UIActivityIndicatorViewStyle
import apple.uikit.enums.UITableViewCellSeparatorStyle
import apple.uikit.protocol.UISearchBarDelegate
import apple.uikit.protocol.UISearchControllerDelegate
import apple.uikit.protocol.UISearchResultsUpdating
import com.github.devjn.githubsearch.model.entities.GitData
import com.github.devjn.githubsearch.service.GitHubApi
import com.github.devjn.githubsearch.model.entities.GitObject
import com.github.devjn.githubsearch.service.GithubService
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.ios.schedulers.IOSSchedulers
import io.reactivex.schedulers.Schedulers
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.Generated
import org.moe.natj.general.ann.NInt
import org.moe.natj.objc.ann.Selector

/**
 * Created by @author Jahongir on 13-May-17
 * devjn@jn-arts.com
 * BaseSearchController
 */

abstract class BaseSearchController<T : GitObject> : UITableViewController,
        UISearchControllerDelegate, UISearchResultsUpdating, UISearchBarDelegate {

    @Selector("navigation")
    external fun navigation(): UINavigationItem

    protected lateinit var searchController: UISearchController

    open val TAG = BaseSearchController::class.java.simpleName!!
    val CELL_IDENTIFIER: String
    val type: Int

    protected val data: ArrayList<T> = ArrayList()
    protected val gitHubApi = GithubService.createService(GitHubApi::class.java)

    protected var disposables: CompositeDisposable = CompositeDisposable()
    protected var lastGitData: GitData<T>? = null
    protected var lastQuery = ""

    private var operationQueue: NSOperationQueue

    protected constructor(peer: Pointer, type: Int) : super(peer) {
        this.type = type
        this.CELL_IDENTIFIER = if (type == TYPE_USERS) "userCell" else "repositoryCell"
        operationQueue = NSOperationQueue.alloc().init()
        operationQueue.setQualityOfService(NSQualityOfService.Background)
    }


    protected fun initViews(controller: BaseSearchController<T>) {
        initSearchBar(controller)
        activityIndicator()
    }

    protected fun initSearchBar(controller: BaseSearchController<T>) {
        this.searchController = UISearchController.alloc().initWithSearchResultsController(null)

        this.searchController.setSearchResultsUpdater(controller)
        this.searchController.setDelegate(controller)
        this.searchController.searchBar().setDelegate(controller)

        this.searchController.setHidesNavigationBarDuringPresentation(false)
        this.searchController.setDimsBackgroundDuringPresentation(true)

        this.navigationItem().setTitleView(searchController.searchBar())

        this.setDefinesPresentationContext(true)
    }


    fun search(query: String) {
        val api: Single<GitData<T>> =
                (if (type == TYPE_USERS) gitHubApi.getUsers(query) else gitHubApi.getRepositories(query)) as Single<GitData<T>>
        disposables.add(api.subscribeOn(Schedulers.io())
                .observeOn(IOSSchedulers.mainThread())
                .doOnSubscribe { showProgress(true) }
                .doFinally { showProgress(false) }
                .subscribe({ gitData ->
                    data.clear()
                    gitData.items?.let { data.addAll(it) }
                    lastGitData = gitData
                    this.tableView().reloadData()
                    Log.e(TAG, "Loaded data")
                }, { e -> Log.e(TAG, "Error while getting data", e) }));
    }


    override fun viewDidUnload() {
        disposables.clear()
        super.viewDidUnload()
    }

    override fun tableViewNumberOfRowsInSection(tableView: UITableView, @NInt section: Long) = data.size.toLong()


    @Selector("setNavigation:")
    external fun setNavigation_unsafe(value: UINavigationItem?)

    @Generated
    fun setNavigation(value: UINavigationItem?) {
        val __old = navigation()
        if (value != null) {
            org.moe.natj.objc.ObjCRuntime.associateObjCObject(this, value)
        }
        setNavigation_unsafe(value)
        if (__old != null) {
            org.moe.natj.objc.ObjCRuntime.dissociateObjCObject(this, __old)
        }
    }

    private var indicator = UIActivityIndicatorView.alloc()!!

    fun activityIndicator() {
        indicator.initWithFrame(CGRectMake(0.0, 0.0, 48.0, 48.0))
        indicator.setActivityIndicatorViewStyle(UIActivityIndicatorViewStyle.Gray)
        indicator.setCenter(this.view().center())
        this.view().addSubview(indicator)
    }

    fun showProgress(show: Boolean) {
        if (show) {
            indicator.startAnimating()
            indicator.setBackgroundColor(UIColor.whiteColor())
        } else {
            indicator.stopAnimating()
            indicator.setHidesWhenStopped(true)
        }
    }

    object TableViewHelper {

        fun showEmptyMessage(message: String, viewController: UITableViewController) {
            val messageLabel = UILabel.alloc().initWithFrame(CGRectMake(0.0, 0.0, viewController.view().bounds().size().width(), viewController.view().bounds().size().height()))
            messageLabel.setText(message)
            messageLabel.setTextColor(UIColor.blackColor())
            messageLabel.setNumberOfLines(0)
            messageLabel.setTextAlignment(NSTextAlignment.Center)
//            messageLabel.sizeToFit()

            viewController.tableView().setBackgroundView(messageLabel)
            viewController.tableView().setSeparatorStyle(UITableViewCellSeparatorStyle.None)
        }

        fun restore(viewController: UITableViewController) {
            viewController.tableView().setBackgroundView(null)
            viewController.tableView().setSeparatorStyle(UITableViewCellSeparatorStyle.SingleLine)
        }

    }

    companion object {
        const val TYPE_USERS = 0
        const val TYPE_REPOSITORIES = 1
    }

}