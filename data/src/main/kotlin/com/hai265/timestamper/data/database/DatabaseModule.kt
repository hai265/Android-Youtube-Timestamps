package com.hai265.timestamper.data.database

import app.cash.sqldelight.db.SqlDriver
import com.hai265.timestamper.Timestamps
import com.hai265.timestamper.Videos
import com.hai265.timestamper.data.AppSqlDatabase
import com.hai265.timestamper.data.database.powersync.powersyncModule
import com.hai265.timestamper.data.domain.TestUseCase
import com.hai265.timestamper.data.network.networkModule
import com.hai265.timestamper.data.repos.VideoRepository
import com.powersync.PowerSyncDatabase
import com.powersync.integrations.sqldelight.PowerSyncDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import javax.inject.Singleton

//Dumb holder I have to use since ksp can't recognize AppSqlDatabase.
class DatabaseHolder(
    val database: AppSqlDatabase
)

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
        @Singleton
        fun provideDatabaseHolder(driver: SqlDriver): DatabaseHolder {
            return DatabaseHolder(
                AppSqlDatabase(
                    driver = driver,
                    videosAdapter = Videos.Adapter(
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

        @Provides
        fun providesTimestampDao(databaseHolder: DatabaseHolder): TimestampDao {
            return SqlDelightTimestampsDao(
                databaseHolder.database
            )
        }

        @Provides
        fun providesVideoDao(databaseHolder: DatabaseHolder): VideoDao {
            return SqlDelightVideoDao(
                databaseHolder.database
            )
        }
    }
}

val dataModule = module {
    includes(networkModule, powersyncModule)
    single<SqlDriver> {
        PowerSyncDriver(
            db = get(),
            scope = get()
        )
    }
    single {
        AppSqlDatabase(
            driver = get<SqlDriver>(),
            videosAdapter = Videos.Adapter(
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
    }

    single<TimestampDao> {
        SqlDelightTimestampsDao(database = get())
    }

    single<VideoDao> {
        SqlDelightVideoDao(database = get())
    }
    single {
        VideoRepository(
            videoDao = get(),
            timestmapDao = get(),
            youtubeMetadataApi = get(),
        )
    }
    factory {
        TestUseCase(videoRepository = get())
    }
}