package com.dplayer.videoplayer.data

import android.net.Uri

data class VideoModel(
    val title:String,
    val duration:Long,
    val path:String,
    val thumb: Uri?,
    val size:Long,
    val date:String?
)
