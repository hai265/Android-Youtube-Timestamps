package com.hai265.timestamper.screens

import kotlinx.serialization.Serializable

sealed interface Navigables {
    @Serializable
    object ListScreen : Navigables

    @Serializable
    data class VideoScreen(val id: String) : Navigables

    @Serializable
    object SignUpScreen : Navigables

    @Serializable
    object LogInScreen : Navigables
}
