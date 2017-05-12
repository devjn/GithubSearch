package com.github.devjn.githubsearch.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.devjn.githubsearch.database.ISQLiteDatabaseHelper
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.model.entities.UserEntity
import com.github.devjn.githubsearch.utils.Utils
import java.io.InputStream

class AndroidSQLiteDatabaseHelper(ctx: Context) : ISQLiteDatabaseHelper {

    internal var db: AndroidSQLiteDatabase? = null

    private inner class DBOpenHelper(context: Context, name: String,
                                     factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

        override fun onCreate(sqlDb: SQLiteDatabase) {
            if (db == null) {
                db = AndroidSQLiteDatabase(sqlDb)
            }
            Utils.executeSQLStatement(db, Utils.createTableSQL(UserEntity.TABLE_NAME,
                    UserEntity.fields))
        }

        override fun onUpgrade(sqlDb: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (db == null) {
                db = AndroidSQLiteDatabase(sqlDb)
            }
            Utils.executeSQLStatement(db, Utils.dropTableIfExistsSQL(UserEntity.TABLE_NAME))
            onCreate(sqlDb)
        }

    }

    private var dbH: DBOpenHelper

    init {
        dbH = DBOpenHelper(ctx, DataSource.DATABASE_NAME, null, 2)
    }

    override fun close() {
        dbH.close()
        db = null
    }

    override val writableDatabase: AndroidSQLiteDatabase
        get() {
            if (db == null) {
                db = AndroidSQLiteDatabase(dbH.writableDatabase)
            }
            return db!!
        }

    val readableDatabase: AndroidSQLiteDatabase
        get() {
            return AndroidSQLiteDatabase(dbH.readableDatabase)
        }

    override val defaultDatabaseContents: InputStream?
        get() = null
}