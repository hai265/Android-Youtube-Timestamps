package com.hai265.timestamper.data

import com.hai265.timestamper.ui.fakes.fakeVideo1
import com.hai265.timestamper.ui.fakes.fakeVideo2
import javax.inject.Inject

class TimestampsRepository @Inject constructor() {
    fun getVideos(): List<Video> {
        return listOf(
            fakeVideo1, fakeVideo2
        )
    }

    fun getVideoById(id: String): Video {
        return fakeVideo1
    }
}

