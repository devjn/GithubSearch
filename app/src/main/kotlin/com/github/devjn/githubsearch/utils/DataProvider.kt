package com.github.devjn.githubsearch.utils

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.provider.BaseColumns
import com.github.devjn.githubsearch.BuildConfig
import com.github.devjn.githubsearch.utils.DataProvider.BookmarkTags.USER_ID


/**
 * Created by @author Jahongir on 29-Apr-17
 * devjn@jn-arts.com
 * DataProvider
 */
class DataProvider : ContentProvider() {

    internal object Tables {
        //        const val SEARCH = "search"
        const val BOOKMARKS = "bookmarks"
    }

    object BookmarkTags {
        val URL = "url"
        val USER_ID = "user_id"
        val LOGIN_NAME = "login"
        val AVATAR_URL = "avatar_url"
    }

    companion object {

        val AUTHORITY = BuildConfig.APPLICATION_ID + ".utils.DataProvider"
        val CONTENT_PROVIDER = "content://" + AUTHORITY
        //        val CONTENT_URI_SEARCH = Uri.parse(CONTENT_PROVIDER + "/" + Tables.SEARCH)
        val CONTENT_URI_BOOKMARKS = Uri.parse(CONTENT_PROVIDER + "/" + Tables.BOOKMARKS)

        private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        //        private val SEARCH_ALLROWS = 201
//        private val SEARCH_SINGLE_ROW = 202
        private val BOOKMARKS_ALLROWS = 203
        private val BOOKMARKS_SINGLE_ROW = 204

        init {
//            uriMatcher.addURI(AUTHORITY, Tables.SEARCH, SEARCH_ALLROWS);
//            uriMatcher.addURI(AUTHORITY, Tables.SEARCH + "/#", SEARCH_SINGLE_ROW);
            uriMatcher.addURI(AUTHORITY, Tables.BOOKMARKS, BOOKMARKS_ALLROWS);
            uriMatcher.addURI(AUTHORITY, Tables.BOOKMARKS + "/#", BOOKMARKS_SINGLE_ROW);
        }

        // Helper method to easily insert and remove users

        fun insertUser(context: Context, user: User) {
            val userValues = ContentValues()
            userValues.put(BookmarkTags.URL, user.url)
            userValues.put(BookmarkTags.USER_ID, user.id)
            userValues.put(BookmarkTags.LOGIN_NAME, user.login)
            userValues.put(BookmarkTags.AVATAR_URL, user.avatar_url)
            context.contentResolver.insert(CONTENT_URI_BOOKMARKS, userValues)
        }

        fun removeUser(context: Context, user: User) {
            context.contentResolver.delete(Uri.parse(CONTENT_PROVIDER + "/" + Tables.BOOKMARKS + "/" + user.id), null, arrayOf(BookmarkTags.USER_ID))
        }

    }

    private class DbHelper internal constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE " + Tables.BOOKMARKS + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + BookmarkTags.URL + " TEXT NOT NULL,"
                    + BookmarkTags.USER_ID + " INTEGER NOT NULL,"
                    + BookmarkTags.AVATAR_URL + " TEXT NOT NULL,"
                    + BookmarkTags.LOGIN_NAME + " TEXT NOT NULL,"
                    + " UNIQUE (" + BookmarkTags.USER_ID + ") ON CONFLICT REPLACE,"
                    + " UNIQUE (" + BookmarkTags.LOGIN_NAME + ") ON CONFLICT REPLACE)");
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.BOOKMARKS)
            onCreate(db)
        }

        companion object {
            private val DATABASE_NAME = "GithubSearch.db"
            private val DATABASE_VERSION = 1
        }

    }

    private lateinit var dbHelper: DbHelper

    override fun onCreate(): Boolean {
        dbHelper = DbHelper(context)
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        val db = dbHelper.writableDatabase
        val id: Long
        when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> id = db.insertOrThrow(Tables.BOOKMARKS, null, values)
            else -> throw IllegalArgumentException("Unsupported URI: " + uri);
        }
        val insertUri = ContentUris.withAppendedId(uri, id)
        context.contentResolver.notifyChange(insertUri, null)
        return insertUri
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val db = dbHelper.readableDatabase
        val qb = SQLiteQueryBuilder()
        when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> qb.tables = Tables.BOOKMARKS;
            BOOKMARKS_SINGLE_ROW -> {
                qb.tables = Tables.BOOKMARKS
                qb.appendWhere(USER_ID + " = " + uri.lastPathSegment);
            };
            else -> throw IllegalArgumentException("Unsupported URI: " + uri);
        }
        val c: Cursor
        c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        c.setNotificationUri(context.contentResolver, uri)
        return c
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val count: Int
        when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> count = db.update(Tables.BOOKMARKS, values, selection, selectionArgs)
            BOOKMARKS_SINGLE_ROW -> count = db.update(Tables.BOOKMARKS, values, USER_ID + " = ?", arrayOf(uri.lastPathSegment))
            else -> throw IllegalArgumentException("Unsupported URI: " + uri);
        }
        context.contentResolver.notifyChange(uri, null);
        return count;
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val count: Int
        when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> count = db.delete(Tables.BOOKMARKS, selection, selectionArgs)

            BOOKMARKS_SINGLE_ROW -> count = db.delete(Tables.BOOKMARKS, USER_ID + " = ?", arrayOf(uri.lastPathSegment))
            else -> throw IllegalArgumentException("Unsupported URI: " + uri);
        }
        context.contentResolver.notifyChange(uri, null);
        return count;
    }

    override fun getType(uri: Uri?): String? {
        return null
    }

}