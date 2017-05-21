package org.moe.bindings.category;


import apple.uikit.UIScrollView;
import org.moe.bindings.SVInfiniteScrollingView;
import org.moe.bindings.SVPullToRefreshView;
import org.moe.natj.general.NatJ;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCBlock;
import org.moe.natj.objc.ann.ObjCCategory;
import org.moe.natj.objc.ann.Selector;
import org.moe.natj.general.ann.Library;

@Generated
@Runtime(ObjCRuntime.class)
@ObjCCategory(UIScrollView.class)
public final class UIScrollViewExt {
	static {
		NatJ.register();
	}

	@Generated
	private UIScrollViewExt() {
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_addInfiniteScrollingWithActionHandler {
		@Generated
		void call_addInfiniteScrollingWithActionHandler();
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_addPullToRefreshWithActionHandler {
		@Generated
		void call_addPullToRefreshWithActionHandler();
	}

	@Generated
	@Selector("addInfiniteScrollingWithActionHandler:")
	public static native void addInfiniteScrollingWithActionHandler(
			UIScrollView _object,
			@ObjCBlock(name = "call_addInfiniteScrollingWithActionHandler") Block_addInfiniteScrollingWithActionHandler actionHandler);

	@Generated
	@Selector("addPullToRefreshWithActionHandler:")
	public static native void addPullToRefreshWithActionHandler(
			UIScrollView _object,
			@ObjCBlock(name = "call_addPullToRefreshWithActionHandler") Block_addPullToRefreshWithActionHandler actionHandler);

	@Generated
	@Selector("infiniteScrollingView")
	public static native SVInfiniteScrollingView infiniteScrollingView(
			UIScrollView _object);

	@Generated
	@Selector("pullToRefreshView")
	public static native SVPullToRefreshView pullToRefreshView(
			UIScrollView _object);

	@Generated
	@Selector("setShowsInfiniteScrolling:")
	public static native void setShowsInfiniteScrolling(UIScrollView _object,
			boolean value);

	@Generated
	@Selector("setShowsPullToRefresh:")
	public static native void setShowsPullToRefresh(UIScrollView _object,
			boolean value);

	@Generated
	@Selector("showsInfiniteScrolling")
	public static native boolean showsInfiniteScrolling(UIScrollView _object);

	@Generated
	@Selector("showsPullToRefresh")
	public static native boolean showsPullToRefresh(UIScrollView _object);

	@Generated
	@Selector("triggerInfiniteScrolling")
	public static native void triggerInfiniteScrolling(UIScrollView _object);

	@Generated
	@Selector("triggerPullToRefresh")
	public static native void triggerPullToRefresh(UIScrollView _object);
}