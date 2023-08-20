package com.cibertec.bodegaubita.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.cibertec.bodegaubita.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIME_OUT: Long = 2000 // 2 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Handler().postDelayed({
//            startActivity(LoginActivity.newIntent(this))
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }, SPLASH_TIME_OUT)
    }
}