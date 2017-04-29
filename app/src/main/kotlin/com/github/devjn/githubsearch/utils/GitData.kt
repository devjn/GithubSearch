package com.github.devjn.githubsearch.utils

import android.os.Parcel
import paperparcel.PaperParcel
import paperparcel.PaperParcelable


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

@PaperParcel
class User : GitObject, PaperParcelable {

    override val url: String;
    val login: String;
    val avatar_url: String;

    constructor() {
        this.login = ""
        this.url = ""
        this.avatar_url = ""
    }

    constructor(login: String, url: String, avatar_url: String) {
        this.login = login
        this.url = url
        this.avatar_url = avatar_url
    }

    companion object {
        @JvmField val CREATOR = PaperParcelUser.CREATOR
    }

}

@PaperParcel
class Repository : GitObject, PaperParcelable {

    override val url: String;
    val full_name: String;
    val description: String;
    val language: String;

    constructor() {
        this.full_name = ""
        this.url = ""
        this.description = ""
        this.language = ""
    }

    constructor(full_name: String, url: String, description: String, language: String) {
        this.full_name = full_name
        this.url = url
        this.description = description
        this.language = language
    }

    private constructor(inParcel: Parcel) {
        url = inParcel.readString()
        full_name = inParcel.readString()
        description = inParcel.readString()
        language = inParcel.readString()
    }

    companion object {
        @JvmField val CREATOR = PaperParcelRepository.CREATOR
    }

}