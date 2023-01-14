package com.silverorange.videoplayer.networking

import com.silverorange.videoplayer.model.Videos
import retrofit2.Call
import retrofit2.http.GET

interface VideoApi {
    @GET("videos")
    fun getVideosLibrary() : Call<ArrayList<Videos>>
}