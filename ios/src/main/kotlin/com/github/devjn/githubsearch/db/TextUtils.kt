package com.github.devjn.githubsearch.db

object TextUtils {

    fun isEmpty(whereClause: String?): Boolean {
        if (whereClause == null) {
            return true
        }
        return whereClause.trim { it <= ' ' }.isEmpty()
    }

}