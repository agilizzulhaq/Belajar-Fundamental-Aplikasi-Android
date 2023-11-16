package com.agilizzulhaq.githubuserapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.agilizzulhaq.githubuserapp.setting.SettingPreferences
import com.agilizzulhaq.githubuserapp.setting.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BaseApp : Application() {

    private lateinit var pref: SettingPreferences

    override fun onCreate() {
        super.onCreate()

        pref = SettingPreferences.getInstance(this.dataStore)

        runBlocking {
            val isDarkModeActive = pref.getThemeSetting().first()
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}