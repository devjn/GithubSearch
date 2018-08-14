package com.github.devjn.githubsearch.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.github.devjn.githubsearch.model.entities.User


/**
 * Created by @author Jahongir on 10-Aug-18
 * devjn@jn-arts.com
 * UserDao
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)

    @Delete
    fun remove(user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM user")
    fun getAllUsersLive(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE id = (:id)")
    fun getUser(id: Long): User?

}

fun UserDao.containsUser(user: User) = getUser(user.id) != null