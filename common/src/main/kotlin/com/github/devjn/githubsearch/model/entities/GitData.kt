/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * GitData
 */

package com.github.devjn.githubsearch.model.entities

import GetPinnedReposQuery
import com.github.devjn.githubsearch.utils.Utils
import java.io.Serializable


interface GitObject {
    /**
     * Api url
     */
    val url: String;
}

data class GitData<T : GitObject>(val total_count: Int = 0,
                                  val incomplete_results: Boolean = false,
                                  val items: List<T>? = null)


@android.arch.persistence.room.Entity
class User : GitObject, Serializable {

    override var url: String;
    var login: String;
    var avatar_url: String;
    @android.arch.persistence.room.PrimaryKey
    var id: Long

    // Optional
    var name: String? = null
    var bio: String? = null
    var email: String? = null
    var company: String? = null
    var location: String? = null
    var blog: String? = null

    @android.arch.persistence.room.Ignore
    var isDetailed = false

    constructor() {
        this.id = 0
        this.login = ""
        this.url = ""
        this.avatar_url = ""
    }

    constructor(id: Long, login: String, url: String, avatar_url: String) {
        this.id = id
        this.login = login
        this.url = url
        this.avatar_url = avatar_url
    }

    fun hasExtra(): Boolean {
        if (!isDetailed) return false
        var has = false
        Utils.whenAnyNotNull(name, email, bio, company, location) {
            has = true
        }
        return has
    }

}


data class Repository(val full_name: String = "",
                      override val url: String = "",
                      val html_url: String = "",
                      val description: String? = "",
                      val language: String? = "") : GitObject, Serializable

class PinnedRepo(
        val name: String,
        val description: String? = null,
        val language: String? = null) {

    companion object {
        fun fromEdge(edge: GetPinnedReposQuery.Edge) = edge.node()?.let {
            return PinnedRepo(it.name(), it.description(), it.primaryLanguage()?.name())
        } ?: PinnedRepo(edge.__typename())
    }
}