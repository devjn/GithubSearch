package com.github.devjn.githubsearch.utils

import java.io.Serializable


/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * GitData
 */
class GitData<T : GitObject> {

    val total_count: Int;
    val incomplete_results: Boolean;
    val items: List<T>?;

    constructor() {
        this.total_count = 0
        this.incomplete_results = false
        this.items = null
    }

    constructor(total_count: Int, incomplete_results: Boolean, items: List<T>) {
        this.total_count = total_count
        this.incomplete_results = incomplete_results
        this.items = items
    }

}

interface GitObject {
    /**
     * Api url
     */
    val url: String;
}

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


class Repository : GitObject, Serializable {

    override val url: String
    val full_name: String
    val description: String
    val language: String
    val html_url: String

    constructor() {
        this.full_name = ""
        this.url = ""
        this.html_url = ""
        this.description = ""
        this.language = ""
    }

    constructor(full_name: String, url: String, html_url: String, description: String, language: String) {
        this.full_name = full_name
        this.url = url
        this.html_url = html_url
        this.description = description
        this.language = language
    }

}

class PinnedRepo {

    val repo: String
    val owner: String?
    val description: String?
    val language: String?

    constructor() {
        this.repo = ""
        this.owner = ""
        this.description = ""
        this.language = ""
    }

    constructor(full_name: String, url: String, description: String, language: String) {
        this.repo = full_name
        this.owner = url
        this.description = description
        this.language = language
    }

}