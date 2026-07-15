package com.hai265.timestamper.data.network

class YoutubeMetadataApiImpl(
    private val ktorImpl: YoutubeMetadataApiOEmbedImpl,
    private val openGraphImpl: YoutubeMetadataOpenGraphImpl
) : YoutubeMetadataApiService {
    override suspend fun getYoutubeMetadata(videoUrl: String): YoutubeMetadataResult {
        val ktorResult = ktorImpl.getYoutubeMetadata(videoUrl)
        if (ktorResult is YoutubeMetadataResult.Success) return ktorResult

        val ogResult = openGraphImpl.getYoutubeMetadata(videoUrl)
        if (ogResult is YoutubeMetadataResult.Success) return ogResult

        return when {
            ktorResult is YoutubeMetadataResult.HttpError -> ktorResult
            ogResult is YoutubeMetadataResult.HttpError -> ogResult
            ktorResult is YoutubeMetadataResult.NetworkError -> ktorResult
            else -> ogResult
        }
    }
}