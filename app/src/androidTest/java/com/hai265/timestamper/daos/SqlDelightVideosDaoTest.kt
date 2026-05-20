package com.hai265.timestamper.daos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Videos
import com.hai265.timestamper.data.database.SqlDelightVideoDao
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.VideoDao
import com.hai265.timestamper.data.database.durationAdapter
import com.hai265.timestamper.data.database.instantAdapter
import com.hai265.timestamper.data.database.powersync.schema
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.integrations.sqldelight.PowerSyncDriver
import kotlinx.coroutines.test.TestScope
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SqlDelightVideosDaoTest : VideosDaoContract() {

    private lateinit var db: AppSqlDatabase
    private lateinit var driver: PowerSyncDriver

    private val testScope = TestScope()

    override fun createDao(): VideoDao {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val driverFactory = DatabaseDriverFactory(context)

        val powersyncDatabase = PowerSyncDatabase(
            factory = driverFactory,
            schema = schema,
            dbFilename = "app_database"
        )

        driver = PowerSyncDriver(powersyncDatabase, testScope)
        db = AppSqlDatabase(
            driver = driver,
            videosAdapter = Videos.Adapter(
                instantAdapter, durationAdapter
            )
        )


        return SqlDelightVideoDao(db)
    }

    override fun clearDao() {
        driver.close()
    }


    override suspend fun insertTimestamps(videoId: String, vararg timestamps: Timestamp) {
        timestamps.forEach {
            db.timestampsQueries.upsertTimestamp(
                id = it.id,
                video_id = it.videoId,
                time = it.time.inWholeMilliseconds,
                description = it.description
            )
        }
    }
}