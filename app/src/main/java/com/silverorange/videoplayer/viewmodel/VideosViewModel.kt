package com.silverorange.videoplayer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverorange.videoplayer.model.Videos
import com.silverorange.videoplayer.networking.RetrofitInstance
import com.silverorange.videoplayer.networking.VideoApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideosViewModel : ViewModel() {
    private var videosLiveData = MutableLiveData<List<Videos>>()
    fun getVideos() {
        RetrofitInstance.api.getVideosLibrary().enqueue(object : Callback<ArrayList<Videos>> {
            override fun onResponse(call: Call<ArrayList<Videos>>, response: Response<ArrayList<Videos>>) {
                if (response.body()!=null){
                    videosLiveData.value = response.body()!!
                }
                else{
                    return
                }
            }
            override fun onFailure(call: Call<ArrayList<Videos>>, t: Throwable) {
                Log.d("ViewModel",t.message.toString())
            }
        })
    }
    fun observeMovieLiveData() : LiveData<List<Videos>> {
        return videosLiveData
    }
}
