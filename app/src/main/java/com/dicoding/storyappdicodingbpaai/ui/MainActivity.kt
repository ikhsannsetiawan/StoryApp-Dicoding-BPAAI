package com.dicoding.storyappdicodingbpaai.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.databinding.ActivityMainBinding
import com.dicoding.storyappdicodingbpaai.ui.auth.AuthenticationActivity
import com.dicoding.storyappdicodingbpaai.ui.home.HomeFragment
import com.dicoding.storyappdicodingbpaai.ui.profile.ProfileFragment
import com.dicoding.storyappdicodingbpaai.ui.story.CameraActivity
import com.google.android.material.internal.ContextUtils.getActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragmentProfile = ProfileFragment()
    private val fragmentHome = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.background = null // hide abnormal layer in bottom nav

        switchFragment(fragmentHome)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> switchFragment(fragmentHome)
                R.id.nav_profile -> switchFragment(fragmentProfile)
                R.id.nav_blank -> routeToStory()
            }
            true
        }
        binding.fab.setOnClickListener {
            routeToStory()
        }
    }


    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun routeToStory() {
        if (!allPermissionsGranted()) {
            requestPermission()
        } else {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    fun routeToAuth() = startActivity(Intent(this, AuthenticationActivity::class.java))

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    @SuppressLint("RtlHardcoded")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.UI_error_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}