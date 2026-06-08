package com.hai265.timestamper.ui.screens.signin

import android.util.Log
import androidx.lifecycle.ViewModel
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
            Log.e("AuthViewModel", "\"Error signing out: \$e")
        }
    }
}
