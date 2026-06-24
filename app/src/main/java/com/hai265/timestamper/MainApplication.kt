package com.hai265.timestamper

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.hai265.timestamper.bindings.appModule
import com.hai265.timestamper.data.repos.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MainApplication : Application(), SingletonImageLoader.Factory {
    private val scope: CoroutineScope by inject()

    private val authRepo: AuthRepository by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(appModule)
        }
        scope.launch {
            authRepo.userId.collect { userID ->
                if (userID == null) {
                    authRepo.disconnect()
                } else {
                    authRepo.connect()
                }
            }
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