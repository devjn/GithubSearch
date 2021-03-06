package org.moe.bindings.enums;


import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.NUInt;

@Generated
public final class SDWebImageOptions {
	@Generated
	private SDWebImageOptions() {
	}

	@Generated
	@NUInt
	public static final long RetryFailed = 0x0000000000000001L;
	@Generated
	@NUInt
	public static final long LowPriority = 0x0000000000000002L;
	@Generated
	@NUInt
	public static final long CacheMemoryOnly = 0x0000000000000004L;
	@Generated
	@NUInt
	public static final long ProgressiveDownload = 0x0000000000000008L;
	@Generated
	@NUInt
	public static final long RefreshCached = 0x0000000000000010L;
	@Generated
	@NUInt
	public static final long ContinueInBackground = 0x0000000000000020L;
	@Generated
	@NUInt
	public static final long HandleCookies = 0x0000000000000040L;
	@Generated
	@NUInt
	public static final long AllowInvalidSSLCertificates = 0x0000000000000080L;
	@Generated
	@NUInt
	public static final long HighPriority = 0x0000000000000100L;
	@Generated
	@NUInt
	public static final long DelayPlaceholder = 0x0000000000000200L;
	@Generated
	@NUInt
	public static final long TransformAnimatedImage = 0x0000000000000400L;
	@Generated
	@NUInt
	public static final long AvoidAutoSetImage = 0x0000000000000800L;
	@Generated
	@NUInt
	public static final long ScaleDownLargeImages = 0x0000000000001000L;
}