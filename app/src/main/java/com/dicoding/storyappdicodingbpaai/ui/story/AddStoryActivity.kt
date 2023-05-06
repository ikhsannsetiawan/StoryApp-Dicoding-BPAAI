package com.dicoding.storyappdicodingbpaai.ui.story

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.databinding.ActivityAddStoryBinding
import com.dicoding.storyappdicodingbpaai.ui.MainActivity
import com.dicoding.storyappdicodingbpaai.utils.Helper
import com.dicoding.storyappdicodingbpaai.viewModel.SettingViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.SettingViewModelFactory
import com.dicoding.storyappdicodingbpaai.viewModel.StoryViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.ViewModelFactory
import java.io.File
import java.lang.StringBuilder

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var userToken: String? = null
    private var storyViewModel: StoryViewModel? = null

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = getString(R.string.menu_tilte_new_story)
        }

        storyViewModel = ViewModelProvider(this, ViewModelFactory(this))[StoryViewModel::class.java]

        val pref = SettingPreferences.getInstance(dataStore)

        val setViewModel = ViewModelProvider(this,SettingViewModelFactory(pref))[SettingViewModel::class.java]
        setViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name).observe(this){ token ->
            userToken = StringBuilder("Bearer ").append(token).toString()
        }

        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = Helper.rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )

        binding.storyImage.setImageBitmap(rotatedBitmap)
        binding.btnUpload.setOnClickListener {
            if (binding.storyDesc.text.isNotEmpty()) {
                uploadImage(myFile, binding.storyDesc.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.UI_validation_empty_story_description), Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel?.let { vm ->
            vm.isSuccessUploadStory.observe(this) {
                if (it) {
                    Toast.makeText(this, getString(R.string.API_success_upload_image), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            vm.loading.observe(this) {
                binding.loading.visibility = it
            }
            vm.error.observe(this) {
                if (it.isNotEmpty()) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(image: File, description: String) {
        if (userToken != null) {
            val file = Helper.reduceFileImage(image)
            storyViewModel?.uploadNewStory(userToken!!, file, description)
        } else {
            Toast.makeText(this, getString(R.string.API_error_header_token), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}