package com.hai265.timestamper.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    storage = FileStorage(
        serializer = PreferencesFileSerializer,
        produceFile = { context.filesDir.resolve(dataStoreFileName) }
    )
)

actual val preferencesModule = module {
    single {
        createDataStore(androidContext())
    }
}


