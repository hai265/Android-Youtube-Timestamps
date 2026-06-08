package com.hai265.timestamper.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import app.cash.sqldelight.db.SqlDriver
import com.hai265.timestamper.Timestamps
import com.hai265.timestamper.Videos
import com.hai265.timestamper.data.AppSqlDatabase
import com.hai265.timestamper.data.database.powersync.powersyncModule
import com.hai265.timestamper.data.network.networkModule
import com.hai265.timestamper.data.repos.AuthRepository
import com.hai265.timestamper.data.repos.PreferencesRepository
import com.hai265.timestamper.data.repos.TimestampRepository
import com.hai265.timestamper.data.repos.VideoRepository
import com.powersync.integrations.sqldelight.PowerSyncDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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
    single {
        TimestampRepository(
            get()
        )
    }
    single { AuthRepository(get(), get()) }
    single { PreferencesRepository(androidContext().dataStore) }

}