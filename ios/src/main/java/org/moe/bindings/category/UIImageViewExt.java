package org.moe.bindings.category;


import apple.foundation.NSArray;
import apple.foundation.NSError;
import apple.foundation.NSURL;
import apple.uikit.UIImage;
import apple.uikit.UIImageView;
import org.moe.natj.general.NatJ;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.NInt;
import org.moe.natj.general.ann.NUInt;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCBlock;
import org.moe.natj.objc.ann.ObjCCategory;
import org.moe.natj.objc.ann.Selector;

@Generated
@Runtime(ObjCRuntime.class)
@ObjCCategory(UIImageView.class)
public final class UIImageViewExt {
	static {
		NatJ.register();
	}

	@Generated
	private UIImageViewExt() {
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_3 {
		@Generated
		void call_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_3(
				@NInt long arg0, @NInt long arg1, NSURL arg2);
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_4 {
		@Generated
		void call_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_4(
				UIImage arg0, NSError arg1, @NInt long arg2, NSURL arg3);
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_sd_setImageWithURLCompleted {
		@Generated
		void call_sd_setImageWithURLCompleted(UIImage arg0, NSError arg1,
				@NInt long arg2, NSURL arg3);
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_sd_setImageWithURLPlaceholderImageCompleted {
		@Generated
		void call_sd_setImageWithURLPlaceholderImageCompleted(UIImage arg0,
				NSError arg1, @NInt long arg2, NSURL arg3);
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_sd_setImageWithURLPlaceholderImageOptionsCompleted {
		@Generated
		void call_sd_setImageWithURLPlaceholderImageOptionsCompleted(
				UIImage arg0, NSError arg1, @NInt long arg2, NSURL arg3);
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_3 {
		@Generated
		void call_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_3(
				@NInt long arg0, @NInt long arg1, NSURL arg2);
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_4 {
		@Generated
		void call_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_4(
				UIImage arg0, NSError arg1, @NInt long arg2, NSURL arg3);
	}

	@Generated
	@Selector("sd_cancelCurrentAnimationImagesLoad")
	public static native void sd_cancelCurrentAnimationImagesLoad(
			UIImageView _object);

	@Generated
	@Selector("sd_setAnimationImagesWithURLs:")
	public static native void sd_setAnimationImagesWithURLs(
			UIImageView _object, NSArray<? extends NSURL> arrayOfURLs);

	@Generated
	@Selector("sd_setImageWithPreviousCachedImageWithURL:placeholderImage:options:progress:completed:")
	public static native void sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted(
			UIImageView _object,
			NSURL url,
			UIImage placeholder,
			@NUInt long options,
			@ObjCBlock(name = "call_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_3") Block_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_3 progressBlock,
			@ObjCBlock(name = "call_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_4") Block_sd_setImageWithPreviousCachedImageWithURLPlaceholderImageOptionsProgressCompleted_4 completedBlock);

	@Generated
	@Selector("sd_setImageWithURL:")
	public static native void sd_setImageWithURL(UIImageView _object, NSURL url);

	@Generated
	@Selector("sd_setImageWithURL:completed:")
	public static native void sd_setImageWithURLCompleted(
			UIImageView _object,
			NSURL url,
			@ObjCBlock(name = "call_sd_setImageWithURLCompleted") Block_sd_setImageWithURLCompleted completedBlock);

	@Generated
	@Selector("sd_setImageWithURL:placeholderImage:")
	public static native void sd_setImageWithURLPlaceholderImage(
			UIImageView _object, NSURL url, UIImage placeholder);

	@Generated
	@Selector("sd_setImageWithURL:placeholderImage:completed:")
	public static native void sd_setImageWithURLPlaceholderImageCompleted(
			UIImageView _object,
			NSURL url,
			UIImage placeholder,
			@ObjCBlock(name = "call_sd_setImageWithURLPlaceholderImageCompleted") Block_sd_setImageWithURLPlaceholderImageCompleted completedBlock);

	@Generated
	@Selector("sd_setImageWithURL:placeholderImage:options:")
	public static native void sd_setImageWithURLPlaceholderImageOptions(
			UIImageView _object, NSURL url, UIImage placeholder,
			@NUInt long options);

	@Generated
	@Selector("sd_setImageWithURL:placeholderImage:options:completed:")
	public static native void sd_setImageWithURLPlaceholderImageOptionsCompleted(
			UIImageView _object,
			NSURL url,
			UIImage placeholder,
			@NUInt long options,
			@ObjCBlock(name = "call_sd_setImageWithURLPlaceholderImageOptionsCompleted") Block_sd_setImageWithURLPlaceholderImageOptionsCompleted completedBlock);

	@Generated
	@Selector("sd_setImageWithURL:placeholderImage:options:progress:completed:")
	public static native void sd_setImageWithURLPlaceholderImageOptionsProgressCompleted(
			UIImageView _object,
			NSURL url,
			UIImage placeholder,
			@NUInt long options,
			@ObjCBlock(name = "call_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_3") Block_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_3 progressBlock,
			@ObjCBlock(name = "call_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_4") Block_sd_setImageWithURLPlaceholderImageOptionsProgressCompleted_4 completedBlock);
}