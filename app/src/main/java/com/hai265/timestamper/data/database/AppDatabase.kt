package com.hai265.timestamper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Video::class, Timestamp::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
}