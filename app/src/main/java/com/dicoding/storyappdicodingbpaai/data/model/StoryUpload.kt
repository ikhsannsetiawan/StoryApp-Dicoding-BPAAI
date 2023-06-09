package com.dicoding.storyappdicodingbpaai.data.model

import com.google.gson.annotations.SerializedName

data class StoryUpload(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)