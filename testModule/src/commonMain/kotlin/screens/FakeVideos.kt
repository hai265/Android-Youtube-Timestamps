package screens

import com.hai265.timestamper.data.database.Timestamp
import com.hai265.timestamper.data.database.Video
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
val fakeVideo1 = Video(
    id = Uuid.fromLongs(1, 1),
    youtubeId = "tQDO-uVCl40",
    videoTitle = "【みんなのGOLF WORLD】メジロ家のパーマー、ライアン、アルダン、ブライトの4人でゲーム実況だ！【前編】",
    thumbnail = "https://img.youtube.com/vi/tQDO-uVCl40/maxresdefault.jpg",
    lastEdited = Instant.fromEpochSeconds(1777855059),
    lastPlayed = Duration.ZERO,
)

@OptIn(ExperimentalUuidApi::class)
val fakeVideo2 = Video(
    id = Uuid.fromLongs(2, 2),
    youtubeId = "b-P-wuUEUeQ",
    videoTitle = "テトリス99】初心に帰ってテトリスをやろう\uD83C\uDFAE【星街すいせい",
    thumbnail = "https://img.youtube.com/vi/b-P-wuUEUeQ/maxresdefault.jpg",
    lastEdited = Instant.fromEpochSeconds(0L),
    lastPlayed = Duration.ZERO,
)

val fakeVideoList = listOf(fakeVideo1, fakeVideo2)

@OptIn(ExperimentalUuidApi::class)
val fakeTimestamp1 = Timestamp(
    id = Uuid.fromLongs(1, 1),
    videoId = Uuid.fromLongs(1, 1),
    time = Duration.ZERO,
    description = "Sample Description"
)

@OptIn(ExperimentalUuidApi::class)
val fakeTimestamp2 = Timestamp(
    id = Uuid.fromLongs(2, 2),
    videoId = Uuid.fromLongs(2, 2),
    time = 10000000.milliseconds,
    description = "Sample Description"
)

@OptIn(ExperimentalUuidApi::class)
val fakeTimestamp3 = Timestamp(
    id = Uuid.fromLongs(3, 3),
    videoId = Uuid.fromLongs(3, 3),
    time = 10000000.milliseconds,
    description = "Sample Description"
)

val fakeTimestampList = listOf(fakeTimestamp1, fakeTimestamp2)