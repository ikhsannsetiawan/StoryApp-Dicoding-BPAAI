package com.dicoding.storyappdicodingbpaai.data.api

import com.dicoding.storyappdicodingbpaai.data.model.Login
import com.dicoding.storyappdicodingbpaai.data.model.Register
import com.dicoding.storyappdicodingbpaai.data.model.StoryList
import com.dicoding.storyappdicodingbpaai.data.model.StoryUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Login>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Register>

    @GET("stories")
    fun getStoryList(
        @Header("Authorization") token:String,
        @Query("size") size:Int
    ): Call<StoryList>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token:String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<StoryUpload>
}