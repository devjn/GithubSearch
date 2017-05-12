package com.github.devjn.githubsearch.db

import android.database.Cursor
import com.github.devjn.githubsearch.database.ISQLiteCursor

class AndroidCursor(private val cursor: Cursor?) : ISQLiteCursor {

    init {
        if (cursor == null) throw IllegalArgumentException("cursor must not be null")
    }

    override fun close() {
        cursor!!.close()
    }

    override fun getInt(i: Int): Int {
        return cursor!!.getInt(i)
    }

    override fun getLong(i: Int): Long {
        return cursor!!.getLong(i)
    }

    override fun getString(i: Int): String {
        return cursor!!.getString(i)
    }

    override val isAfterLast: Boolean
        get() = cursor!!.isAfterLast

    override fun moveToFirst() {
        cursor!!.moveToFirst()
    }

    override fun moveToNext() {
        cursor!!.moveToNext()
    }

    override fun getDouble(i: Int): Double {
        return cursor!!.getDouble(i)
    }

}