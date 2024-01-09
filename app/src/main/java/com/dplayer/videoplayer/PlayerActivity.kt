package com.dplayer.videoplayer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.dplayer.videoplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlayerBinding

    private lateinit var play:ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUri = intent.getStringExtra("uri")

        play = ExoPlayer.Builder(this).build()

        binding.media3.player = play

        play.setMediaItem(MediaItem.fromUri(Uri.parse(videoUri)))
        play.prepare()
        play.play()


    }

    override fun onDestroy() {
        super.onDestroy()
        play.release()
    }

}