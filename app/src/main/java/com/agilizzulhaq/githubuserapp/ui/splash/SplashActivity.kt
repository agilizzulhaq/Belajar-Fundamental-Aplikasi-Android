package com.agilizzulhaq.githubuserapp.ui.splash

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agilizzulhaq.githubuserapp.R
import com.agilizzulhaq.githubuserapp.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        if (isDarkModeEnabled()) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            window.decorView.setBackgroundColor(Color.parseColor("#000000"))
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            window.decorView.setBackgroundColor(Color.WHITE)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }, 2000)
    }
    private fun isDarkModeEnabled(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}