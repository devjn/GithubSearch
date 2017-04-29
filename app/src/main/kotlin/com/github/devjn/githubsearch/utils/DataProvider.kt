package com.github.devjn.githubsearch.utils

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri


/**
 * Created by @author Jahongir on 29-Apr-17
 * devjn@jn-arts.com
 * DataProvider
 */
class DataProvider : ContentProvider() {

    internal object Tables {
        const val SEARCH = "search"
        const val BOOKMARKS = "bookmarks"
    }

    companion object {

        val AUTHORITY = "com.github.devjn.githubsearch.provider.utils.DataProvider"
        val CONTENT_PROVIDER = "content://" + AUTHORITY
        val CONTENT_URI_SEARCH = Uri.parse(CONTENT_PROVIDER + "/" + Tables.SEARCH)
        val CONTENT_URI_BOOKMARKS = Uri.parse(CONTENT_PROVIDER + "/" + Tables.BOOKMARKS)

        private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        private val SEARCH_ALLROWS = 201
        private val SEARCH_SINGLE_ROW = 202
        private val BOOKMARKS_ALLROWS = 203
        private val BOOKMARKS_SINGLE_ROW = 204

        init {
            uriMatcher.addURI(AUTHORITY, Tables.SEARCH, SEARCH_ALLROWS);
            uriMatcher.addURI(AUTHORITY, Tables.SEARCH + "/#", SEARCH_SINGLE_ROW);
            uriMatcher.addURI(AUTHORITY, Tables.BOOKMARKS, BOOKMARKS_ALLROWS);
            uriMatcher.addURI(AUTHORITY, Tables.BOOKMARKS + "/#", BOOKMARKS_SINGLE_ROW);
        }

    }

//    private val dbHelper: DbHelper? = null

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(uri: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}