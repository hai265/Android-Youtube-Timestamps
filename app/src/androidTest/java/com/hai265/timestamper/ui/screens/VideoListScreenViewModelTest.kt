package com.hai265.timestamper.ui.screens

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hai265.timestamper.data.database.AppDatabase
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.domain.TimestampsToYamlStringUseCase
import com.hai265.timestamper.ui.screens.list.VideoListScreenViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoListScreenViewModelTest {
    private lateinit var db: AppDatabase

    private lateinit var videoRepository: VideoRepository
    private lateinit var timestampRepository: TimestampRepository
    private lateinit var subject: VideoListScreenViewModel


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        videoRepository = VideoRepository(db.videoDao(), youtubeMetadataApi = FakeYoutubeMetadata())
        timestampRepository = TimestampRepository(
            timestampDao = db.timestampDao()
        )
        subject = VideoListScreenViewModel(
            videoRepository,
            TimestampsToYamlStringUseCase(timestampRepository, videoRepository)
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun init_test() {
        subject
    }
}