package com.hai265.timestamper.domain

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {
    companion object {
        @Provides
        @Singleton
        fun providesContentResolver(@ApplicationContext context: Context): ContentResolver {
            return context.contentResolver
        }
    }
}