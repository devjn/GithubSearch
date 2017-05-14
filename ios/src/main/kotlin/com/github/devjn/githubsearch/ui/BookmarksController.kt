package com.github.devjn.githubsearch.ui


import apple.foundation.NSBundle
import apple.foundation.NSCoder
import apple.foundation.NSIndexPath
import apple.uikit.UITableView
import apple.uikit.UITableViewController
import org.moe.natj.general.NatJ
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.*
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

    @Generated
    @Selector("init")
    override external fun init(): BookmarksController

    @Generated
    @Selector("initWithCoder:")
    override external fun initWithCoder(aDecoder: NSCoder): BookmarksController

    @Generated
    @Selector("initWithNibName:bundle:")
    override external fun initWithNibNameBundle(
            nibNameOrNil: String, nibBundleOrNil: NSBundle): BookmarksController

    @Generated
    @Selector("initWithStyle:")
    override external fun initWithStyle(@NInt style: Long): BookmarksController


    override fun viewDidLoad() {
        super.viewDidLoad()
        println("viewDidLoad")
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
        @JvmStatic external fun alloc(): BookmarksController
    }
}