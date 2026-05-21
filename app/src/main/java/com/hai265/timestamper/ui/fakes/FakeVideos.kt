package com.hai265.timestamper.ui.fakes

import com.hai265.timestamper.common.ZERO
import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

val fakeVideo1 = Video(
    youtubeId = "tQDO-uVCl40",
    videoTitle = "【みんなのGOLF WORLD】メジロ家のパーマー、ライアン、アルダン、ブライトの4人でゲーム実況だ！【前編】",
    thumbnail = "https://img.youtube.com/vi/tQDO-uVCl40/maxresdefault.jpg",
    lastEdited = Instant.fromEpochSeconds(1777855059),
    lastPlayed = Duration.ZERO,
)

val fakeVideo2 = Video(
    youtubeId = "b-P-wuUEUeQ",
    videoTitle = "テトリス99】初心に帰ってテトリスをやろう\uD83C\uDFAE【星街すいせい",
    thumbnail = "https://img.youtube.com/vi/b-P-wuUEUeQ/maxresdefault.jpg",
    lastEdited = Instant.ZERO,
    lastPlayed = Duration.ZERO,
)

val fakeVideoList = listOf(fakeVideo1, fakeVideo2)

@OptIn(ExperimentalUuidApi::class)
val fakeTimestamp1 = Timestamp(
    id = "id1",
    videoId = "1",
    time = Duration.ZERO,
    description = "Sample Description"
)

val fakeTimestamp2 = Timestamp(
    id = "id2",
    videoId = "1",
    time = 10000000.milliseconds,
    description = "Sample Description"
)

val fakeTimestamp3 = Timestamp(
    id = "id3",
    videoId = "1",
    time = 10000000.milliseconds,
    description = "Sample Description"
)

val fakeTimestampList = listOf(fakeTimestamp1, fakeTimestamp2)