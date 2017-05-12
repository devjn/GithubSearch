package com.github.devjn.githubsearch.model.db

import com.github.devjn.githubsearch.database.ISQLiteCursor
import com.github.devjn.githubsearch.database.ISQLiteDatabase
import com.github.devjn.githubsearch.database.ISQLiteDatabaseHelper
import com.github.devjn.githubsearch.model.entities.UserEntity
import com.github.devjn.githubsearch.utils.User
import com.github.devjn.githubsearch.utils.Utils
import java.util.*

class DataSource(private val dbHelper: ISQLiteDatabaseHelper) {

    object Tables {
        const val BOOKMARKS = "BOOKMARKS"
    }

    private var db: ISQLiteDatabase? = null

    fun open() {
        db = dbHelper.writableDatabase
        Utils.executeSQLStatement(db, Utils.createTableSQL(UserEntity.TABLE_NAME, UserEntity.fields))
    }

    fun close() {
        dbHelper.close()
    }

    fun createUser(user: User) {
        UserEntity.saveToDB(db, user)
    }

    fun deleteUser(id: Int) {
        UserEntity.deleteFromDB(db, id)
    }

    fun getUserById(id: Int): User? {
        val cursor = UserEntity.selectFromDB(db, UserEntity.Tags.ID.fieldName + " = " + id) ?: return null

        cursor.moveToFirst()
        var user: User? = null
        if (!cursor.isAfterLast) {
            user = UserEntity.cursorToObject(cursor)
        }
        cursor.close()
        return user
    }

    val allUsers: ArrayList<User>?
        get() {
            val cursor = UserEntity.selectFromDB(db, null) ?: return null

            val users = ArrayList<User>()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val museum = UserEntity.cursorToObject(cursor)
                users.add(museum)
                cursor.moveToNext()
            }
            cursor.close()
            return users
        }

    companion object {
        val DATABASE_NAME = "GithubSearch.db"

        fun usersFromCursor(cursor: ISQLiteCursor) : ArrayList<User> {
            val users = ArrayList<User>()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val museum = UserEntity.cursorToObject(cursor)
                users.add(museum)
                cursor.moveToNext()
            }
            cursor.close()
            return users
        }
    }

}