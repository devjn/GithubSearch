package com.github.devjn.githubsearch.ui


import android.util.Log
import apple.foundation.NSBundle
import apple.foundation.NSCoder
import apple.foundation.NSURL
import apple.uikit.*
import apple.uikit.enums.UIBarButtonItemStyle
import com.github.devjn.githubsearch.Main
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.utils.User
import org.moe.bindings.category.UIImageViewExt
import org.moe.natj.general.NatJ
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.Owned
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.general.ann.Runtime
import org.moe.natj.objc.ObjCRuntime
import org.moe.natj.objc.SEL
import org.moe.natj.objc.ann.IBOutlet
import org.moe.natj.objc.ann.ObjCClassName
import org.moe.natj.objc.ann.Property
import org.moe.natj.objc.ann.Selector

/**
 * Created by @author Jahongir on 13-May-17
 * devjn@jn-arts.com
 * UserDetailsController
 */
@Runtime(ObjCRuntime::class)
@ObjCClassName("UserDetailsController")
@RegisterOnStartup
class UserDetailsController
protected constructor(peer: Pointer) : UIViewController(peer) {

    @Selector("init")
    override external fun init(): UserDetailsController

    @Selector("initWithCoder:")
    override external fun initWithCoder(aDecoder: NSCoder): UserDetailsController

    @Selector("initWithNibName:bundle:")
    override external fun initWithNibNameBundle(
            nibNameOrNil: String, nibBundleOrNil: NSBundle): UserDetailsController

    @Selector("imageView")
    @Property
    @IBOutlet
    external fun imageView(): UIImageView

    @Selector("setImageView:")
    external fun setImageView(value: UIImageView)

    @Selector("setTextLogin:")
    external fun setTextLogin(value: UITextView)

    @Selector("textLogin")
    @Property
    @IBOutlet
    external fun textLogin(): UITextView

    val TAG = UserDetailsController::class.simpleName

    private var source: DataSource = Main.dataSource
    private var isBookmarked = false

    var user: User? = null

    override fun viewDidLoad() {
        super.viewDidLoad()
        println("user viewDidLoad")
        user?.let {
            textLogin().setText(it.login)
            val url = NSURL.URLWithString(it.avatar_url)
            UIImageViewExt.sd_setImageWithURLPlaceholderImage(this.imageView(), url, UIImage.imageNamed("User"))
        } ?: print("user is null")
//        source = DataSource(SQLiteDatabaseHelper())
//        source.open()
        if (source.getUserById(user!!.id) != null)
            isBookmarked = true
        val image = UIImage.imageNamed(if (isBookmarked) "Bookmark" else "NonBookmark")
        val button = UIBarButtonItem.alloc().initWithImageStyleTargetAction(image, UIBarButtonItemStyle.Plain, this, SEL("bookmark:"))
        navigationItem().setRightBarButtonItem(button)
    }

//    override fun viewWillDisappear(animated: Boolean) {
//        super.viewWillDisappear(animated)
//        source.close()
//    }

    fun addToBookmarks() {
        source.createUser(user!!)
        isBookmarked = true
        navigationItem().rightBarButtonItem().setImage(UIImage.imageNamed("Bookmark"))
        Log.i(TAG, "adding user to bookmarks: " + user)
    }

    fun removeFromBookmarks() {
        source.deleteUser(user!!.id.toInt())
        isBookmarked = false
        navigationItem().rightBarButtonItem().setImage(UIImage.imageNamed("NonBookmark"))
        Log.i(TAG, "removing user from bookmarks: " + user)
    }

    @Selector("bookmark:")
    fun bookmark() {
        println("user bookmark")
        if (!isBookmarked)
            addToBookmarks()
        else removeFromBookmarks()
    }


    companion object {
        init {
            NatJ.register()
        }

        @Owned
        @Selector("alloc")
        @JvmStatic external fun alloc(): UserDetailsController

        @Selector("initialize")
        external fun initialize()
    }
}