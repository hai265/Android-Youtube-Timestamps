package com.hai265.timestamper.ui.screens

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Timestamps
import com.hai265.timestamper.Videos
import com.hai265.timestamper.common.ZERO
import com.hai265.timestamper.data.database.SqlDelightTimestampsDao
import com.hai265.timestamper.data.database.SqlDelightVideoDao
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.durationAdapter
import com.hai265.timestamper.data.database.instantAdapter
import com.hai265.timestamper.data.database.uuidAdapter
import com.hai265.timestamper.data.network.YoutubeMetadata
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import com.hai265.timestamper.data.repos.PreferencesRepository
import com.hai265.timestamper.data.repos.RepoModule.Companion.dataStore
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.ui.screens.editor.TimestampEditorState
import com.hai265.timestamper.ui.screens.editor.TimestampViewerViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

@RunWith(AndroidJUnit4::class)
class TimestampViewerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var videoRepo: VideoRepository
    private lateinit var timestampRepo: TimestampRepository
    private lateinit var preferencesRepo: PreferencesRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var subject: TimestampViewerViewModel

    private lateinit var db: AppSqlDatabase

    private val videoId = "videoId"
    private val testVideo = Video(
        id = Uuid.fromLongs(1, 1),
        youtubeId = videoId,
        videoTitle = "Test Video",
        thumbnail = "thumb",
        lastEdited = Instant.ZERO,
        lastPlayed = 0.milliseconds
    )
    private val testTimestamps = listOf(
        Timestamp(
            id = Uuid.fromLongs(1, 1),
            videoId = Uuid.fromLongs(1, 1),
            time = 1000.milliseconds,
            description = "First"
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        //TODO: Use powersync driver?
        AppSqlDatabase.Schema.create(driver).await()
        db = AppSqlDatabase(
            driver = driver,
            videosAdapter = Videos.Adapter(
                uuidAdapter, instantAdapter, durationAdapter
            ),
            timestampsAdapter = Timestamps.Adapter(uuidAdapter, uuidAdapter, durationAdapter)
        )

        videoRepo = VideoRepository(
            SqlDelightVideoDao(db),
            SqlDelightTimestampsDao(db),
            FakeYoutubeMetadata(),
        )
        timestampRepo = TimestampRepository(SqlDelightTimestampsDao(db))
        preferencesRepo = PreferencesRepository(context.dataStore)
        savedStateHandle = SavedStateHandle(mapOf("id" to videoId))
        subject = TimestampViewerViewModel(
            savedStateHandle = savedStateHandle,
            videoRepo = videoRepo,
            timestampRepo = timestampRepo,
            preferencesRepository = preferencesRepo,
        )
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