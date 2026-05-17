package com.hai265.timestamper.data.database.powersync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val schema = Schema(
    Table(
        name = "videos",
        columns = listOf(
            Column.text("user_id"),
            Column.text("video_id"),
            Column.text("video_title"),
            Column.text("thumbnail"),
            Column.text("last_edited"),
            Column.text("last_played_position")
        )
    ),
    Table(
        name = "timestamps",
        columns = listOf(
            Column.text("user_id"),
            Column.text("video_id"),
            Column.text("time"),
            Column.text("description")
        )
    )
)
