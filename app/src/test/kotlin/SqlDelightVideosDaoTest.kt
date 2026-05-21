import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Videos
import com.hai265.timestamper.common.ZERO
import com.hai265.timestamper.data.database.SqlDelightVideoDao
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import com.hai265.timestamper.data.database.durationAdapter
import com.hai265.timestamper.data.database.instantAdapter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.Duration
import kotlin.time.Instant

@RunWith(JUnit4::class)
class SqlDelightVideosDaoTest {

    private lateinit var db: AppSqlDatabase
    private lateinit var driver: SqlDriver

    private lateinit var dao: SqlDelightVideoDao

    @Before
    fun setup() = runBlocking {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppSqlDatabase.Schema.create(driver).await()
        db = AppSqlDatabase(
            driver = driver,
            videosAdapter = Videos.Adapter(
                instantAdapter, durationAdapter
            )
        )


        dao = SqlDelightVideoDao(db)
    }

    @After
    fun teardown() {
        driver.close()
    }


    suspend fun insertTimestamps(videoId: String, vararg timestamps: Timestamp) {
        timestamps.forEach {
            db.timestampsQueries.upsertTimestamp(
                id = it.id,
                video_id = videoId,
                time = it.time.inWholeMilliseconds,
                description = it.description
            )
        }
    }

    @Test
    fun `returns empty list when no videos`() = runTest {
        assert(dao.getAllVideosAndTimestamps().isEmpty())
    }

    @Test
    fun `video with no timestamps returns empty timestamp list`() = runTest {
        dao.addVideo(videoA)
        val result = dao.getAllVideosAndTimestamps()

        assert(result.size == 1)
        assertEquals(videoA.youtubeId, result[0].video.youtubeId)
        assert(result[0].timestamps.isEmpty())
    }

    //TODO: Fix timestamp tests
    @Test
    fun `video with timestamps returns all of them`() = runTest {
        dao.addVideo(videoA)
        val ts1 = ts1.copy(videoId = videoA.youtubeId)
        val ts2 = ts2.copy(videoId = videoA.youtubeId)
        insertTimestamps(
            videoA.youtubeId,
            ts1,
            ts2.copy(videoId = videoA.youtubeId)
        )

        val result = dao.getAllVideosAndTimestamps()

        assertEquals(1, result.size)
        assertEquals(2, result[0].timestamps.size)
        assertEquals(videoA, result[0].video)
        assert(result[0].timestamps.containsAll(listOf(ts1, ts2)))
    }

    @Test
    fun `multiple videos each get their own timestamps`() = runTest {
        val ts1 = ts1.copy(videoId = videoA.youtubeId)
        val ts2 = ts2.copy(videoId = videoB.youtubeId)
        val ts3 = ts3.copy(videoId = videoB.youtubeId)
        dao.addVideo(videoA)
        dao.addVideo(videoB)
        insertTimestamps(videoA.youtubeId, ts1)
        insertTimestamps(videoB.youtubeId, ts2, ts3)

        val result = dao.getAllVideosAndTimestamps()
            .sortedBy { it.video.youtubeId }

        assertEquals(videoA.youtubeId, result[0].video.youtubeId)
        assertEquals(1, result[0].timestamps.size)

        assertEquals(videoB.youtubeId, result[1].video.youtubeId)
        assertEquals(2, result[1].timestamps.size)
    }

    @Test
    fun `timestamps do not bleed across videos`() = runTest {
        dao.addVideo(videoA)
        dao.addVideo(videoB)
        insertTimestamps(videoA.youtubeId, ts1)

        val result = dao.getAllVideosAndTimestamps()
        val videoAResult = result.first { it.video.youtubeId == videoA.youtubeId }
        val videoBResult = result.first { it.video.youtubeId == videoB.youtubeId }

        assertEquals(1, videoAResult.timestamps.size)
        assert(videoBResult.timestamps.isEmpty())
    }

    companion object {
        val videoA = Video(
            youtubeId = "a",
            videoTitle = "Video A",
            thumbnail = "thumbnail",
            lastEdited = Instant.ZERO,
            lastPlayed = Duration.ZERO
        )
        val videoB = Video(
            youtubeId = "b",
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