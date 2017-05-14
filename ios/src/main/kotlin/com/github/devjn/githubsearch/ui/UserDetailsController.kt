package com.github.devjn.githubsearch.ui


import apple.foundation.NSBundle
import apple.foundation.NSCoder
import apple.foundation.NSURL
import apple.uikit.*
import apple.uikit.enums.UIBarButtonSystemItem
import com.github.devjn.githubsearch.db.SQLiteDatabaseHelper
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.utils.User
import org.moe.bindings.category.UIImageViewExt
import org.moe.natj.general.NatJ
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.Owned
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.general.ann.Runtime
import org.moe.natj.objc.ObjCRuntime
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

    var user: User? = null

    override fun viewDidLoad() {
        super.viewDidLoad()
        print("user viewDidLoad")
        user?.let {
            textLogin().setText(it.login)
            val url = NSURL.URLWithString(it.avatar_url)
            UIImageViewExt.sd_setImageWithURLPlaceholderImage(this.imageView(), url, UIImage.imageNamed("User"))
        } ?: print("user is null")
        val button = UIBarButtonItem.alloc().initWithBarButtonSystemItemTargetAction (UIBarButtonSystemItem.Bookmarks, this, null)
        navigationItem().setRightBarButtonItem(button)
    }

    fun addToBookmarks() {
        val source = DataSource(SQLiteDatabaseHelper())
        source.open()
        source.createUser(user!!)
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