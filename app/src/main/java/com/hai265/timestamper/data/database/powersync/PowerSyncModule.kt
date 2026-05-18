package com.hai265.timestamper.data.database.powersync

import android.content.Context
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PowerSyncModule {

    fun providesPowersyncDatabase(
        @ApplicationContext context: Context,
    ): PowerSyncDatabase {
        // Android
        val driverFactory = DatabaseDriverFactory(context)

        val database = PowerSyncDatabase(
            factory = driverFactory,
            schema = schema,
            dbFilename = "powersync.db"
        )
        return database
    }
}