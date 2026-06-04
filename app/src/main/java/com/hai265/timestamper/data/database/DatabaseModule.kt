package com.hai265.timestamper.data.database

import app.cash.sqldelight.db.SqlDriver
import com.hai265.timestamper.AppSqlDatabase
import com.hai265.timestamper.Timestamps
import com.hai265.timestamper.Videos
import com.powersync.PowerSyncDatabase
import com.powersync.integrations.sqldelight.PowerSyncDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {
    companion object {
        @Provides
        @Singleton
        fun provideSqlDriver(
            powerSyncDatabase: PowerSyncDatabase,
            externalScope: CoroutineScope,
        ): SqlDriver {
            return PowerSyncDriver(powerSyncDatabase, externalScope)
        }

        @Provides
        fun providesTimestampDao(driver: SqlDriver): TimestampDao {
            return SqlDelightTimestampsDao(
                //TODO: Create AppSqlDatabase once
                AppSqlDatabase(
                    driver = driver,
                    videosAdapter = Videos.Adapter(
                        uuidAdapter,
                        uuidAdapter,
                        instantAdapter,
                        durationAdapter
                    ),
                    timestampsAdapter = Timestamps.Adapter(
                        uuidAdapter,
                        uuidAdapter,
                        durationAdapter
                    ),
                )
            )
        }

        @Provides
        fun providesVideoDao(driver: SqlDriver): VideoDao {
            return SqlDelightVideoDao(
                AppSqlDatabase(
                    driver = driver,
                    videosAdapter = Videos.Adapter(
                        uuidAdapter,
                        uuidAdapter,
                        instantAdapter,
                        durationAdapter
                    ),
                    timestampsAdapter = Timestamps.Adapter(
                        uuidAdapter,
                        uuidAdapter,
                        durationAdapter
                    )
                )
            )
        }
    }
}