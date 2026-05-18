package com.hai265.timestamper.data.database.powersync

import com.powersync.PowerSyncDatabase
import javax.inject.Inject

class PowerSyncVideoRepo @Inject constructor(
    private val database: PowerSyncDatabase,
    private val connector: MyConnector
) {
    
}