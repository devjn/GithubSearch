package com.github.devjn.githubsearch.db

import apple.foundation.c.Foundation
import apple.foundation.enums.NSSearchPathDirectory
import apple.foundation.enums.NSSearchPathDomainMask
import com.github.devjn.githubsearch.database.ISQLiteDatabase
import com.github.devjn.githubsearch.database.ISQLiteDatabaseHelper
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.model.entities.UserEntity
import com.github.devjn.githubsearch.utils.Utils
import org.moe.natj.general.ptr.Ptr
import org.moe.natj.general.ptr.VoidPtr
import org.moe.natj.general.ptr.impl.PtrFactory
import org.sqlite.c.Globals
import java.io.File
import java.io.IOException
import java.io.InputStream

class SQLiteDatabaseHelper() : ISQLiteDatabaseHelper {

    private var connectionHandle: VoidPtr? = null

    init {
        try {
            init()
        } catch (e: Exception) {
            connectionHandle = null
        }

    }

    @Throws(IOException::class)
    private fun init() {
        // Get path to database
        val docPath = documentsPath
        if (docPath == null) {
            System.err.println("Failed to load app's document path")
            return
        }
        val file = File(docPath, DataSource.DATABASE_NAME)

        // Check existence
        val isNew = !file.exists()

        // Open database
        @SuppressWarnings("unchecked")
        val dbHandleRef = PtrFactory.newPointerPtr(Void::class.java, 2, 1, true, false) as Ptr<VoidPtr>
        if (Globals.sqlite3_open(file.canonicalPath, dbHandleRef) != 0) {
            throw IOException("Failed to open/create database file")
        }
        connectionHandle = dbHandleRef.get()

        // Initialize
        if (isNew) {
            onCreate(writableDatabase)
        } else {
            // onUpdate(getWritableDatabase());
        }
    }

    private fun onCreate(sqLiteDatabase: ISQLiteDatabase) {
        Utils.executeSQLStatement(sqLiteDatabase, Utils.createTableSQL(DataSource.Tables.BOOKMARKS,
                UserEntity.fields))
    }

    @SuppressWarnings("unused")
    private fun onUpdate(sqLiteDatabase: ISQLiteDatabase) {
        Utils.executeSQLStatement(sqLiteDatabase, Utils.dropTableIfExistsSQL(DataSource.Tables.BOOKMARKS))
        onCreate(sqLiteDatabase)
    }

    private val documentsPath: String?
        get() {
            val paths = Foundation.NSSearchPathForDirectoriesInDomains(
                    NSSearchPathDirectory.DocumentDirectory,
                    NSSearchPathDomainMask.UserDomainMask, true)
            return paths.firstObject()
        }

    override val writableDatabase: ISQLiteDatabase
        get() = SQLiteDatabase(connectionHandle)

    override fun close() {
        if (connectionHandle != null) {
            Globals.sqlite3_close(connectionHandle)
            connectionHandle = null
        }
    }

    override val defaultDatabaseContents: InputStream?
        get() = null

}