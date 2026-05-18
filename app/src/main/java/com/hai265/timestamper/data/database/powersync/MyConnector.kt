package com.hai265.timestamper.data.database.powersync

import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import com.powersync.connectors.PowerSyncCredentials
import javax.inject.Inject

class MyConnector @Inject constructor() : PowerSyncBackendConnector() {
    override suspend fun fetchCredentials(): PowerSyncCredentials {
        // for development: use development token
        return PowerSyncCredentials(
            endpoint = "https://6a06bbf6234fa2bf51a2ccd2.powersync.journeyapps.com",
            token = "eyJhbGciOiJSUzI1NiIsImtpZCI6InBvd2Vyc3luYy1kZXYtMzIyM2Q0ZTMifQ.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJpYXQiOjE3NzkwNjE0NjcsImlzcyI6Imh0dHBzOi8vcG93ZXJzeW5jLWFwaS5qb3VybmV5YXBwcy5jb20iLCJhdWQiOiJodHRwczovLzZhMDZiYmY2MjM0ZmEyYmY1MWEyY2NkMi5wb3dlcnN5bmMuam91cm5leWFwcHMuY29tIiwiZXhwIjoxNzc5MTA0NjY3fQ.RDaj3Ag8HE-g6bc23PRxPD87ftoaxU_cEnMsY21wVRpo5Z4f4rZW7j9wpztxMD2dLB9uT6V2cagDj5IjXPuU42Y43tqqnBIsey6xoiw2Uyzg2kXJVAFL9goxhWSTE_nxlqgmZUeqq0QMaf29ooNaRyMhhdTtkgq_TFcrI0-alSpyOEYwID14prkWCKZpdgflod5kpEPt8rdi5uYE763AmYjjbNSH1A7wbWTUdS7zB5d4rbKAsO3s3iprKSoke7zqD1PDZRevv162HucqQyw2VZheXjKzZxZ7TMthc0jnfwc_CcwmNdtyiMRpmRVbX11adjZRDQIunXJEvUuaamCxcQ"
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