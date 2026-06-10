package com.hai265.timestamper.data.database

import app.cash.sqldelight.db.SqlDriver
import com.hai265.timestamper.Timestamps
import com.hai265.timestamper.Videos
import com.hai265.timestamper.data.AppSqlDatabase
import com.hai265.timestamper.data.network.networkModule
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.powersync.integrations.sqldelight.PowerSyncDriver
import org.koin.dsl.module

val kmpDataModule = module {
    includes(networkModule)
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
    single {
        TimestampRepository(
            get()
        )
    }
//    single { AuthRepository(get(), get()) }
//    single { PreferencesRepository(androidContext().dataStore) }

}