package com.hai265.timestamper.ui.screens

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.hai265.timestamper.common.ZERO
import com.hai265.timestamper.data.database.AppDatabase
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.network.YoutubeMetadata
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import com.hai265.timestamper.data.repos.PreferencesRepository
import com.hai265.timestamper.data.repos.RepoModule.Companion.dataStore
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.ui.screens.editor.TimestampEditorState
import com.hai265.timestamper.ui.screens.editor.TimestampEditorViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

@RunWith(AndroidJUnit4::class)
class TimestampEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var videoRepo: VideoRepository
    private lateinit var timestampRepo: TimestampRepository
    private lateinit var preferencesRepo: PreferencesRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var subject: TimestampEditorViewModel

    private lateinit var db: AppDatabase

    private val videoId = "videoId"
    private val testVideo = Video(
        videoId = videoId,
        videoTitle = "Test Video",
        thumbnail = "thumb",
        lastEdited = Instant.ZERO,
        lastPlayed = 0.milliseconds
    )
    private val testTimestamps = listOf(
        Timestamp(id = 1, videoId = videoId, time = 1000.milliseconds, description = "First")
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        videoRepo = VideoRepository(db.videoDao(), FakeYoutubeMetadata())
        timestampRepo = TimestampRepository(db.timestampDao())
        preferencesRepo = PreferencesRepository(context.dataStore)
        savedStateHandle = SavedStateHandle(mapOf("id" to videoId))
        subject = TimestampEditorViewModel(
            savedStateHandle = savedStateHandle,
            videoRepo = videoRepo,
            timestampRepo = timestampRepo,
            preferencesRepository = preferencesRepo
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun init_test() = runTest {
        subject.state.test {
            assertEquals(TimestampEditorState(), awaitItem())
        }
    }
}

class FakeYoutubeMetadata : YoutubeMetadataApiService {
    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadata {
        return YoutubeMetadata(title = "Title", thumbnail = "thumbnail")
    }

}