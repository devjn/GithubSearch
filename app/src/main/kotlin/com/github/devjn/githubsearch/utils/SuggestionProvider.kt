package com.github.devjn.githubsearch.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.app.SearchableInfo
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.util.Log


/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * SuggestionProvider
 */

class SuggestionProvider : android.content.SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
    }

    companion object {
        const val AUTHORITY = "com.github.devjn.githubsearch.utils.SuggestionProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}

class SuggestionAdapter(private val activity: Activity, searchManager: SearchManager) {

    private val mSearchManager: SearchManager = searchManager

    fun getSuggestions(query: String, limit: Int): ArrayList<GitSuggestion>? {
        var cursor: Cursor? = null
        try {
            cursor = getSearchManagerSuggestions(mSearchManager.getSearchableInfo(activity.componentName), query, limit)
            if (cursor != null) {
                val text1Col = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
                val list: ArrayList<GitSuggestion> = ArrayList(cursor.count)
                if (cursor.moveToFirst()) {
                    var count = 0
                    do {
                        list.add(GitSuggestion(cursor.getString(text1Col)))
                        count++
                    } while (cursor.moveToNext() && count < limit)
                    return list
                }
            } else Log.w("SuggestionAdapter", "cursor is null")
        } catch (e: RuntimeException) {
            Log.w("SuggestionAdapter", "Search suggestions query threw an exception.", e)
        } finally {
            cursor?.close()
        }
        return null
    }

    @SuppressLint("Recycle")
    private fun getSearchManagerSuggestions(searchable: SearchableInfo?, query: String, limit: Int): Cursor? {
        if (searchable == null) {
            return null
        }

        val authority = searchable.suggestAuthority ?: SuggestionProvider.AUTHORITY

        val uriBuilder = Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority)
                .query("")  // workaround for a bug in Uri.writeToParcel()
                .fragment("")  // workaround for a bug in Uri.writeToParcel()

        // if content path provided, insert it now
        val contentPath = searchable.suggestPath
        if (contentPath != null) {
            uriBuilder.appendEncodedPath(contentPath)
        }

        // append standard suggestion query path
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)

        // get the query selection, may be null
        val selection = searchable.suggestSelection
        // inject query, either as selection args or inline
        var selArgs: Array<String>? = null
        if (selection != null) {    // use selection if provided
            selArgs = arrayOf(query)
        } else {                    // no selection, use REST pattern
            uriBuilder.appendPath(query)
        }

        if (limit > 0) {
            uriBuilder.appendQueryParameter("limit", limit.toString())
        }

        val uri = uriBuilder.build()

        // finally, make the query
        return activity.contentResolver.query(uri, null, selection, selArgs, null)
    }

}