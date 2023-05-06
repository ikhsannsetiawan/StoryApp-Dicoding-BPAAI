package com.dicoding.storyappdicodingbpaai.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.dicoding.storyappdicodingbpaai.ui.MainActivity
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, LoginFragment.newInstance())
                .commit()
        }

        supportActionBar?.hide()
    }

    fun routeToMainActivity() {
        startActivity(Intent(this@AuthenticationActivity, MainActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}