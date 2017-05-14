package org.moe.bindings;


import apple.NSObject;
import apple.foundation.NSArray;
import apple.foundation.NSData;
import apple.foundation.NSMethodSignature;
import apple.foundation.NSOperation;
import apple.foundation.NSSet;
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
@ObjCClassName("SDImageCache")
@RegisterOnStartup
public class SDImageCache extends NSObject {
	static {
		NatJ.register();
	}

	@Generated
	protected SDImageCache(Pointer peer) {
		super(peer);
	}

	@Generated
	@Selector("accessInstanceVariablesDirectly")
	public static native boolean accessInstanceVariablesDirectly();

	@Generated
	@Selector("addReadOnlyCachePath:")
	public native void addReadOnlyCachePath(String path);

	@Generated
	@Owned
	@Selector("alloc")
	public static native SDImageCache alloc();

	@Generated
	@Selector("allocWithZone:")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object allocWithZone(VoidPtr zone);

	@Generated
	@Selector("automaticallyNotifiesObserversForKey:")
	public static native boolean automaticallyNotifiesObserversForKey(String key);

	@Generated
	@Selector("cachePathForKey:inPath:")
	public native String cachePathForKeyInPath(String key, String path);

	@Generated
	@Selector("calculateSizeWithCompletionBlock:")
	public native void calculateSizeWithCompletionBlock(
			@ObjCBlock(name = "call_calculateSizeWithCompletionBlock") Block_calculateSizeWithCompletionBlock completionBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_calculateSizeWithCompletionBlock {
		@Generated
		void call_calculateSizeWithCompletionBlock(@NUInt long arg0,
				@NUInt long arg1);
	}

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
	@Selector("clearDiskOnCompletion:")
	public native void clearDiskOnCompletion(
			@ObjCBlock(name = "call_clearDiskOnCompletion") Block_clearDiskOnCompletion completion);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_clearDiskOnCompletion {
		@Generated
		void call_clearDiskOnCompletion();
	}

	@Generated
	@Selector("clearMemory")
	public native void clearMemory();

	@Generated
	@Selector("debugDescription")
	public static native String debugDescription_static();

	@Generated
	@Selector("defaultCachePathForKey:")
	public native String defaultCachePathForKey(String key);

	@Generated
	@Selector("deleteOldFilesWithCompletionBlock:")
	public native void deleteOldFilesWithCompletionBlock(
			@ObjCBlock(name = "call_deleteOldFilesWithCompletionBlock") Block_deleteOldFilesWithCompletionBlock completionBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_deleteOldFilesWithCompletionBlock {
		@Generated
		void call_deleteOldFilesWithCompletionBlock();
	}

	@Generated
	@Selector("description")
	public static native String description_static();

	@Generated
	@Selector("diskImageExistsWithKey:completion:")
	public native void diskImageExistsWithKeyCompletion(
			String key,
			@ObjCBlock(name = "call_diskImageExistsWithKeyCompletion") Block_diskImageExistsWithKeyCompletion completionBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_diskImageExistsWithKeyCompletion {
		@Generated
		void call_diskImageExistsWithKeyCompletion(boolean arg0);
	}

	@Generated
	@Selector("getDiskCount")
	@NUInt
	public native long getDiskCount();

	@Generated
	@Selector("getSize")
	@NUInt
	public native long getSize();

	@Generated
	@Selector("hash")
	@NUInt
	public static native long hash_static();

	@Generated
	@Selector("imageFromCacheForKey:")
	public native UIImage imageFromCacheForKey(String key);

	@Generated
	@Selector("imageFromDiskCacheForKey:")
	public native UIImage imageFromDiskCacheForKey(String key);

	@Generated
	@Selector("imageFromMemoryCacheForKey:")
	public native UIImage imageFromMemoryCacheForKey(String key);

	@Generated
	@Selector("init")
	public native SDImageCache init();

	@Generated
	@Selector("initWithNamespace:")
	public native SDImageCache initWithNamespace(String ns);

	@Generated
	@Selector("initWithNamespace:diskCacheDirectory:")
	public native SDImageCache initWithNamespaceDiskCacheDirectory(String ns,
			String directory);

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
	@Selector("makeDiskCachePath:")
	public native String makeDiskCachePath(String fullNamespace);

	@Generated
	@Selector("maxMemoryCost")
	@NUInt
	public native long maxMemoryCost();

	@Generated
	@Selector("maxMemoryCountLimit")
	@NUInt
	public native long maxMemoryCountLimit();

	@Generated
	@Owned
	@Selector("new")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object new_objc();

	@Generated
	@Selector("queryCacheOperationForKey:done:")
	public native NSOperation queryCacheOperationForKeyDone(
			String key,
			@ObjCBlock(name = "call_queryCacheOperationForKeyDone") Block_queryCacheOperationForKeyDone doneBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_queryCacheOperationForKeyDone {
		@Generated
		void call_queryCacheOperationForKeyDone(UIImage arg0, NSData arg1,
				@NInt long arg2);
	}

	@Generated
	@Selector("removeImageForKey:fromDisk:withCompletion:")
	public native void removeImageForKeyFromDiskWithCompletion(
			String key,
			boolean fromDisk,
			@ObjCBlock(name = "call_removeImageForKeyFromDiskWithCompletion") Block_removeImageForKeyFromDiskWithCompletion completion);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_removeImageForKeyFromDiskWithCompletion {
		@Generated
		void call_removeImageForKeyFromDiskWithCompletion();
	}

	@Generated
	@Selector("removeImageForKey:withCompletion:")
	public native void removeImageForKeyWithCompletion(
			String key,
			@ObjCBlock(name = "call_removeImageForKeyWithCompletion") Block_removeImageForKeyWithCompletion completion);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_removeImageForKeyWithCompletion {
		@Generated
		void call_removeImageForKeyWithCompletion();
	}

	@Generated
	@Selector("resolveClassMethod:")
	public static native boolean resolveClassMethod(SEL sel);

	@Generated
	@Selector("resolveInstanceMethod:")
	public static native boolean resolveInstanceMethod(SEL sel);

	@Generated
	@Selector("setMaxMemoryCost:")
	public native void setMaxMemoryCost(@NUInt long value);

	@Generated
	@Selector("setMaxMemoryCountLimit:")
	public native void setMaxMemoryCountLimit(@NUInt long value);

	@Generated
	@Selector("setVersion:")
	public static native void setVersion(@NInt long aVersion);

	@Generated
	@Selector("sharedImageCache")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object sharedImageCache();

	@Generated
	@Selector("storeImage:forKey:completion:")
	public native void storeImageForKeyCompletion(
			UIImage image,
			String key,
			@ObjCBlock(name = "call_storeImageForKeyCompletion") Block_storeImageForKeyCompletion completionBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_storeImageForKeyCompletion {
		@Generated
		void call_storeImageForKeyCompletion();
	}

	@Generated
	@Selector("storeImage:forKey:toDisk:completion:")
	public native void storeImageForKeyToDiskCompletion(
			UIImage image,
			String key,
			boolean toDisk,
			@ObjCBlock(name = "call_storeImageForKeyToDiskCompletion") Block_storeImageForKeyToDiskCompletion completionBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_storeImageForKeyToDiskCompletion {
		@Generated
		void call_storeImageForKeyToDiskCompletion();
	}

	@Generated
	@Selector("storeImage:imageData:forKey:toDisk:completion:")
	public native void storeImageImageDataForKeyToDiskCompletion(
			UIImage image,
			NSData imageData,
			String key,
			boolean toDisk,
			@ObjCBlock(name = "call_storeImageImageDataForKeyToDiskCompletion") Block_storeImageImageDataForKeyToDiskCompletion completionBlock);

	@Runtime(ObjCRuntime.class)
	@Generated
	public interface Block_storeImageImageDataForKeyToDiskCompletion {
		@Generated
		void call_storeImageImageDataForKeyToDiskCompletion();
	}

	@Generated
	@Selector("storeImageDataToDisk:forKey:")
	public native void storeImageDataToDiskForKey(NSData imageData, String key);

	@Generated
	@Selector("superclass")
	public static native Class superclass_static();

	@Generated
	@Selector("version")
	@NInt
	public static native long version_static();
}