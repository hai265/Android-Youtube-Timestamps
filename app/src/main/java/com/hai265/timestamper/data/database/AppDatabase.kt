package com.hai265.timestamper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Database(entities = [Video::class, Timestamp::class], version = 1, exportSchema = false)
@TypeConverters(DurationConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao

    abstract fun timestampDao(): TimestampDao
}


class DurationConverter {
    @TypeConverter
    fun fromDuration(duration: Duration?): Long? {
        return duration?.inWholeMilliseconds
    }

    @TypeConverter
    fun toDuration(value: Long?): Duration? {
        return value?.milliseconds
    }
}