package com.github.devjn.githubsearch

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.devjn.githubsearch.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        // Set only when savedInstanceState is null to preserve state
        if (savedInstanceState == null)
            binding.navigation.selectedItemId = R.id.navigation_users
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_repos -> {
                supportFragmentManager.beginTransaction().replace(
                        R.id.container, SearchFragment.newInstance(SearchFragment.TYPE_REPOSITORIES))
                        .commit();
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                var bundle: Bundle? = null
                if (intent != null && intent.action == Intent.ACTION_SEARCH) {
                    bundle = intent.extras
                    intent = null
                }
                supportFragmentManager.beginTransaction().replace(
                        R.id.container, SearchFragment.newInstance(SearchFragment.TYPE_USERS, bundle))
                        .commit();
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_bookmarks -> {
                supportFragmentManager.beginTransaction().replace(
                        R.id.container, BookmarksFragment())
                        .commit();
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun getRoot(): View = binding.coordinator

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

}
