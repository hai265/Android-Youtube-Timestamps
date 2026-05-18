package com.hai265.timestamper.data.database.powersync

import com.hai265.timestamper.BuildConfig
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import com.powersync.connectors.PowerSyncCredentials
import javax.inject.Inject

class MyConnector @Inject constructor() : PowerSyncBackendConnector() {
    override suspend fun fetchCredentials(): PowerSyncCredentials {
        // for development: use development token
        return PowerSyncCredentials(
            endpoint = BuildConfig.POWERSYNC_ENDPOINT,
            token = BuildConfig.POWERSYNC_TOKEN,
        )
    }

    override suspend fun uploadData(database: PowerSyncDatabase) {
        val transaction = database.getNextCrudTransaction() ?: return

        for (op in transaction.crud) {
            val record = op.opData?.plus(("id" to op.id))
            // upload to your backend API
        }

        transaction.complete(null)
    }
}