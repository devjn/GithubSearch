package com.github.devjn.currencyobserver.utils

import java.io.File


/**
 * Created by @author Jahongir on 18-Apr-17
 * devjn@jn-arts.com
 * NativeUtils
 */
object NativeUtils {

    lateinit var resolver: NativeUtilsResolver
        private set

    fun registerResolver(resolver: NativeUtilsResolver) {
        println("registering Resolver")
        NativeUtils.resolver = resolver
    }
}

interface NativeUtilsResolver {

    val cacheDir : File

    fun isNetworkAvailable(): Boolean

}