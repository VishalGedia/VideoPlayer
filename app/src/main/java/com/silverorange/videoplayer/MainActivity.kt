package com.silverorange.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.silverorange.videoplayer.databinding.ActivityMainBinding
import com.silverorange.videoplayer.model.Videos
import com.silverorange.videoplayer.model.toDate
import com.silverorange.videoplayer.viewmodel.VideosViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: VideosViewModel
    private var videoLibrary = listOf<Videos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fetch videos from provided api
        fetchVideoLibrary()

    }

    private fun fetchVideoLibrary(){
        viewModel = ViewModelProvider(this)[VideosViewModel::class.java]
        viewModel.getVideos()
        viewModel.observeMovieLiveData().observe(this) { videosList ->
            Log.d("Size", "${videosList[0].publishedAt}")

            videoLibrary = videosList.sortedBy { it.publishedAt!!.toDate() }.toList()

        }
    }
}