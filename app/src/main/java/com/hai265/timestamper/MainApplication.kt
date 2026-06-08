package com.hai265.timestamper

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.hai265.timestamper.bindings.appModule
import com.hai265.timestamper.data.database.dataModule
import com.hai265.timestamper.data.repos.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class MainApplication : Application(), SingletonImageLoader.Factory {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var authRepo: AuthRepository

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            authRepo.userId.collect { userID ->
                if (userID == null) {
                    authRepo.disconnect()
                } else {
                    authRepo.connect()
                }
            }
        }
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(appModule, dataModule)
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
//            .diskCache {
//                DiskCache.Builder()
//                    .directory(cacheDir.resolve("coil_cache").toOkioPath())
//                    .maxSizePercent(0.02)
//                    .build()
//            }
            .build()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {
    @Singleton
    @Provides
    fun providesApplicationCoroutineScope(
    ): CoroutineScope = CoroutineScope(SupervisorJob())
}