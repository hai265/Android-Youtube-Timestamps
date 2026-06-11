//import app.cash.sqldelight.db.SqlDriver
//import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
//import com.hai265.timestamper.Timestamps
//import com.hai265.timestamper.Videos
//import com.hai265.timestamper.data.AppSqlDatabase
//import com.hai265.timestamper.data.database.SqlDelightVideoDao
//import com.hai265.timestamper.data.database.Timestamp
//import com.hai265.timestamper.data.database.Video
//import com.hai265.timestamper.data.database.durationAdapter
//import com.hai265.timestamper.data.database.instantAdapter
//import com.hai265.timestamper.data.database.uuidAdapter
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.test.runTest
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import kotlin.time.Duration
//import kotlin.time.Instant
//import kotlin.uuid.Uuid
//
//val Instant.Companion.ZERO: Instant
//    get() = Instant.fromEpochSeconds(0L)
//
//class SqlDelightVideosDaoTest {
//
//    private lateinit var db: AppSqlDatabase
//    private lateinit var driver: SqlDriver
//
//    private lateinit var dao: SqlDelightVideoDao
//
//    @Before
//    fun setup() = runBlocking {
//        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
//        AppSqlDatabase.Companion.Schema.create(driver).await()
//        db = AppSqlDatabase.Companion(
//            driver = driver,
//            videosAdapter = Videos.Adapter(
//                uuidAdapter, instantAdapter, durationAdapter
//            ),
//            timestampsAdapter = Timestamps.Adapter(uuidAdapter, uuidAdapter, durationAdapter)
//        )
//
//
//        dao = SqlDelightVideoDao(db)
//    }
//
//    @After
//    fun teardown() {
//        driver.close()
//    }
//
//
//    suspend fun insertTimestamps(videoId: Uuid, vararg timestamps: Timestamp) {
//        timestamps.forEach {
//            db.timestampsQueries.upsertTimestamp(
//                id = it.id,
//                video_id = videoId,
//                time = it.time,
//                description = it.description
//            )
//        }
//    }
//
//    @Test
//    fun `returns empty list when no videos`() = runTest {
//        assert(dao.getAllVideosAndTimestamps().isEmpty())
//    }
//
//    @Test
//    fun `video with no timestamps returns empty timestamp list`() = runTest {
//        dao.addVideo(videoA)
//        val result = dao.getAllVideosAndTimestamps()
//
//        assert(result.size == 1)
//        assertEquals(videoA.youtubeId, result[0].video.youtubeId)
//        assert(result[0].timestamps.isEmpty())
//    }
//
//    //TODO: Fix timestamp tests
//    @Test
//    fun `video with timestamps returns all of them`() = runTest {
//        dao.addVideo(videoA)
//        val ts1 = ts1.copy(videoId = videoA.id)
//        val ts2 = ts2.copy(videoId = videoA.id)
//        insertTimestamps(
//            videoA.id,
//            ts1,
//            ts2
//        )
//
//        val result = dao.getAllVideosAndTimestamps()
//
//        assertEquals(1, result.size)
//        assertEquals(2, result[0].timestamps.size)
//        assertEquals(videoA, result[0].video)
//        assert(result[0].timestamps.containsAll(listOf(ts1, ts2)))
//    }
//
//    @Test
//    fun `multiple videos each get their own timestamps`() = runTest {
//        val ts1 = ts1.copy(videoId = videoA.id)
//        val ts2 = ts2.copy(videoId = videoB.id)
//        val ts3 = ts3.copy(videoId = videoB.id)
//        dao.addVideo(videoA)
//        dao.addVideo(videoB)
//        insertTimestamps(videoA.id, ts1)
//        insertTimestamps(videoB.id, ts2, ts3)
//
//        val result = dao.getAllVideosAndTimestamps()
//            .sortedBy { it.video.id }
//
//        assertEquals(videoA.id, result[0].video.id)
//        assertEquals(1, result[0].timestamps.size)
//
//        assertEquals(videoB.id, result[1].video.id)
//        assertEquals(2, result[1].timestamps.size)
//    }
//
//    @Test
//    fun `timestamps do not bleed across videos`() = runTest {
//        dao.addVideo(videoA)
//        dao.addVideo(videoB)
//        insertTimestamps(videoA.id, ts1)
//
//        val result = dao.getAllVideosAndTimestamps()
//        val videoAResult = result.first { it.video.id == videoA.id }
//        val videoBResult = result.first { it.video.id == videoB.id }
//
//        assertEquals(1, videoAResult.timestamps.size)
//        assert(videoBResult.timestamps.isEmpty())
//    }
//
//    companion object {
//        val videoA = Video(
//            id = Uuid.fromLongs(1L, 1L),
//            youtubeId = "a",
//            videoTitle = "Video A",
//            thumbnail = "thumbnail",
//            lastEdited = Instant.ZERO,
//            lastPlayed = Duration.ZERO
//        )
//        val videoB = Video(
//            id = Uuid.fromLongs(2L, 2L),
//            youtubeId = "b",
//            videoTitle = "Video B",
//            thumbnail = "thumbnail",
//            lastEdited = Instant.ZERO,
//            lastPlayed = Duration.ZERO
//        )
//        val ts1 = Timestamp(
//            id = Uuid.fromLongs(1L, 1L),
//            videoId = Uuid.fromLongs(1L, 1L),
//            time = Duration.ZERO,
//            description = ""
//        )
//        val ts2 = Timestamp(
//            id = Uuid.fromLongs(2L, 2L),
//            videoId = Uuid.fromLongs(1L, 1L),
//            time = Duration.ZERO,
//            description = ""
//        )
//        val ts3 = Timestamp(
//            id = Uuid.fromLongs(3L, 3L),
//            videoId = Uuid.fromLongs(1L, 1L),
//            time = Duration.ZERO,
//            description = ""
//        )
//    }
//}