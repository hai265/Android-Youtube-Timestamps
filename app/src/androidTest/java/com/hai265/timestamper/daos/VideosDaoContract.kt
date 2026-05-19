package com.hai265.timestamper.daos;

import com.hai265.timestamper.common.ZERO
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.VideoDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Instant

abstract class VideosDaoContract {

    abstract fun createDao(): VideoDao

    private lateinit var dao: VideoDao

    @Before
    fun setup() {
        dao = createDao()

    }

    abstract fun clearDao()

    @After
    fun teardown() {
        clearDao()
    }

    // ── getAllVideosAndTimestamps ──────────────────────────────────────────

    @Test
    fun `returns empty list when no videos`() = runTest {
        assert(dao.getAllVideosAndTimestamps().isEmpty())
    }

    @Test
    fun `video with no timestamps returns empty timestamp list`() = runTest {
        dao.addVideo(videoA)
        val result = dao.getAllVideosAndTimestamps()

        assert(result.size == 1)
        assertEquals(videoA.videoId, result[0].video.videoId)
        assert(result[0].timestamps.isEmpty())
    }

    @Test
    fun `video with timestamps returns all of them`() = runTest {
        dao.addVideo(videoA)
        val ts1 = ts1.copy(videoId = videoA.videoId)
        val ts2 = ts2.copy(videoId = videoA.videoId)
        insertTimestamps(
            videoA.videoId,
            ts1,
            ts2.copy(videoId = videoA.videoId)
        )

        val result = dao.getAllVideosAndTimestamps()

        assertEquals(1, result.size)
        assertEquals(2, result[0].timestamps.size)
        assertEquals(videoA, result[0].video)
        assert(result[0].timestamps.containsAll(listOf(ts1, ts2)))
    }

    @Test
    fun `multiple videos each get their own timestamps`() = runTest {
        val ts1 = ts1.copy(videoId = videoA.videoId)
        val ts2 = ts2.copy(videoId = videoB.videoId)
        val ts3 = ts3.copy(videoId = videoB.videoId)
        dao.addVideo(videoA)
        dao.addVideo(videoB)
        insertTimestamps(videoA.videoId, ts1)
        insertTimestamps(videoB.videoId, ts2, ts3)

        val result = dao.getAllVideosAndTimestamps()
            .sortedBy { it.video.videoId }

        assertEquals(videoA.videoId, result[0].video.videoId)
        assertEquals(1, result[0].timestamps.size)

        assertEquals(videoB.videoId, result[1].video.videoId)
        assertEquals(2, result[1].timestamps.size)
    }

    @Test
    fun `timestamps do not bleed across videos`() = runTest {
        dao.addVideo(videoA)
        dao.addVideo(videoB)
        insertTimestamps(videoA.videoId, ts1)

        val result = dao.getAllVideosAndTimestamps()
        val videoAResult = result.first { it.video.videoId == videoA.videoId }
        val videoBResult = result.first { it.video.videoId == videoB.videoId }

        assertEquals(1, videoAResult.timestamps.size)
        assert(videoBResult.timestamps.isEmpty())
    }

    // subclasses provide this to insert timestamps directly
    abstract suspend fun insertTimestamps(videoId: String, vararg timestamps: Timestamp)

    companion object {
        val videoA = Video(
            videoId = "a",
            videoTitle = "Video A",
            thumbnail = "thumbnail",
            lastEdited = Instant.ZERO,
            lastPlayed = Duration.ZERO
        )
        val videoB = Video(
            videoId = "b",
            videoTitle = "Video B",
            thumbnail = "thumbnail",
            lastEdited = Instant.ZERO,
            lastPlayed = Duration.ZERO
        )
        val ts1 = Timestamp(id = 1L, videoId = "a", time = Duration.ZERO, description = "")
        val ts2 = Timestamp(id = 2L, time = Duration.ZERO, description = "")
        val ts3 = Timestamp(id = 3L, time = Duration.ZERO, description = "")
    }
}