package com.hai265.timestamper.ui.screens

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.hai265.timestamper.data.database.AppDatabase
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
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

@RunWith(AndroidJUnit4::class)
class TimestampEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var videoRepo: VideoRepository
    private lateinit var timestampRepo: TimestampRepository
    private lateinit var preferencesRepo: PreferencesRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: TimestampEditorViewModel

    private lateinit var db: AppDatabase

    private val videoId = "test_video_id"
    private val testVideo = Video(
        videoId = videoId,
        videoTitle = "Test Video",
        thumbnail = "thumb",
        lastEdited = 0.milliseconds,
        lastPlayed = 0.milliseconds
    )
    private val testTimestamps = listOf(
        Timestamp(id = 1, videoId = videoId, timeMs = 1000, description = "First")
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

        videoRepo = VideoRepository(db.videoDao())
        timestampRepo = TimestampRepository(db.timestampDao())
        preferencesRepo = PreferencesRepository(context.dataStore)
        savedStateHandle = SavedStateHandle(mapOf("id" to videoId))
        viewModel = TimestampEditorViewModel(
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
        viewModel.state.test {
            assertEquals(TimestampEditorState(), awaitItem())
        }
    }
}