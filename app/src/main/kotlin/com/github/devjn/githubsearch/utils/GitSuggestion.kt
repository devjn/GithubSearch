package com.github.devjn.githubsearch.utils

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion


/**
 * Created by @author Jahongir on 29-Apr-17
 * devjn@jn-arts.com
 * GitSuggestion
 */

class GitSuggestion : SearchSuggestion {

    private var sugggestionName: String

    constructor(name: String) {
        this.sugggestionName = name
    }

    constructor(source: Parcel) {
        sugggestionName = source.readString()
    }

    override fun getBody(): String = sugggestionName

    val CREATOR: Parcelable.Creator<GitSuggestion> = object : Parcelable.Creator<GitSuggestion> {
        override fun createFromParcel(inParcel: Parcel): GitSuggestion {
            return GitSuggestion(inParcel)
        }

        override fun newArray(size: Int): Array<GitSuggestion?> {
            return arrayOfNulls<GitSuggestion>(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = dest.writeString(sugggestionName)

    companion object {
        @JvmField val CREATOR = "GitSuggestion"
    }

}