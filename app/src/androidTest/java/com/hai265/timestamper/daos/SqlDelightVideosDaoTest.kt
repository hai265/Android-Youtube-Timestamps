package com.hai265.timestamper.daos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.data.database.SqlDelightVideoDao
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.VideoDao
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SqlDelightVideosDaoTest : VideosDaoContract() {

    private lateinit var db: AppSqlDatabase
    private lateinit var driver: AndroidSqliteDriver


    override fun createDao(): VideoDao {
        val context = ApplicationProvider.getApplicationContext<Context>()
        driver = AndroidSqliteDriver(AppSqlDatabase.Schema, context, null)
        db = AppSqlDatabase(driver)


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