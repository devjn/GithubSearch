package com.github.devjn.githubsearch.ui


import apple.foundation.NSDictionary
import apple.foundation.NSIndexPath
import apple.foundation.NSProcessInfo
import apple.foundation.NSURL
import apple.foundation.struct.NSOperatingSystemVersion
import apple.uikit.*
import apple.uikit.protocol.UISearchBarDelegate
import apple.uikit.protocol.UISearchControllerDelegate
import apple.uikit.protocol.UISearchResultsUpdating
import com.github.devjn.githubsearch.model.entities.Repository
import org.moe.natj.general.NatJ
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.NInt
import org.moe.natj.general.ann.Owned
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.general.ann.Runtime
import org.moe.natj.objc.ObjCRuntime
import org.moe.natj.objc.ann.ObjCClassName
import org.moe.natj.objc.ann.Selector

/**
 * Created by @author Jahongir on 13-May-17
 * devjn@jn-arts.com
 * RepositorySearchController
 */

@Runtime(ObjCRuntime::class)
@ObjCClassName("RepositorySearchController")
@RegisterOnStartup
class RepositorySearchController
protected constructor(peer: Pointer) : BaseSearchController<Repository>(peer, BaseSearchController.TYPE_REPOSITORIES),
        UISearchControllerDelegate, UISearchResultsUpdating, UISearchBarDelegate {

    @Selector("init")
    override external fun init(): RepositorySearchController

    override fun viewDidLoad() {
        super.viewDidLoad()
        initViews(this)
    }

    override fun updateSearchResultsForSearchController(searchController: UISearchController?) {
        //TODO: implement search suggestions
    }

    override fun searchBarSearchButtonClicked(searchBar: UISearchBar) {
        val query = searchBar.text()
        if (query != null && query.isNotEmpty()) {
            println(query)
            search(query)
        }
    }

    override fun tableViewCellForRowAtIndexPath(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
        val cell = tableView.dequeueReusableCellWithIdentifierForIndexPath(CELL_IDENTIFIER, indexPath)
        val repo = data.get(indexPath.item().toInt())

        cell.textLabel().setText(repo.full_name)
        cell.detailTextLabel().setText(repo.description)
        return cell
    }


    override fun tableViewNumberOfRowsInSection(tableView: UITableView, @NInt section: Long): Long {
        return data.size.toLong()
    }

    override fun numberOfSectionsInTableView(tableView: UITableView?): Long {
        if (data.size > 0) {
            TableViewHelper.restore(this)
            return 1
        } else {
            TableViewHelper.showEmptyMessage("Look up for repositories on Github", this)
            return 0
        }
    }

    override fun tableViewDidSelectRowAtIndexPath(tableView: UITableView?, indexPath: NSIndexPath?) {
        val repo = data.get(indexPath!!.item().toInt())
        open(repo.html_url)
    }

    fun open(scheme: String) {
        val url = NSURL.URLWithString(scheme)
        if (NSProcessInfo.alloc().init().isOperatingSystemAtLeastVersion(NSOperatingSystemVersion(10L, 0L, 0L))) {
            println("iOS >= 10.0.0")
            UIApplication.sharedApplication().
                    openURLOptionsCompletionHandler(url, NSDictionary.dictionary<String, Any?>() as NSDictionary<String, *>?, { success -> print("Open ${scheme}: $success)") })
        } else {
            val success = UIApplication.sharedApplication().openURL(url)
            print("Open $scheme: $success")
        }
    }

    companion object {
        init {
            NatJ.register()
        }

        @Owned
        @Selector("alloc")
        @JvmStatic external fun alloc(): RepositorySearchController

        @Selector("initialize")
        external fun initialize()
    }
}