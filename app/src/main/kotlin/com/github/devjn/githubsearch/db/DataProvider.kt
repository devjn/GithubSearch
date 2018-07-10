package com.github.devjn.githubsearch.db

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.github.devjn.githubsearch.BuildConfig
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.model.db.DataSource.Tables
import com.github.devjn.githubsearch.model.entities.UserEntity
import com.github.devjn.githubsearch.model.entities.UserEntity.Tags
import com.github.devjn.githubsearch.utils.User


/**
 * Created by @author Jahongir on 29-Apr-17
 * devjn@jn-arts.com
 * DataProvider
 */
class DataProvider : ContentProvider() {

    companion object {

        const val AUTHORITY = BuildConfig.APPLICATION_ID + ".utils.DataProvider"
        val CONTENT_PROVIDER = "content://$AUTHORITY"
//        val CONTENT_URI_SEARCH = Uri.parse(CONTENT_PROVIDER + "/" + Tables.SEARCH)
        val CONTENT_URI_BOOKMARKS = Uri.parse(CONTENT_PROVIDER + "/" + UserEntity.TABLE_NAME)!!

        private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

//        private val SEARCH_ALLROWS = 201
//        private val SEARCH_SINGLE_ROW = 202
        private const val BOOKMARKS_ALLROWS = 203
        private const val BOOKMARKS_SINGLE_ROW = 204

        init {
//            uriMatcher.addURI(AUTHORITY, Tables.SEARCH, SEARCH_ALLROWS);
//            uriMatcher.addURI(AUTHORITY, Tables.SEARCH + "/#", SEARCH_SINGLE_ROW);
            uriMatcher.addURI(AUTHORITY, UserEntity.TABLE_NAME, BOOKMARKS_ALLROWS)
            uriMatcher.addURI(AUTHORITY, UserEntity.TABLE_NAME + "/#", BOOKMARKS_SINGLE_ROW)
        }

        // Helper method to easily insert and remove users

        fun insertUser(context: Context, user: User) {
            val source = DataSource(AndroidSQLiteDatabaseHelper(context))
            source.open()
            source.createUser(user)
        }

        fun removeUser(context: Context, user: User) {
            context.contentResolver.delete(Uri.parse(CONTENT_PROVIDER + "/" + Tables.BOOKMARKS + "/" + user.id), null, arrayOf(Tags.ID.fieldName))
        }

    }


    private lateinit var dbHelper: AndroidSQLiteDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = AndroidSQLiteDatabaseHelper(context)
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        val db = dbHelper.writableDatabase
        val id: Long
        when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> id = db.insert(Tables.BOOKMARKS, null, AndroidContentValues(values!!))
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        val insertUri = ContentUris.withAppendedId(uri, id)
        context.contentResolver.notifyChange(insertUri, null)
        return insertUri
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val db = dbHelper.readableDatabase
        val qb = SQLiteQueryBuilder()
        when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> qb.tables = Tables.BOOKMARKS
            BOOKMARKS_SINGLE_ROW -> {
                qb.tables = Tables.BOOKMARKS
                qb.appendWhere(Tags.ID.fieldName + " = " + uri.lastPathSegment)
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        val c: Cursor
        c = qb.query(db.getNativeDb(), projection, selection, selectionArgs, null, null, sortOrder)
        c.setNotificationUri(context.contentResolver, uri)
        return c
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val count: Int
        count = when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> db.update(Tables.BOOKMARKS, AndroidContentValues(values!!), selection!!, selectionArgs)
            BOOKMARKS_SINGLE_ROW -> db.update(Tables.BOOKMARKS, AndroidContentValues(values!!), Tags.ID.fieldName + " = ?", arrayOf(uri.lastPathSegment))
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val count: Int
        count = when (uriMatcher.match(uri)) {
            BOOKMARKS_ALLROWS -> db.delete(Tables.BOOKMARKS, selection, selectionArgs)
            BOOKMARKS_SINGLE_ROW -> db.delete(Tables.BOOKMARKS, Tags.ID.fieldName + " = ?", arrayOf(uri.lastPathSegment))
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri?): String? {
        return null
    }

}