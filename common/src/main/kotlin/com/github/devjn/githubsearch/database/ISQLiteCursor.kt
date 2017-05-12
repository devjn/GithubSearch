package com.github.devjn.githubsearch.database

interface ISQLiteCursor {

    fun moveToFirst()
    fun close()
    val isAfterLast: Boolean
    fun getString(i: Int): String
    fun getInt(i: Int): Int
    fun getLong(i: Int): Long
    fun moveToNext()
    fun getDouble(i: Int): Double
}