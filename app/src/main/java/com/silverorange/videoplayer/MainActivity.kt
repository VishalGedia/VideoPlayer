package com.silverorange.videoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.silverorange.videoplayer.databinding.ActivityMainBinding
import com.silverorange.videoplayer.model.Videos
import com.silverorange.videoplayer.model.toDate
import com.silverorange.videoplayer.viewmodel.VideosViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: VideosViewModel
    private var videoLibrary = listOf<Videos>()
    private var mPlayer: ExoPlayer? = null
    private var currentVideoIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fetch videos from provided api
        fetchVideoLibrary()

    }


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun initPlayer() {
        // Create a player instance.
        mPlayer = ExoPlayer.Builder(this).build()
    }

    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        //release player when done
        mPlayer!!.release()
        mPlayer = null
    }

    private fun fetchVideoLibrary() {
        viewModel = ViewModelProvider(this)[VideosViewModel::class.java]
        viewModel.getVideos()
        viewModel.observeMovieLiveData().observe(this) { videosList ->
            Log.d("Size", "${videosList[0].publishedAt}")

            videoLibrary = videosList.sortedBy { it.publishedAt!!.toDate() }.toList()

            //load first video by default after it gets sorted by date
            loadVideo()
        }
    }

    private fun loadVideo() {

        // Bind the player to the view.
        binding.vvPlayer.player = mPlayer

        //setting exoplayer when it is ready.
        binding.vvPlayer.useController = false
        binding.vvPlayer.player?.playWhenReady = false

        // Set the media source to be played.
        mPlayer!!.setMediaSource(buildMediaSource(videoLibrary[currentVideoIndex].hlsURL.toString()))

        // Prepare the player.
        mPlayer!!.prepare()
    }

    //creating mediaSource
    private fun buildMediaSource(url: String): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        // Create a HLS media source pointing to a playlist uri.
        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
    }
}