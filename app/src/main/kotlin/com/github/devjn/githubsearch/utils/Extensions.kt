package com.github.devjn.githubsearch.utils

import android.os.Bundle
import android.support.v4.app.Fragment


/**
 * Created by @author Jahongir on 20-Aug-18
 * devjn@jn-arts.com
 * Extensions
 */
inline fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T = this.apply {
    arguments = Bundle().apply(argsBuilder)
}

fun Bundle.putAllSafe(bundle: Bundle?) = bundle?.let { this.putAll(bundle) }