package com.github.devjn.githubsearch.database

import java.io.InputStream

interface ISQLiteDatabaseHelper {

    val writableDatabase: ISQLiteDatabase

    val defaultDatabaseContents: InputStream?

    fun close()
}