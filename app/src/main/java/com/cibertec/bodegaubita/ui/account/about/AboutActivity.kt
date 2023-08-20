package com.cibertec.bodegaubita.ui.account.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cibertec.bodegaubita.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnback.setOnClickListener {
            finish()
        }
    }
}