package com.hai265.timestamper.data.database.powersync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val schema = Schema(
    listOf(
        Table(
            name = "videos",
            columns = listOf(
                Column.text("video_title"),
                Column.text("user_id"),
                Column.text("thumbnail"),
                Column.text("last_edited"),
                Column.integer("last_played"),
                Column.text("youtube_id")
            )
        ),
        Table(
            name = "timestamps",
            columns = listOf(
                Column.text("video_id"),
                Column.text("time"),
                Column.text("description"),
            )
        )
    )
)
