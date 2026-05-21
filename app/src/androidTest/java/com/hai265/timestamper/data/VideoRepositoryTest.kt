package com.hai265.timestamper.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hai265.timestamper.data.database.AppDatabase
import com.hai265.timestamper.data.database.VideoWithTimestamps
import com.hai265.timestamper.data.network.YoutubeMetadata
import com.hai265.timestamper.data.network.YoutubeMetadataApiService
import com.hai265.timestamper.data.repos.VideoRepository
import com.hai265.timestamper.data.repos.VideoResult
import com.hai265.timestamper.ui.fakes.fakeTimestamp1
import com.hai265.timestamper.ui.fakes.fakeTimestamp2
import com.hai265.timestamper.ui.fakes.fakeTimestamp3
import com.hai265.timestamper.ui.fakes.fakeVideo1
import com.hai265.timestamper.ui.fakes.fakeVideo2
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response
import kotlin.jvm.java


const val VIDEO_URL = "https://www.youtube.com/watch?v=videoid"

@RunWith(AndroidJUnit4::class)
class VideoRepositoryTest {
    private lateinit var youtubeMetaApi: FakeYoutubeMetadataApiService
    private lateinit var database: AppDatabase

    private lateinit var subject: VideoRepository


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        youtubeMetaApi = FakeYoutubeMetadataApiService()

        subject = VideoRepository(
            videoDao = database.videoDao(),
            timestmapDao = database.timestampDao(),
            youtubeMetadataApi = youtubeMetaApi,
            database = database,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAddVideo_Success() = runTest {
        val result = subject.addVideo(VIDEO_URL)
        val video = subject.getVideos().first().first()
        assert(result is VideoResult.Success)
        assertEquals("title", video.videoTitle)
        assertEquals("thumbnail", video.thumbnail)
    }

    @Test
    fun testAddVideo_IoExceptionReturnsNetworkError() = runTest {
        youtubeMetaApi.shouldThrow = kotlinx.io.IOException("error message")

        val result = subject.addVideo(VIDEO_URL)

        assert(result is VideoResult.NetworkError)
        assertEquals("error message", (result as VideoResult.NetworkError).errorMessage)
    }

    @Test
    fun testAddVideo_InvalidUrl() = runTest {
        youtubeMetaApi.shouldThrow = kotlinx.io.IOException("error message")

        val result = subject.addVideo("invalid_url")

        assert(result is VideoResult.InvalidUrl)
        assertEquals("invalid_url", (result as VideoResult.InvalidUrl).url)
    }

    @Test
    fun testAddVideo_NetworkExceptionReturnsNetworkError() = runTest {
        youtubeMetaApi.shouldThrow = HttpException(
            Response.error<String>(
                404,
                "".toResponseBody()
            )
        )

        val result = subject.addVideo(VIDEO_URL)

        assert(result is VideoResult.NetworkError)
        assertEquals("HTTP 404 Response.error()", (result as VideoResult.NetworkError).errorMessage)
    }

    @Test
    fun testAddVideo_NetworkException400ReturnsNetworkErrorVideoDoesntExist() = runTest {
        youtubeMetaApi.shouldThrow = HttpException(
            Response.error<String>(
                400,
                "".toResponseBody()
            )
        )

        val result = subject.addVideo(VIDEO_URL)

        assert(result is VideoResult.NetworkError)
        assertEquals("Video Doesn't Exist", (result as VideoResult.NetworkError).errorMessage)
    }

    @Test
    fun testAddVideo_NetworkException403ReturnsNetworkErrorVideoIsPrivate() = runTest {
        youtubeMetaApi.shouldThrow = HttpException(
            Response.error<String>(
                403,
                "".toResponseBody()
            )
        )

        val result = subject.addVideo(VIDEO_URL)

        assert(result is VideoResult.NetworkError)
        assertEquals("Video Is Private", (result as VideoResult.NetworkError).errorMessage)
    }

    @Test
    fun testAddVideo_videoAlreadyExists_returnsVideoAlreadyExists() = runTest {
        subject.addVideo(VIDEO_URL)
        val result = subject.addVideo(VIDEO_URL)

        assert(result is VideoResult.VideoAlreadyExists)
    }

    @Test
    fun testImportVideosWithTimestamps_appearInRoomDatabase() = runTest {
        subject.importVideosWithTimestamps(videoWithTimestamps)

        assertEquals(videoWithTimestamps, subject.getVideosWithTimestamps())
    }

    @Test
    fun testImportVideosExistingVideoAlreadyExistNotReplaced() = runTest {
        // Add video1 via network — its metadata comes from FakeYoutubeMetadataApiService,
        // so the stored version will differ from fakeVideo1's hardcoded fields.
        subject.addVideo(youtubeUrlFromId(fakeVideo1.youtubeId))
        val networkFetchedVideo1 = subject.getVideoById(fakeVideo1.youtubeId)

        // Sanity check: confirm the stored video differs from the fakeVideo1 fixture,
        // proving that importing fakeVideo1 would actually be a meaningful change if allowed.
        assertNotEquals(
            "Precondition failed: network-fetched video1 should differ from fakeVideo1 fixture",
            fakeVideo1,
            networkFetchedVideo1
        )

        // Import a list that includes fakeVideo1 (with different data) and a new fakeVideo2.
        subject.importVideosWithTimestamps(videoWithTimestamps)

        // video1 already existed — import should NOT overwrite it.
        val videoAfterImport = subject.getVideoById(fakeVideo1.youtubeId)
        assertEquals(
            "Existing video1 should not be replaced by import",
            networkFetchedVideo1,
            videoAfterImport
        )

        // video2 did not exist before import — it should be inserted as-is.
        assertEquals(
            "New video2 should be inserted exactly as provided",
            fakeVideo2,
            subject.getVideoById(fakeVideo2.youtubeId)
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun timestampDaoExceptionNothingImported() = runTest {
        val invalidTimestamp = fakeTimestamp1.copy(
            videoId = "nonexistent-video-id"
        )

        val data = listOf(
            VideoWithTimestamps(
                video = fakeVideo1,
                timestamps = listOf(invalidTimestamp)
            )
        )

        subject.importVideosWithTimestamps(data)


        val videoWithTimestamps = subject.getVideosWithTimestamps()

        assertTrue(videoWithTimestamps.isEmpty())

    }

    companion object {
        val videoWithTimestamps = listOf(
            VideoWithTimestamps(
                video = fakeVideo1,
                timestamps = listOf(fakeTimestamp1.copy(videoId = fakeVideo1.youtubeId))
            ),
            VideoWithTimestamps(
                video = fakeVideo2,
                timestamps = listOf(
                    fakeTimestamp2.copy(videoId = fakeVideo2.youtubeId),
                    fakeTimestamp3.copy(videoId = fakeVideo2.youtubeId)
                )
            )
        )
    }
}

class FakeYoutubeMetadataApiService : YoutubeMetadataApiService {

    var shouldThrow: Throwable? = null

    var response = YoutubeMetadata(
        title = "title",
        thumbnail = "thumbnail"
    )

    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadata {
        shouldThrow?.let { throw it }
        return response
    }
}


fun youtubeUrlFromId(videoId: String): String {
    return "https://www.youtube.com/watch?v=$videoId"
}