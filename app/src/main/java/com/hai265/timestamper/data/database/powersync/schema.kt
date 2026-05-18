package com.hai265.timestamper.data.database.powersync

import com.powersync.db.schema.RawTable
import com.powersync.db.schema.RawTableSchema
import com.powersync.db.schema.Schema

val schema = Schema(
    RawTable(
        name = "videos",
        schema = RawTableSchema()
    ),
    RawTable(
        name = "timestamps",
        schema = RawTableSchema(),
    )
)
