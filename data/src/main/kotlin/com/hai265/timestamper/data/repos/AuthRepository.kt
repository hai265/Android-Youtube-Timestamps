package com.hai265.timestamper.data.repos

import co.touchlab.kermit.Logger
import com.hai265.timestamper.data.database.powersync.SupabaseConnector
import com.powersync.PowerSyncDatabase
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.uuid.Uuid

@Singleton
class AuthRepository @Inject constructor(
    private val powerSyncDatabase: PowerSyncDatabase,
    private val supabase: SupabaseConnector,
) {
    var userId: StateFlow<Uuid?> = supabase.sessionStatus.map { status ->
        (status as? SessionStatus.Authenticated)?.session?.user?.id?.let { Uuid.parse(it) }
    }.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    suspend fun signIn(
        email: String,
        password: String,
    ) {
        supabase.login(email, password)
        userId.first { it != null }
    }

    suspend fun signUp(
        email: String,
        password: String,
    ) {
        supabase.signUp(email, password)
    }

    suspend fun signOut() {
        try {
            supabase.signOut()
        } catch (e: Exception) {
            Logger.e("Error signing out: $e")
        }
    }

    suspend fun connect() {
        powerSyncDatabase.connect(supabase)
    }

    suspend fun disconnect() {
        powerSyncDatabase.disconnect()
    }
}