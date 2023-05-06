package com.dicoding.storyappdicodingbpaai.viewModel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyappdicodingbpaai.R
import com.dicoding.storyappdicodingbpaai.data.api.ApiConfig
import com.dicoding.storyappdicodingbpaai.data.model.Login
import com.dicoding.storyappdicodingbpaai.data.model.Register
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationViewModel(val context: Context) : ViewModel() {

    var loading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")
    val emailTemp = MutableLiveData("")

    val loginResult = MutableLiveData<Login>()
    val registerResult = MutableLiveData<Register>()

    private val TAG = AuthenticationViewModel::class.simpleName

    fun login(email: String, password: String){
        emailTemp.postValue(email)
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                when (response.code()) {
                    400 -> error.postValue(context.getString(R.string.API_error_email_invalid))
                    401 -> error.postValue(context.getString(R.string.API_error_unauthorized))
                    200 -> loginResult.postValue(response.body())
                    else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }

        })

    }

    fun register(name: String, email: String, password: String) {
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<Register> {
            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                // parsing manual error code API
                when (response.code()) {
                    400 -> error.postValue(context.getString(R.string.API_error_email_invalid))
                    201 -> registerResult.postValue(response.body())
                    else -> error.postValue("ERROR ${response.code()} : ${response.errorBody()}")
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Register>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }
}