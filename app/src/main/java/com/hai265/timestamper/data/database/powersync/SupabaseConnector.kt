package com.hai265.timestamper.data.database.powersync

import co.touchlab.kermit.Logger
import com.hai265.timestamper.BuildConfig
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import com.powersync.connectors.PowerSyncCredentials
import com.powersync.db.crud.CrudEntry
import com.powersync.db.crud.CrudTransaction
import com.powersync.db.crud.UpdateType
import com.powersync.db.runWrapped
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

/**
 * Get a Supabase token to authenticate against the PowerSync instance.
 */
@OptIn(SupabaseInternal::class, InternalAPI::class)
public open class SupabaseConnector(
    public val supabaseClient: SupabaseClient,
    public val powerSyncEndpoint: String,
    private val storageBucket: String? = null,
) : PowerSyncBackendConnector() {
    private val json = Json { coerceInputValues = true }
    private var errorCode: String? = null

    public companion object PostgresFatalCodes {
        // Using Regex patterns for Postgres error codes
        private val FATAL_RESPONSE_CODES =
            listOf(
                // Class 22 — Data Exception
                "^22...".toRegex(),
                // Class 23 — Integrity Constraint Violation
                "^23...".toRegex(),
                // INSUFFICIENT PRIVILEGE
                "^42501$".toRegex(),
            )

        public fun isFatalError(code: String): Boolean =
            FATAL_RESPONSE_CODES.any { pattern ->
                pattern.matches(code)
            }
    }

    public fun storageBucket(): BucketApi {
        if (storageBucket == null) {
            throw Exception("No bucket has been specified")
        }
        return supabaseClient.storage[storageBucket]
    }

    public constructor(
        supabaseUrl: String,
        supabaseKey: String,
        powerSyncEndpoint: String,
        storageBucket: String? = null,
    ) : this(
        supabaseClient =
            createSupabaseClient(supabaseUrl, supabaseKey) {
                install(Auth.Companion)
                install(Postgrest.Companion)
                if (storageBucket != null) {
                    install(Storage.Companion)
                }
            },
        powerSyncEndpoint = powerSyncEndpoint,
        storageBucket = storageBucket,
    )

    init {
        require(supabaseClient.pluginManager.getPluginOrNull(Auth.Companion) != null) { "The Auth plugin must be installed on the Supabase client" }
        require(
            supabaseClient.pluginManager.getPluginOrNull(Postgrest.Companion) != null,
        ) { "The Postgrest plugin must be installed on the Supabase client" }

        // This retrieves the error code from the response
        // as this is not accessible in the Supabase client RestException
        // to handle fatal Postgres errors
        supabaseClient.httpClient.httpClient.plugin(HttpSend.Plugin).intercept { request ->
            val resp = execute(request)
            val response = resp.response
            if (response.status.value >= 400) {
                val responseText = response.bodyAsText()

                try {
                    val error =
                        json.decodeFromString<Map<String, String?>>(
                            responseText,
                        )
                    errorCode = error["code"]
                } catch (e: Exception) {
                    Logger.Companion.e("Failed to parse error response: $e")
                }
            }
            resp
        }
    }

    public suspend fun login(
        email: String,
        password: String,
    ) {
        runWrapped {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    public suspend fun signUp(
        email: String,
        password: String,
    ) {
        runWrapped {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    public suspend fun signOut() {
        runWrapped {
            supabaseClient.auth.signOut()
        }
    }

    public fun session(): UserSession? = supabaseClient.auth.currentSessionOrNull()

    public val sessionStatus: StateFlow<SessionStatus> = supabaseClient.auth.sessionStatus

    public suspend fun loginAnonymously() {
        runWrapped {
            supabaseClient.auth.signInAnonymously()
        }
    }

    /**
     * Get credentials for PowerSync.
     */
    override suspend fun fetchCredentials(): PowerSyncCredentials =
        runWrapped {
            return PowerSyncCredentials(
                endpoint = BuildConfig.POWERSYNC_ENDPOINT,
                token = BuildConfig.POWERSYNC_TOKEN,
            )
//            check(supabaseClient.auth.sessionStatus.value is SessionStatus.Authenticated) { "Supabase client is not authenticated" }
//
//            // Use Supabase token for PowerSync
//            val session =
//                supabaseClient.auth.currentSessionOrNull()
//                    ?: error("Could not fetch Supabase credentials")
//
//            check(session.user != null) { "No user data" }
//
//            PowerSyncCredentials(
//                endpoint = powerSyncEndpoint,
//                token = session.accessToken, // Use the access token to authenticate against PowerSync
//            )
        }

    /**
     * Uses the PostgREST APIs to upload a given [entry] to the backend database.
     *
     * This method should report errors during the upload as an exception that would be caught by [uploadData].
     */
    public open suspend fun uploadCrudEntry(entry: CrudEntry) {
        val table = supabaseClient.from(entry.table)

        when (entry.op) {
            UpdateType.PUT -> {
                val data =
                    buildMap {
                        put("id", JsonPrimitive(entry.id))
                        entry.opData?.jsonValues?.let { putAll(it) }
                    }
                table.upsert(data)
            }

            UpdateType.PATCH -> {
                table.update(entry.opData!!.jsonValues) {
                    filter {
                        eq("id", entry.id)
                    }
                }
            }

            UpdateType.DELETE -> {
                table.delete {
                    filter {
                        eq("id", entry.id)
                    }
                }
            }
        }
    }

    /**
     * Handles an error during the upload. This method can be overridden to log errors or customize error handling.
     *
     * By default, it discards the rest of a transaction when the error code indicates that this is a fatal postgres
     * error that can't be retried. Otherwise, it rethrows the exception so that the PowerSync SDK will retry.
     *
     * @param tx The full [com.powersync.db.crud.CrudTransaction] we're in the process of uploading.
     * @param entry The [CrudEntry] for which an upload has failed.
     * @param exception The [Exception] thrown by the Supabase client.
     * @param [errorCode] The postgres error code, if any.
     * @throws Exception If the upload should be retried. If this method doesn't throw, it should mark [tx] as complete
     * by invoking [com.powersync.db.crud.CrudTransaction.complete]. In that case, the local write would be lost.
     */
    public open suspend fun handleError(
        tx: CrudTransaction,
        entry: CrudEntry,
        exception: Exception,
        errorCode: String?,
    ) {
        if (errorCode != null && isFatalError(errorCode)) {
            /**
             * Instead of blocking the queue with these errors,
             * discard the (rest of the) transaction.
             *
             * Note that these errors typically indicate a bug in the application.
             * If protecting against data loss is important, save the failing records
             * elsewhere instead of discarding, and/or notify the user.
             */
            Logger.Companion.e("Data upload error: ${exception.message}")
            Logger.Companion.e("Discarding entry: $entry")
            tx.complete(null)
            return
        }

        Logger.Companion.e("Data upload error - retrying last entry: $entry, $exception")
        throw exception
    }

    /**
     * Upload local changes to the app backend (in this case Supabase).
     *
     * This function is called whenever there is data to upload, whether the device is online or offline.
     * If this call throws an error, it is retried periodically.
     */
    override suspend fun uploadData(database: PowerSyncDatabase) {
        return runWrapped {
            val transaction = database.getNextCrudTransaction() ?: return@runWrapped

            var lastEntry: CrudEntry? = null
            try {
                for (entry in transaction.crud) {
                    lastEntry = entry
                    uploadCrudEntry(entry)
                }

                transaction.complete(null)
            } catch (e: Exception) {
                if (lastEntry != null) {
                    handleError(transaction, lastEntry, e, errorCode)
                } else {
                    throw e
                }
            }
        }
    }
}