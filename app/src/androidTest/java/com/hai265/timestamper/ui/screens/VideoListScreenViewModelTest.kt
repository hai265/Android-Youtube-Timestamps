package com.hai265.timestamper.ui.screens

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Timestamps
import com.hai265.timestamper.Videos
import com.hai265.timestamper.data.database.SqlDelightTimestampsDao
import com.hai265.timestamper.data.database.SqlDelightVideoDao
import com.hai265.timestamper.data.database.durationAdapter
import com.hai265.timestamper.data.database.instantAdapter
import com.hai265.timestamper.data.database.uuidAdapter
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.domain.ExportTimestampsToFileUseCase
import com.hai265.timestamper.domain.ImportTimestampsFromFileUseCase
import com.hai265.timestamper.ui.screens.list.VideoListScreenViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoListScreenViewModelTest {
    private lateinit var driver: SqlDriver
    private lateinit var db: AppSqlDatabase

    private lateinit var videoRepository: VideoRepository
    private lateinit var timestampRepository: TimestampRepository
    private lateinit var subject: VideoListScreenViewModel


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

        db = AppSqlDatabase(
            driver = driver,
            videosAdapter = Videos.Adapter(
                uuidAdapter, instantAdapter, durationAdapter
            ),
            timestampsAdapter = Timestamps.Adapter(uuidAdapter, uuidAdapter, durationAdapter)
        )

        videoRepository = VideoRepository(
            videoDao = SqlDelightVideoDao(db),
            youtubeMetadataApi = FakeYoutubeMetadata(),
            timestmapDao = SqlDelightTimestampsDao(db),
        )
        timestampRepository = TimestampRepository(
            timestampDao = SqlDelightTimestampsDao(db)
        )
        subject = VideoListScreenViewModel(
            videoRepository,
            timestampRepository,
            ImportTimestampsFromFileUseCase(
                timestampRepository,
                videoRepository,
                context.contentResolver
            ),
            ExportTimestampsToFileUseCase(
                videoRepository,
                context.contentResolver
            ),
        )
    }

    @After
    fun tearDown() {
        driver.close()
    }

    @Test
    fun init_test() {
        subject
    }
}