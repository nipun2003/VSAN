package com.nipunapps.vsan.data.remote

import com.nipunapps.vsan.utils.Resource
import com.nipunapps.vsan.data.remote.dto.Video
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

interface VideoService {
    suspend fun getVideos() : List<Video>
    companion object{
        fun create() : VideoService {
            return VideoServiceImp(
                client = HttpClient(Android){
                    install(Logging){
                        level = LogLevel.ALL
                    }
                    install(JsonFeature){
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}