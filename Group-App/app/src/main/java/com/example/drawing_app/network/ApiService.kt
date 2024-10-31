package com.example.drawing_app.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DrawingResponse(
    val id: Int,
    val userId: String,
    val imageUrl: String,
    val shared: Boolean
)

@Serializable
data class ImageUploadRequest(val userId: String, val imageUrl: String)

class ApiService {
    private val URL_BASE = "http://10.0.2.2:8080"

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Resources)
    }

    suspend fun uploadImage(userId: String, imageUrl: String): String {
        val response = httpClient.post("$URL_BASE/images/upload") {
            contentType(ContentType.Application.Json)
            setBody(ImageUploadRequest(userId, imageUrl))
        }
        return response.body<String>()
    }

    suspend fun fetchSharedImages(userId: String): List<DrawingResponse> {
        return httpClient.get("$URL_BASE/images/shared") {
            if (userId != null) {
                parameter("userId", userId)
            }
        }.body()
    }

    suspend fun shareImage(imageId: Int) {
        httpClient.post("$URL_BASE/images/$imageId/share").body<Unit>()
    }

    suspend fun unshareImage(imageId: Int) {
        httpClient.post("$URL_BASE/images/$imageId/unshare").body<Unit>()
    }

    suspend fun deleteImage(imageId: Int) {
        httpClient.delete("$URL_BASE/images/$imageId").body<Unit>()
    }
}
