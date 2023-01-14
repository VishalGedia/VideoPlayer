package com.silverorange.videoplayer.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private const val baseUrl = "http://localhost:4000/"
    val api : VideoApi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoApi::class.java)
    }
}