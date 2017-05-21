package com.github.devjn.githubsearch.ui


import apple.foundation.NSIndexPath
import apple.foundation.NSURL
import apple.uikit.*
import apple.uikit.enums.UITableViewCellStyle
import apple.uikit.protocol.UISearchBarDelegate
import apple.uikit.protocol.UISearchControllerDelegate
import apple.uikit.protocol.UISearchResultsUpdating
import com.github.devjn.githubsearch.utils.User
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
 * UserSearchController
 */
@Runtime(ObjCRuntime::class)
@ObjCClassName("UserSearchController")
@RegisterOnStartup
class UserSearchController(peer: Pointer) : BaseSearchController<User>(peer, BaseSearchController.TYPE_USERS),
        UISearchControllerDelegate, UISearchResultsUpdating, UISearchBarDelegate {

    @Selector("init")
    override external fun init(): UserSearchController

    override val TAG = UserSearchController::class.simpleName

    override fun viewDidLoad() {
        super.viewDidLoad()
        initViews(this)
    }

    override fun updateSearchResultsForSearchController(searchController: UISearchController?) {

    }

    override fun searchBarSearchButtonClicked(searchBar: UISearchBar) {
        val query = searchBar.text()
        if (query != null && query.isNotEmpty()) {
            println(query)
            search(query)
        }
    }

    override fun prepareForSegueSender(segue: UIStoryboardSegue?, sender: Any?) {
        println("prepareForSegueSender")
//        if(segue?.identifier()!!.equals("showUserDetail")) {
        val index = this.tableView().indexPathForSelectedRow().item()
        val user = mData.get(index.toInt())
        val destViewController = segue!!.destinationViewController() as UserDetailsController
        destViewController.mUser = user
        print("mUser prepareForSegueSender")
//        }
//        else super.prepareForSegueSender(segue, sender)
    }

    override fun tableViewCellForRowAtIndexPath(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
        var cell = tableView.dequeueReusableCellWithIdentifierForIndexPath(CELL_IDENTIFIER, indexPath)
        val user = mData.get(indexPath.item().toInt())
        println("--- tableViewCellForRowAtIndexPath " + indexPath.item());

        if (cell == null) {
            cell = UITableViewCell.alloc().initWithStyleReuseIdentifier(UITableViewCellStyle.Default, CELL_IDENTIFIER)
        }
        cell.textLabel().setText(user.login)
        val url = NSURL.URLWithString(user.avatar_url)
        UIImageViewExt.sd_setImageWithURLPlaceholderImage(cell.imageView(), url, UIImage.imageNamed("User"))
//        ,UIImageViewExt.Block_sd_setImageWithURLPlaceholderImageCompleted { image, arg1, arg2, arg3 ->  cell.setNeedsLayout() })
//        cell.imageView().setTransform(CGAffineTransformMakeScale(0.5, 0.5))
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
            BaseSearchController.TableViewHelper.showEmptyMessage("Look up for users on Github", this)
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

        init {
            NatJ.register()
        }

        @Owned
        @Selector("alloc")
        @JvmStatic external fun alloc(): UserSearchController

        @Selector("initialize")
        external fun initialize()

        @Selector("setVersion:")
        external fun setVersion(@NInt aVersion: Long)
    }

}