package com.github.devjn.githubsearch.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.github.devjn.githubsearch.db.dao.UserDao
import com.github.devjn.githubsearch.model.entities.User


/**
 * Created by @author Jahongir on 10-Aug-18
 * devjn@jn-arts.com
 * AppDatabase
 */
@Database(entities = [User::class], version = 1)
/*, AnotherEntityType.class, AThirdEntityType.class */
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
}