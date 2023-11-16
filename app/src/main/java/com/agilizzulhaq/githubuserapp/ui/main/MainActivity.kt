package com.agilizzulhaq.githubuserapp.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agilizzulhaq.githubuserapp.R
import com.agilizzulhaq.githubuserapp.data.model.User
import com.agilizzulhaq.githubuserapp.databinding.ActivityMainBinding
import com.agilizzulhaq.githubuserapp.setting.SettingActivity
import com.agilizzulhaq.githubuserapp.setting.SettingPreferences
import com.agilizzulhaq.githubuserapp.setting.ViewModelFactory
import com.agilizzulhaq.githubuserapp.setting.dataStore
import com.agilizzulhaq.githubuserapp.ui.detail.DetailUserActivity
import com.agilizzulhaq.githubuserapp.ui.favorite.FavoriteActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let { actionBar ->
            val actionBarColor = if (isDarkModeEnabled()) {
                R.color.black
            } else {
                R.color.blue
            }

            actionBar.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        this,
                        actionBarColor
                    )
                )
            )

            val title = SpannableString("GitHub Users")
            title.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title.setSpan(
                AbsoluteSizeSpan(24, true),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            supportActionBar?.title = title
        }

        adapter = UserAdapter()
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickedCallback {
            override fun onItemClicked(data: User) {
                Intent(this@MainActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                    it.putExtra(DetailUserActivity.EXTRA_URL, data.avatar_url)
                    startActivity(it)
                }
            }
        })

        val pref = SettingPreferences.getInstance(application.dataStore)

        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        binding.apply {
            rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUser.setHasFixedSize(true)
            rvUser.adapter = adapter


            btnSearch.setOnClickListener {
                searchUser()
            }

            etQuery.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchUser()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }

        val searchTextInputLayout = findViewById<TextInputLayout>(R.id.searchTextInputLayout)
        val searchIconRes =
            if (isDarkModeEnabled()) R.drawable.ic_search_white else R.drawable.ic_search_black

        if (isDarkModeEnabled()) {
            searchTextInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.white)
            binding.btnSearch.setColorFilter(ContextCompat.getColor(this, R.color.white))
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        } else {
            searchTextInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.black)
            binding.btnSearch.setColorFilter(ContextCompat.getColor(this, R.color.black))
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        }

        binding.btnSearch.setImageResource(searchIconRes)

        viewModel.getSearchUsers().observe(this) {
            if (it != null) {
                adapter.setList(it)
                showLoading(false)
            }
        }

        viewModel.snackbarText.observe(this) { message ->
            message.getContentIfNotHandled()?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchUser() {
        binding.apply {
            val query = etQuery.text.toString()
            if (query.isEmpty()) return
            showLoading(true)
            viewModel.setSearchUsers(query)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.favorite_menu -> {
                Intent(this, FavoriteActivity::class.java).also {
                    startActivity(it)
                }
            }

            R.id.setting_menu -> {
                Intent(this, SettingActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isDarkModeEnabled(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }
}