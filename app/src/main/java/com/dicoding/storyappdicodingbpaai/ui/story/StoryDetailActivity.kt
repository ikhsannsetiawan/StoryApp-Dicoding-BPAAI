package com.dicoding.storyappdicodingbpaai.ui.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aprianto.dicostory.utils.Constanta
import com.bumptech.glide.Glide
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* toolbar */
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = getString(R.string.menu_tilte_detail_story)
        }

        binding.storyName.text =
            intent.getData(Constanta.StoryDetail.UserName.name, "Name")
        Glide.with(binding.root)
            .load(intent.getData(Constanta.StoryDetail.ImageURL.name, ""))
            .into(binding.storyImage)
        binding.storyDesc.text =
            intent.getData(Constanta.StoryDetail.ContentDescription.name, "Caption")
        binding.storyUploadTime.text =
            intent.getData(Constanta.StoryDetail.UploadTime.name, "Upload time")
    }

    private fun Intent.getData(key: String, defaultValue: String = "None"): String {
        return getStringExtra(key) ?: defaultValue
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}