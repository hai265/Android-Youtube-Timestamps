package com.hai265.timestamper.ui.fakes

import com.hai265.timestamper.data.database.Video

val fakeVideo1 = Video(
    videoId = "tQDO-uVCl40",
    videoTitle = "【みんなのGOLF WORLD】メジロ家のパーマー、ライアン、アルダン、ブライトの4人でゲーム実況だ！【前編】",
    thumbnail = "https://img.youtube.com/vi/tQDO-uVCl40/maxresdefault.jpg",
    lastEdited = 0
)

val fakeVideo2 = Video(
    videoId = "b-P-wuUEUeQ",
    videoTitle = "テトリス99】初心に帰ってテトリスをやろう\uD83C\uDFAE【星街すいせい",
    thumbnail = "https://img.youtube.com/vi/b-P-wuUEUeQ/maxresdefault.jpg",
    lastEdited = 0
)

val fakeVideoList = listOf(fakeVideo1, fakeVideo2)