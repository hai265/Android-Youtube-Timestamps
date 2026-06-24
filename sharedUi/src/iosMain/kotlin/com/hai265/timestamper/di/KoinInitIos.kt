package com.hai265.timestamper.di

import com.hai265.timestamper.screens.platformModule
import org.koin.core.context.startKoin

fun initKoinIos() {
    startKoin {
        modules(

            platformModule
        )
    }
}