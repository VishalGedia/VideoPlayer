package com.silverorange.videoplayer.model

import java.text.SimpleDateFormat
import java.util.*

data class Videos(

    var id: String? = null,
    var title: String? = null,
    var hlsURL: String? = null,
    var fullURL: String? = null,
    var description: String? = null,
    var publishedAt: String? = null,
    var author: Author? = Author()
)

data class Author(
    var id: String? = null,
    var name: String? = null
)

fun String.toDate(): Date? {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(this)
}
