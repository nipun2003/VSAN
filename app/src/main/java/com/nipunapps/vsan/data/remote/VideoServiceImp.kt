package com.nipunapps.vsan.data.remote

import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.utils.Resource
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*

class VideoServiceImp(
    private val client: HttpClient
) : VideoService {
    override suspend fun getVideos(): Resource<List<Video>> {
        return try {
            Resource.success(client.get {
                url(HttpRoutes.VIDEOS)
            })
        } catch (e: RedirectResponseException) {
            //3xx- response
            println("Error : ${e.response.status.description}")
            Resource.error(e.response.status.description,null)
        } catch (e: ClientRequestException) {
            //4xx- response
            println("Error : ${e.response.status.description}")
            Resource.error(e.response.status.description,null)
        } catch (e: ServerResponseException) {
            //5xx- response
            println("Error : ${e.response.status.description}")
            Resource.error(e.response.status.description,null)
        } catch (e: Exception) {
            Resource.error(e.message.toString(),null)
        }
    }
}