package com.github.devjn.githubsearch.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.github.devjn.githubsearch.R
import com.github.devjn.githubsearch.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private val binding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        // Set only when savedInstanceState is null to preserve state
        if (savedInstanceState == null)
            binding.navigation.selectedItemId = R.id.navigation_users
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val fragment: Fragment = when (item.itemId) {
            R.id.navigation_repos -> SearchFragment.newInstance(SearchFragment.TYPE_REPOSITORIES)
            R.id.navigation_users -> SearchFragment.newInstance(SearchFragment.TYPE_USERS,
                    intent?.extras.takeIf { intent.action == Intent.ACTION_SEARCH }?.apply { intent = null })
            else -> BookmarksFragment() // 'else' is used to make code cleaner
        }

        supportFragmentManager.beginTransaction().
                replace(R.id.container, fragment)
                .commit()

        true
    }

}
