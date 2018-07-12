package com.github.devjn.githubsearch.ui


import GetPinnedReposQuery
import android.util.Log
import apple.coregraphics.c.CoreGraphics
import apple.foundation.NSBundle
import apple.foundation.NSCoder
import apple.foundation.NSIndexPath
import apple.foundation.NSURL
import apple.uikit.*
import apple.uikit.enums.UIActivityIndicatorViewStyle
import apple.uikit.enums.UIBarButtonItemStyle
import apple.uikit.protocol.UITableViewDataSource
import apple.uikit.protocol.UITableViewDelegate
import com.github.devjn.githubsearch.Main
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.utils.GitHubApi
import com.github.devjn.githubsearch.utils.GithubGraphQL
import com.github.devjn.githubsearch.utils.GithubService
import com.github.devjn.githubsearch.utils.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.ios.schedulers.IOSSchedulers
import io.reactivex.schedulers.Schedulers
import org.moe.bindings.RepoViewCell
import org.moe.bindings.category.UIImageViewExt
import org.moe.natj.general.NatJ
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.NInt
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
protected constructor(peer: Pointer) : UIViewController(peer), UITableViewDelegate, UITableViewDataSource {

    @Selector("init")
    external override fun init(): UserDetailsController

    @Selector("initWithCoder:")
    external override fun initWithCoder(aDecoder: NSCoder): UserDetailsController

    @Selector("initWithNibName:bundle:")
    external override fun initWithNibNameBundle(
            nibNameOrNil: String, nibBundleOrNil: NSBundle): UserDetailsController

    @Selector("setImageView:")
    external fun setImageView(value: UIImageView)

    @Selector("setTextLogin:")
    external fun setTextLogin(value: UITextView)

    @Selector("imageView")
    @Property
    @IBOutlet
    external fun imageView(): UIImageView

    @Selector("infoView")
    @Property
    @IBOutlet
    external fun infoView(): UIStackView

    @Selector("textLogin")
    @Property
    @IBOutlet
    external fun textLogin(): UITextView

    @Selector("textName")
    @Property
    @IBOutlet
    external fun textName(): UITextView

    @Selector("textBio")
    @Property
    @IBOutlet
    external fun textBio(): UITextView

    @Selector("textCompany")
    @Property
    @IBOutlet
    external fun textCompany(): UITextView

    @Selector("textLocation")
    @Property
    @IBOutlet
    external fun textLocation(): UITextView

    @Selector("textEmail")
    @Property
    @IBOutlet
    external fun textEmail(): UITextView

    @Selector("textBlog")
    @Property
    @IBOutlet
    external fun textBlog(): UITextView

    @Selector("textEmpty")
    @Property
    @IBOutlet
    external fun textEmpty(): UITextView

    @Selector("tableView")
    @Property
    @IBOutlet
    external fun tableView(): UITableView

    private val disposables = CompositeDisposable()
    private val repos: ArrayList<GetPinnedReposQuery.Node?> = ArrayList()

    private var source: DataSource = Main.dataSource
    private var isBookmarked = false

    var mUser: User? = null

    override fun viewDidLoad() {
        super.viewDidLoad()
        // This view controller will provide the delegate methods and row data for the table view.
        this.tableView().setDelegate(this)
        this.tableView().setDataSource(this)

        mUser?.let {
            textLogin().setText(it.login)
            val url = NSURL.URLWithString(it.avatar_url)
            UIImageViewExt.sd_setImageWithURLPlaceholderImage(this.imageView(), url, UIImage.imageNamed("User"))

            val gitHubApi = GithubService.createService(GitHubApi::class.java)
            gitHubApi.getUser(it.login).subscribeOn(Schedulers.io())
                    .observeOn(IOSSchedulers.mainThread())
                    .subscribe({ user ->
                        user.isDetailed = true
                        if (user != null) {
                            mUser = user
                            setupUser(user)
                        }
                    }, { e ->
                        Log.e(TAG, "Error while getting data", e)
                    })

            disposables.add(GithubGraphQL.getPinnedRepos(it.login)
                    .subscribeOn(Schedulers.io())
                    .observeOn(IOSSchedulers.mainThread())
                    .map { t -> ArrayList<GetPinnedReposQuery.Node?>(t.size).apply { t.forEach { this.add(it.node()) } } }
                    .subscribe({ list ->
                        if (list.isNotEmpty()) {
                            repos.addAll(list)
                            this.tableView().reloadData()
                            Log.i(TAG, "Loaded repos: " + list.size)
                        }
                    }, { e ->
                        Log.e(TAG, "Error while getting data", e)
                    }))
        } ?: println("mUser is null")

        if (source.getUserById(mUser!!.id) != null)
            isBookmarked = true
        val image = UIImage.imageNamed(if (isBookmarked) "Bookmark" else "NonBookmark")
        val button = UIBarButtonItem.alloc().initWithImageStyleTargetAction(image, UIBarButtonItemStyle.Plain, this, SEL("bookmark:"))
        navigationItem().setRightBarButtonItem(button)
        setupActivityIndicator()
        showProgress(true)
    }


    override fun viewDidUnload() {
        super.viewDidUnload()
        disposables.dispose()
    }


    private fun setupUser(user: User) {
        textName().setText(user.name)
        val toHide = ArrayList<UIView>()
        user.bio?.let { textBio().setText(it) } ?: run { toHide.add(textBio()) }
        user.company?.let { textCompany().setText(it) } ?: run { toHide.add(textCompany()) }
        user.location?.let { textLocation().setText(it) } ?: run { toHide.add(textLocation()) }
        user.email?.let { textEmail().setText(it) } ?: run { toHide.add(textEmail()) }
        user.blog?.let { textBlog().setText(it) } ?: run { toHide.add(textBlog()) }

        if (user.hasExtra()) toHide.add(textEmpty())

        toHide.forEach { it.isHidden = true }

        infoView().setNeedsUpdateConstraints()
        infoView().updateConstraintsIfNeeded()
        infoView().setNeedsLayout()
        infoView().layoutIfNeeded()

        UIView.animateWithDurationAnimations(1.0) {
            infoView().isHidden = false
            showProgress(false)
        }
    }

    private var indicator = UIActivityIndicatorView.alloc()

    private fun setupActivityIndicator() {
        indicator.initWithFrame(CoreGraphics.CGRectMake(0.0, 0.0, 48.0, 48.0))
        indicator.setActivityIndicatorViewStyle(UIActivityIndicatorViewStyle.Gray)
        indicator.setCenter(this.view().center())
        this.view().addSubview(indicator)
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            indicator.startAnimating()
            indicator.setBackgroundColor(UIColor.whiteColor())
        } else {
            indicator.stopAnimating()
            indicator.setHidesWhenStopped(true)
        }
    }


    private fun addToBookmarks() {
        source.createUser(mUser!!)
        isBookmarked = true
        navigationItem().rightBarButtonItem().setImage(UIImage.imageNamed("Bookmark"))
        Log.i(TAG, "adding mUser to bookmarks: $mUser")
    }

    private fun removeFromBookmarks() {
        source.deleteUser(mUser!!.id.toInt())
        isBookmarked = false
        navigationItem().rightBarButtonItem().setImage(UIImage.imageNamed("NonBookmark"))
        Log.i(TAG, "removing mUser from bookmarks: $mUser")
    }

    @Selector("bookmark:")
    fun bookmark() {
        if (!isBookmarked)
            addToBookmarks()
        else removeFromBookmarks()
    }


    override fun tableViewCellForRowAtIndexPath(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
        val cell = tableView.dequeueReusableCellWithIdentifierForIndexPath(CELL_IDENTIFIER, indexPath) as RepoViewCell
        val repo = repos[indexPath.item().toInt()]

        cell.titleLabel().setText(repo?.name())
        cell.descriptionLabel().setText(repo?.description())
        cell.langLabel().setText(repo?.primaryLanguage()?.name())
        cell.contentView().layer().setBorderWidth(1.0)
        cell.contentView().layer().setBorderColor(UIColor.blackColor().CGColor())
        println("cell is build " + indexPath.item())
        return cell
    }

    override fun tableViewNumberOfRowsInSection(tableView: UITableView, @NInt section: Long): Long {
        println("tableViewNumberOfRowsInSection " + repos.size.toLong())
        val size = repos.size.toLong()
        if (size > 0 && tableView().isHidden) {
            UIView.animateWithDurationAnimations(1.0) {
                tableView().isHidden = false
                showProgress(false)
            }
        }
        return size
    }

    override fun tableViewHeightForRowAtIndexPath(tableView: UITableView?, indexPath: NSIndexPath?): Double {
        return 96.0
    }

    override fun numberOfSectionsInTableView(tableView: UITableView?): Long {
        return 1
    }


    companion object {
        private const val CELL_IDENTIFIER = "RepoCell"
        val TAG = UserDetailsController::class.java.simpleName!!

        init {
            NatJ.register()
        }

        @Owned
        @Selector("alloc")
        @JvmStatic
        external fun alloc(): UserDetailsController

        @Selector("initialize")
        external fun initialize()
    }
}
