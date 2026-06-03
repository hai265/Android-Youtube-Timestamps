package com.hai265.timestamper.data.repos

import co.touchlab.kermit.Logger
import com.hai265.timestamper.data.database.powersync.SupabaseConnector
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val supabase: SupabaseConnector,
) {
    val currentUserIdFlow: Flow<String?> = supabase.sessionStatus.map {
        when (it) {
            is SessionStatus.Authenticated -> it.session.user?.id
            else -> null
        }
    }

    suspend fun signIn(
        email: String,
        password: String,
    ) {
        supabase.login(email, password)
    }

    suspend fun signUp(
        email: String,
        password: String,
    ) {
        //TODO: Disabled because supabase is rate limited
//        supabase.signUp(email, password)
    }

    suspend fun signOut() {
        try {
            supabase.signOut()
        } catch (e: Exception) {
            Logger.e("Error signing out: $e")
        }
    }
}