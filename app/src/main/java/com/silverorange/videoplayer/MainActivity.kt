package com.silverorange.videoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
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
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fetch videos from provided api
        fetchVideoLibrary()

        //event listener for play and pause button
        binding.ibPlay.setOnClickListener {
            it as ImageButton
            isPlaying = !isPlaying;
            if (isPlaying) {
                it.setImageResource(R.drawable.pause)
                playVideo()
            } else {
                it.setImageResource(R.drawable.play)
                pauseVideo()
            }
        }

        //click event listener for next button
        binding.ibNext.setOnClickListener {
            changeVideo(true)
        }

        //click event listener for previous button
        binding.ibPrevious.setOnClickListener {
            changeVideo(false)
        }
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

            if (videoLibrary.isNotEmpty()) {
                //load first video by default after it gets sorted by date
                loadVideo()
                //make previous button insensitive
                makeButtonInsensitive(true)

                if (videoLibrary.size == 1) {
                    //make next button insensitive
                    makeButtonInsensitive(false)
                }
            } else {
                Toast.makeText(this, "Empty Video Library", Toast.LENGTH_SHORT).show()
            }
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

        //update pause button to play when prev/next button is pressed on a running video
        if (isPlaying) {
            isPlaying = false
            binding.ibPlay.setImageResource(R.drawable.play)
        }
    }

    //creating mediaSource
    private fun buildMediaSource(url: String): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        // Create a HLS media source pointing to a playlist uri.
        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
    }

    //function to play a video
    private fun playVideo() {

        binding.vvPlayer.player?.play()
    }

    //function to pause a video
    private fun pauseVideo() {
        binding.vvPlayer.player?.pause()
    }

    private fun changeVideo(isNext: Boolean) {
        if (isNext) {
            //increment the current index
            currentVideoIndex++

            if (currentVideoIndex <= videoLibrary.size - 1) {
                loadVideo()
                if (currentVideoIndex == videoLibrary.size - 1) {
                    makeButtonInsensitive(false)
                }
                makeButtonSensitive(true)
            } else {
                makeButtonInsensitive(false)
            }
        } else {
            currentVideoIndex--

            // check if the increment is possible
            if (currentVideoIndex >= 0) {
                loadVideo()
                if (currentVideoIndex == 0) {
                    makeButtonInsensitive(true)
                }
                makeButtonSensitive(false)
            } else {
                makeButtonInsensitive(true)
            }
        }
    }

    //function to make button Insensitive
    private fun makeButtonInsensitive(isPrevious: Boolean) {
        if (!isPrevious) {
            binding.ibNext.alpha = 0.5f
            binding.ibNext.isClickable = false
        } else {
            binding.ibPrevious.alpha = 0.5f
            binding.ibPrevious.isClickable = false
        }
    }

    //function to make button Sensitive
    private fun makeButtonSensitive(isPrevious: Boolean) {
        if (!isPrevious) {
            binding.ibNext.isClickable = true
            binding.ibNext.alpha = 1.0f
        } else {
            binding.ibPrevious.isClickable = true
            binding.ibPrevious.alpha = 1.0f
        }
    }
}