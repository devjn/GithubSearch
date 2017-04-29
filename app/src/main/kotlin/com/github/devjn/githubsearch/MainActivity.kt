package com.github.devjn.githubsearch

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import com.github.devjn.githubsearch.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.navigation.selectedItemId = R.id.navigation_users
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_repos -> {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.container, SearchFragment.newInstance(SearchFragment.TYPE_REPOSITORIES))
                        .commit();
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.container, SearchFragment.newInstance(SearchFragment.TYPE_USERS))
                        .commit();
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_bookmarks -> {
                makeText(this@MainActivity, "Not implemented", Toast.LENGTH_SHORT).show()
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

    override fun getRoot(): View = findViewById(R.id.coordinator)

}
