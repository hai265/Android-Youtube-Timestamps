package com.hai265.timestamper.ui.fakes

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

val fakeVideo1 = Video(
    videoId = "tQDO-uVCl40",
    videoTitle = "【みんなのGOLF WORLD】メジロ家のパーマー、ライアン、アルダン、ブライトの4人でゲーム実況だ！【前編】",
    thumbnail = "https://img.youtube.com/vi/tQDO-uVCl40/maxresdefault.jpg",
    lastEdited = Duration.ZERO,
    lastPlayed = Duration.ZERO,
)

val fakeVideo2 = Video(
    videoId = "b-P-wuUEUeQ",
    videoTitle = "テトリス99】初心に帰ってテトリスをやろう\uD83C\uDFAE【星街すいせい",
    thumbnail = "https://img.youtube.com/vi/b-P-wuUEUeQ/maxresdefault.jpg",
    lastEdited = Duration.ZERO,
    lastPlayed = Duration.ZERO,
)

val fakeVideoList = listOf(fakeVideo1, fakeVideo2)

val fakeTimestamp1 = Timestamp(
    id = 1,
    videoId = "1",
    time = Duration.ZERO,
    description = "Sample Description"
)

val fakeTimestamp2 = Timestamp(
    id = 2,
    videoId = "1",
    time = 10000000.milliseconds,
    description = "Sample Description"
)

val fakeTimestampList = listOf(fakeTimestamp1, fakeTimestamp2)