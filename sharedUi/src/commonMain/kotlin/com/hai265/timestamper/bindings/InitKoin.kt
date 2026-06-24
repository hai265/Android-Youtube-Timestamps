package com.hai265.timestamper.bindings

import com.hai265.timestamper.screens.platformModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        includes(config)
        modules(
            appModule,
            platformModule
        )
    }
}