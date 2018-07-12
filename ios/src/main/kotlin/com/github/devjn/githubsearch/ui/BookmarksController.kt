package com.github.devjn.githubsearch.ui


import android.util.Log
import apple.coregraphics.c.CoreGraphics
import apple.foundation.NSBundle
import apple.foundation.NSCoder
import apple.foundation.NSIndexPath
import apple.foundation.NSURL
import apple.uikit.*
import apple.uikit.enums.UIActivityIndicatorViewStyle
import apple.uikit.enums.UITableViewCellStyle
import com.github.devjn.githubsearch.Main
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.utils.GitHubApi
import com.github.devjn.githubsearch.utils.GithubService
import com.github.devjn.githubsearch.utils.User
import io.reactivex.Observable
import io.reactivex.ios.schedulers.IOSSchedulers
import io.reactivex.schedulers.Schedulers
import org.moe.bindings.category.UIImageViewExt
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
 * BookmarksController
 */

@Runtime(ObjCRuntime::class)
@ObjCClassName("BookmarksController")
@RegisterOnStartup
class BookmarksController
protected constructor(peer: Pointer) : UITableViewController(peer) {

    @Selector("init")
    override external fun init(): BookmarksController

    @Selector("initWithCoder:")
    override external fun initWithCoder(aDecoder: NSCoder): BookmarksController

    @Selector("initWithNibName:bundle:")
    override external fun initWithNibNameBundle(
            nibNameOrNil: String, nibBundleOrNil: NSBundle): BookmarksController

    @Selector("initWithStyle:")
    override external fun initWithStyle(@NInt style: Long): BookmarksController

    private val mData: ArrayList<User> = ArrayList()
    private val gitHubApi = GithubService.createService(GitHubApi::class.java)
    private var source: DataSource = Main.dataSource


    override fun viewDidLoad() {
        super.viewDidLoad()
        println("BookmarksController viewDidLoad")
        activityIndicator()
    }

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
//        source = DataSource(SQLiteDatabaseHelper())
//        source.open()
        Observable.fromCallable { getBookmarks() }.subscribeOn(Schedulers.io())
                .observeOn(IOSSchedulers.mainThread())
                .subscribe({ list ->
                    mData.clear()
                    list?.let { mData.addAll(it) }
                    this.tableView().reloadData()
                    showProgress(false)
                    Log.e(TAG, "Loaded data")
                }, { e ->
                    showProgress(false)
                    Log.e(TAG, "Error while getting data", e)
                })
    }

//    override fun viewWillDisappear(animated: Boolean) {
//        super.viewWillDisappear(animated)
//        source.close()
//    }

    private fun getBookmarks() = source.allUsers

    var indicator = UIActivityIndicatorView.alloc()

    fun activityIndicator() {
        indicator.initWithFrame(CoreGraphics.CGRectMake(0.0, 0.0, 48.0, 48.0))
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

    override fun prepareForSegueSender(segue: UIStoryboardSegue?, sender: Any?) {
        print(TAG + " prepareForSegueSender")
        if (segue?.identifier()!!.equals("showUserDetail")) {
            val index = this.tableView().indexPathForSelectedRow().item()
            val user = mData.get(index.toInt())
            val destViewController = segue.destinationViewController() as UserDetailsController
            destViewController.mUser = user
            print("mUser prepareForSegueSender")
        } else super.prepareForSegueSender(segue, sender)
    }

    override fun tableViewCellForRowAtIndexPath(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
        var cell = tableView.dequeueReusableCellWithIdentifierForIndexPath(CELL_IDENTIFIER, indexPath)
        val user = mData.get(indexPath.item().toInt())

        if (cell == null) {
            cell = UITableViewCell.alloc().initWithStyleReuseIdentifier(UITableViewCellStyle.Default, CELL_IDENTIFIER)
        }
        cell.textLabel().setText(user.login)
        val url = NSURL.URLWithString(user.avatar_url)
        UIImageViewExt.sd_setImageWithURLPlaceholderImage(cell.imageView(), url, UIImage.imageNamed("User"))
        return cell
    }

    override fun tableViewNumberOfRowsInSection(tableView: UITableView, @NInt section: Long): Long {
        return mData.size.toLong()
    }

    override fun numberOfSectionsInTableView(tableView: UITableView?): Long {
        if (mData.size > 0) {
            BaseSearchController.TableViewHelper.restore(this)
            return 1
        } else {
            BaseSearchController.TableViewHelper.showEmptyMessage("No bookmarks yet", this)
            return 0
        }
    }

    override fun tableViewDidSelectRowAtIndexPath(tableView: UITableView?, indexPath: NSIndexPath?) {
//        val storyBoard : UIStoryboard = UIStoryboard.storyboardWithNameBundle("Storyboard", null)
//
//        val nextViewController = storyBoard.instantiateViewControllerWithIdentifier("UserDetails") as UserDetailsController
//        this.presentViewControllerAnimatedCompletion(nextViewController, true, null)
    }

    companion object {
        private const val CELL_IDENTIFIER = "bookmarkCell"

        val TAG = BookmarksController::class.java.simpleName!!

        init {
            NatJ.register()
        }

        @Owned
        @Selector("alloc")
        @JvmStatic
        external fun alloc(): BookmarksController
    }
}