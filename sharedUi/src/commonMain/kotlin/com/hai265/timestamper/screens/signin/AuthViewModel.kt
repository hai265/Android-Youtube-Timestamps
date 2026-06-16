package com.hai265.timestamper.screens.signin

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import com.hai265.timestamper.data.repos.AuthRepository

sealed class AuthState {
    data object SignedOut : AuthState()

    data object SignedIn : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    suspend fun signIn(
        email: String,
        password: String,
    ) {
        authRepository.signIn(email, password)
    }

    suspend fun signUp(
        email: String,
        password: String,
    ) {
        authRepository.signUp(email, password)
    }

    suspend fun signOut() {
        try {
            authRepository.signOut()
        } catch (e: Exception) {
            Logger.e($$"\"Error signing out: $e", e, "AuthViewModel")
        }
    }
}
