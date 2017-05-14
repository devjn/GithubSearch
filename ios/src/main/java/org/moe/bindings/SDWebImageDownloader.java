package org.moe.bindings;


import apple.NSObject;
import apple.foundation.NSArray;
import apple.foundation.NSData;
import apple.foundation.NSError;
import apple.foundation.NSMethodSignature;
import apple.foundation.NSSet;
import apple.foundation.NSURL;
import apple.foundation.NSURLCredential;
import apple.foundation.NSURLSessionConfiguration;
import apple.uikit.UIImage;
import org.moe.natj.c.ann.FunctionPtr;
import org.moe.natj.general.NatJ;
import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Mapped;
import org.moe.natj.general.ann.MappedReturn;
import org.moe.natj.general.ann.NInt;
import org.moe.natj.general.ann.NUInt;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.general.ptr.VoidPtr;
import org.moe.natj.objc.Class;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.SEL;
import org.moe.natj.objc.ann.ObjCBlock;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Selector;
import org.moe.natj.objc.map.ObjCObjectMapper;

@Generated
@Runtime(ObjCRuntime.class)
@ObjCClassName("SDWebImageDownloader")
@RegisterOnStartup
public class SDWebImageDownloader extends NSObject {
	static {
		NatJ.register();
	}

	@Generated
	protected SDWebImageDownloader(Pointer peer) {
		super(peer);
	}

	@Generated
	@Selector("accessInstanceVariablesDirectly")
	public static native boolean accessInstanceVariablesDirectly();

	@Generated
	@Owned
	@Selector("alloc")
	public static native SDWebImageDownloader alloc();

	@Generated
	@Selector("allocWithZone:")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object allocWithZone(VoidPtr zone);

	@Generated
	@Selector("automaticallyNotifiesObserversForKey:")
	public static native boolean automaticallyNotifiesObserversForKey(String key);

	@Generated
	@Selector("cancel:")
	public native void cancel(SDWebImageDownloadToken token);

	@Generated
	@Selector("cancelAllDownloads")
	public native void cancelAllDownloads();

	@Generated
	@Selector("cancelPreviousPerformRequestsWithTarget:")
	public static native void cancelPreviousPerformRequestsWithTarget(
			@Mapped(ObjCObjectMapper.class) Object aTarget);

	@Generated
	@Selector("cancelPreviousPerformRequestsWithTarget:selector:object:")
	public static native void cancelPreviousPerformRequestsWithTargetSelectorObject(
			@Mapped(ObjCObjectMapper.class) Object aTarget, SEL aSelector,
			@Mapped(ObjCObjectMapper.class) Object anArgument);

	@Generated
	@Selector("class")
	public static native Class class_objc_static();

	@Generated
	@Selector("classFallbacksForKeyedArchiver")
	public static native NSArray<String> classFallbacksForKeyedArchiver();

	@Generated
	@Selector("classForKeyedUnarchiver")
	public static native Class classForKeyedUnarchiver();

	@Generated
	@Selector("currentDownloadCount")
	@NUInt
	public native long currentDownloadCount();

	@Generated
	@Selector("debugDescription")
	public static native String debugDescription_static();

	@Generated
	@Selector("description")
	public static native String description_static();

	@Generated
	@Selector("downloadImageWithURL:options:progress:completed:")
	public native SDWebImageDownloadToken downloadImageWithURLOptionsProgressCompleted(
			NSURL url,
			@NUInt long options,
			@ObjCBlock(name = "call_downloadImageWithURLOptionsProgressCompleted_2") Block_downloadImageWithURLOptionsProgressCompleted_2 progressBlock,
			@ObjCBlock(name = "call_downloadImageWithURLOptionsProgressCompleted_3") Block_downloadImageWithURLOptionsProgressCompleted_3 completedBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_downloadImageWithURLOptionsProgressCompleted_2 {
		@Generated
		void call_downloadImageWithURLOptionsProgressCompleted_2(
				@NInt long arg0, @NInt long arg1, NSURL arg2);
	}

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_downloadImageWithURLOptionsProgressCompleted_3 {
		@Generated
		void call_downloadImageWithURLOptionsProgressCompleted_3(UIImage arg0,
				NSData arg1, NSError arg2, boolean arg3);
	}

	@Generated
	@Selector("downloadTimeout")
	public native double downloadTimeout();

	@Generated
	@Selector("executionOrder")
	@NInt
	public native long executionOrder();

	@Generated
	@Selector("hash")
	@NUInt
	public static native long hash_static();

	@Generated
	@Selector("init")
	public native SDWebImageDownloader init();

	@Generated
	@Selector("initWithSessionConfiguration:")
	public native SDWebImageDownloader initWithSessionConfiguration(
			NSURLSessionConfiguration sessionConfiguration);

	@Generated
	@Selector("initialize")
	public static native void initialize();

	@Generated
	@Selector("instanceMethodForSelector:")
	@FunctionPtr(name = "call_instanceMethodForSelector_ret")
	public static native NSObject.Function_instanceMethodForSelector_ret instanceMethodForSelector(
			SEL aSelector);

	@Generated
	@Selector("instanceMethodSignatureForSelector:")
	public static native NSMethodSignature instanceMethodSignatureForSelector(
			SEL aSelector);

	@Generated
	@Selector("instancesRespondToSelector:")
	public static native boolean instancesRespondToSelector(SEL aSelector);

	@Generated
	@Selector("isSubclassOfClass:")
	public static native boolean isSubclassOfClass(Class aClass);

	@Generated
	@Selector("keyPathsForValuesAffectingValueForKey:")
	public static native NSSet<String> keyPathsForValuesAffectingValueForKey(
			String key);

	@Generated
	@Selector("load")
	public static native void load_objc_static();

	@Generated
	@Selector("maxConcurrentDownloads")
	@NInt
	public native long maxConcurrentDownloads();

	@Generated
	@Owned
	@Selector("new")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object new_objc();

	@Generated
	@Selector("password")
	public native String password();

	@Generated
	@Selector("resolveClassMethod:")
	public static native boolean resolveClassMethod(SEL sel);

	@Generated
	@Selector("resolveInstanceMethod:")
	public static native boolean resolveInstanceMethod(SEL sel);

	@Generated
	@Selector("setDownloadTimeout:")
	public native void setDownloadTimeout(double value);

	@Generated
	@Selector("setExecutionOrder:")
	public native void setExecutionOrder(@NInt long value);

	@Generated
	@Selector("setMaxConcurrentDownloads:")
	public native void setMaxConcurrentDownloads(@NInt long value);

	@Generated
	@Selector("setOperationClass:")
	public native void setOperationClass(Class operationClass);

	@Generated
	@Selector("setPassword:")
	public native void setPassword(String value);

	@Generated
	@Selector("setShouldDecompressImages:")
	public native void setShouldDecompressImages(boolean value);

	@Generated
	@Selector("setSuspended:")
	public native void setSuspended(boolean suspended);

	@Generated
	@Selector("setUrlCredential:")
	public native void setUrlCredential(NSURLCredential value);

	@Generated
	@Selector("setUsername:")
	public native void setUsername(String value);

	@Generated
	@Selector("setValue:forHTTPHeaderField:")
	public native void setValueForHTTPHeaderField(String value, String field);

	@Generated
	@Selector("setVersion:")
	public static native void setVersion(@NInt long aVersion);

	@Generated
	@Selector("sharedDownloader")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object sharedDownloader();

	@Generated
	@Selector("shouldDecompressImages")
	public native boolean shouldDecompressImages();

	@Generated
	@Selector("superclass")
	public static native Class superclass_static();

	@Generated
	@Selector("urlCredential")
	public native NSURLCredential urlCredential();

	@Generated
	@Selector("username")
	public native String username();

	@Generated
	@Selector("valueForHTTPHeaderField:")
	public native String valueForHTTPHeaderField(String field);

	@Generated
	@Selector("version")
	@NInt
	public static native long version_static();
}