package com.hai265.timestamper.data.repos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun providesVideoRepository(repo: VideoRepositoryImpl): VideoRepository

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        @Provides
        @Singleton
        fun providesPreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
            val dataStore = context.dataStore
            return PreferencesRepository(dataStore)
        }
    }

}