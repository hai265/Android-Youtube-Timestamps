package com.hai265.timestamper.data.database.powersync

import co.touchlab.kermit.Logger
import com.hai265.timestamper.data.database.AppDatabase
import com.powersync.PowerSyncDatabase
import com.powersync.db.schema.Schema
import com.powersync.integrations.room.RoomConnectionPool
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
abstract class PowerSyncModule {
    companion object {
        @Provides
        fun providesPowerSyncDatabase(
            appDatabase: AppDatabase,
            externalScope: CoroutineScope,
        ): PowerSyncDatabase {
            val schema = Schema()
            val pool = RoomConnectionPool(appDatabase, schema)

            val powersync = PowerSyncDatabase.opened(
                pool = pool,
                scope = externalScope,
                schema = schema,
                identifier = "app_database", // Prefer to use the same path/name as your Room database
                logger = Logger,
            )
            return powersync
        }
    }
}