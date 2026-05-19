package com.hai265.timestamper.data.database

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    abstract fun providesVideoDao(dao: SqlDelightVideoDao): VideoDao

    companion object {
        @Provides
        @Singleton
        fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "app_database"
            ).build()
        }

//        @Provides
//        @Singleton
//        fun providesSqlDelightDatabase(@ApplicationContext context: Context): AppSqlDatabase {
//            return AppSqlDatabase(
//                AndroidSqliteDriver(
//                    AppSqlDatabase.Schema, context, "app_database.db"
//                )
//            )
//        }

        @Provides
        fun providesTimestampDao(database: AppDatabase) = database.timestampDao()
    }
}