package com.hai265.timestamper

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.crossfade
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class MainApplication : Application(), SingletonImageLoader.Factory {
    @Inject
    lateinit var scope: CoroutineScope
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