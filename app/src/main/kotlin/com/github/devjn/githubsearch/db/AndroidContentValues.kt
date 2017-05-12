package com.github.devjn.githubsearch.db

import android.content.ContentValues
import com.github.devjn.githubsearch.database.ISQLiteContentValues

class AndroidContentValues : ISQLiteContentValues {

    val contentValues: ContentValues

    constructor() {
        contentValues = ContentValues()
    }

    constructor(values: ContentValues) {
        contentValues = values
    }

    override operator fun get(colName: String): Any? {
        return contentValues.get(colName)
    }

    override fun keySet(): Set<String> {
        return contentValues.keySet()
    }

    override fun put(key: String, value: String) {
        contentValues.put(key, value)
    }

    override fun put(key: String, value: Byte?) {
        contentValues.put(key, value)
    }

    override fun put(key: String, value: Short?) {
        contentValues.put(key, value)
    }

    override fun put(key: String, value: Int?) {
        contentValues.put(key, value)
    }

    override fun put(key: String, value: Long?) {
        contentValues.put(key, value)
    }

    override fun put(key: String, value: Float?) {
        contentValues.put(key, value)
    }

    override fun put(key: String, value: Double?) {
        contentValues.put(key, value)
    }

    override fun put(key: String, value: Boolean?) {
        contentValues.put(key, value)
    }

    override fun size(): Int {
        return contentValues.size()
    }

}