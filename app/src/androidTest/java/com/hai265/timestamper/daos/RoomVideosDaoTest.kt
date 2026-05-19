package com.hai265.timestamper.daos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hai265.timestamper.data.database.AppDatabase
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.VideoDao
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomVideosDaoTest : VideosDaoContract() {
    private lateinit var database: AppDatabase


    override fun createDao(): VideoDao {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()


        return database.videoDao()
    }

    override fun clearDao() {
        database.close()
    }


    override suspend fun insertTimestamps(videoId: String, vararg timestamps: Timestamp) {
        timestamps.forEach {
            database.timestampDao().upsertTimestamp(
                it
            )
        }
    }
}