package com.github.devjn.githubsearch.utils


/**
 * Created by @author Jahongir on 28-Apr-17
 * devjn@jn-arts.com
 * SuggestionProvider
 */

class SuggestionProvider : android.content.SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(SuggestionProvider.Companion.AUTHORITY, SuggestionProvider.Companion.MODE)
    }

    companion object {
        val AUTHORITY = "com.github.devjn.githubsearch.utils.SuggestionProvider"
        val MODE = DATABASE_MODE_QUERIES
    }
}