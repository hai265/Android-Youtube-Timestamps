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
import kotlin.uuid.Uuid

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
        assertEquals(videoA.id, result[0].video.youtubeId)
        assert(result[0].timestamps.isEmpty())
    }

    @Test
    fun `video with timestamps returns all of them`() = runTest {
        dao.addVideo(videoA)
        val ts1 = ts1.copy(videoId = videoA.id)
        val ts2 = ts2.copy(videoId = videoA.id)
        insertTimestamps(
            videoA.id,
            ts1,
            ts2.copy(videoId = videoA.id)
        )

        val result = dao.getAllVideosAndTimestamps()

        assertEquals(1, result.size)
        assertEquals(2, result[0].timestamps.size)
        assertEquals(videoA, result[0].video)
        assert(result[0].timestamps.containsAll(listOf(ts1, ts2)))
    }

    @Test
    fun `multiple videos each get their own timestamps`() = runTest {
        val ts1 = ts1.copy(videoId = videoA.id)
        val ts2 = ts2.copy(videoId = videoB.id)
        val ts3 = ts3.copy(videoId = videoB.id)
        dao.addVideo(videoA)
        dao.addVideo(videoB)
        insertTimestamps(videoA.id, ts1)
        insertTimestamps(videoB.id, ts2, ts3)

        val result = dao.getAllVideosAndTimestamps()
            .sortedBy { it.video.youtubeId }

        assertEquals(videoA.id, result[0].video.youtubeId)
        assertEquals(1, result[0].timestamps.size)

        assertEquals(videoB.id, result[1].video.youtubeId)
        assertEquals(2, result[1].timestamps.size)
    }

    @Test
    fun `timestamps do not bleed across videos`() = runTest {
        dao.addVideo(videoA)
        dao.addVideo(videoB)
        insertTimestamps(videoA.id, ts1)

        val result = dao.getAllVideosAndTimestamps()
        val videoAResult = result.first { it.video.id == videoA.id }
        val videoBResult = result.first { it.video.id == videoB.id }

        assertEquals(1, videoAResult.timestamps.size)
        assert(videoBResult.timestamps.isEmpty())
    }

    // subclasses provide this to insert timestamps directly
    abstract suspend fun insertTimestamps(videoId: Uuid, vararg timestamps: Timestamp)

    companion object {
        val videoA = Video(
            youtubeId = "a",
            id = Uuid.fromLongs(1, 1),
            videoTitle = "Video A",
            thumbnail = "thumbnail",
            lastEdited = Instant.ZERO,
            lastPlayed = Duration.ZERO
        )
        val videoB = Video(
            youtubeId = "b",
            id = Uuid.fromLongs(1, 1),
            videoTitle = "Video B",
            thumbnail = "thumbnail",
            lastEdited = Instant.ZERO,
            lastPlayed = Duration.ZERO
        )
        val ts1 = Timestamp(
            id = Uuid.fromLongs(1, 1),
            videoId = Uuid.fromLongs(1, 1),
            time = Duration.ZERO,
            description = ""
        )
        val ts2 = Timestamp(
            id = Uuid.fromLongs(1, 1),
            videoId = Uuid.fromLongs(1, 1),
            time = Duration.ZERO,
            description = ""
        )
        val ts3 = Timestamp(
            id = Uuid.fromLongs(1, 1),
            videoId = Uuid.fromLongs(1, 1),
            time = Duration.ZERO,
            description = ""
        )
    }
}