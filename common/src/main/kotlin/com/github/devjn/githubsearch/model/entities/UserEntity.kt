package com.github.devjn.githubsearch.model.entities

import com.github.devjn.githubsearch.database.ISQLiteCursor
import com.github.devjn.githubsearch.database.ISQLiteDatabase
import com.github.devjn.githubsearch.model.db.DataBaseField
import com.github.devjn.githubsearch.model.db.DataSource
import com.github.devjn.githubsearch.model.entities.UserEntity.Tags.ID
import com.github.devjn.githubsearch.utils.Utils

object UserEntity {

    val TABLE_NAME = DataSource.Tables.BOOKMARKS

    private val SQLITE_TYPE_TEXT = "TEXT"
    private val SQLITE_TYPE_DOUBLE = "DOUBLE"
    private val SQLITE_TYPE_INTEGER = "INTEGER"

    object Tags {
        val ID = DataBaseField("ID", SQLITE_TYPE_INTEGER, true, true)
        val URL = DataBaseField("url", SQLITE_TYPE_TEXT, notNull = true)
        val LOGIN = DataBaseField("login", SQLITE_TYPE_TEXT, notNull = true)
        val AVATAR_URL = DataBaseField("avatar_url", SQLITE_TYPE_TEXT, notNull = true)
    }

    val fields = arrayOf<DataBaseField?>(ID, Tags.LOGIN, Tags.URL, Tags.AVATAR_URL)
    val fieldNames = arrayOf<String?>(ID.fieldName, Tags.LOGIN.fieldName, Tags.URL.fieldName, Tags.AVATAR_URL.fieldName)

    fun saveToDB(db: ISQLiteDatabase?, user: User) {
        val values = db!!.newContentValues()
        values.put(Tags.ID.fieldName, user.id)
        values.put(Tags.LOGIN.fieldName, user.login)
        values.put(Tags.URL.fieldName, user.url)
        values.put(Tags.AVATAR_URL.fieldName, user.avatar_url)
        db.insert(TABLE_NAME, null, values)
    }

    fun deleteFromDB(db: ISQLiteDatabase, id: Int) {
        db.delete(TABLE_NAME, Utils.createClauseWhereFieldEqualsValue(ID, id), null)
    }

    fun selectFromDB(db: ISQLiteDatabase, selection: String?) = db.query(TABLE_NAME, fieldNames, selection, null, null, null, null)


    fun selectMaxIDFromDB(db: ISQLiteDatabase): ISQLiteCursor? {
        val column = ID.fieldName
        val selection = "SELECT max($column) FROM ${TABLE_NAME}"
        return db.rawQuery(selection, null)
    }

    fun cursorToObject(cursor: ISQLiteCursor): User {
        val id = cursor.getLong(0)
        val login = cursor.getString(1)
        val url = cursor.getString(2)
        val avatar = cursor.getString(3)
        return User(id, login, url, avatar)
    }

}