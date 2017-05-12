package com.github.devjn.githubsearch.model.db

class DataBaseField {

    val fieldName: String
    val fieldType: String
    var isPrimaryKey: Boolean = false
    var isAutoIncrement: Boolean = false
    var isNotNull: Boolean = false

    constructor(name: String, type: String, primaryKey: Boolean = false, autoIncrement: Boolean = false, notNull: Boolean) {
        this.fieldName = name
        this.fieldType = type
        this.isPrimaryKey = primaryKey
        this.isAutoIncrement = autoIncrement
        this.isNotNull = notNull
    }

    constructor(name: String, type: String, primaryKey: Boolean, autoIncrement: Boolean) {
        this.fieldName = name
        this.fieldType = type
        this.isPrimaryKey = primaryKey
        this.isAutoIncrement = autoIncrement
        this.isNotNull = false
    }

    constructor(name: String, type: String) {
        this.fieldName = name
        this.fieldType = type
        this.isPrimaryKey = false
        this.isAutoIncrement = false
        this.isNotNull = true
    }
}