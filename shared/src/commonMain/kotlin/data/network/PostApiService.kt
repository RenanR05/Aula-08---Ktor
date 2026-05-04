package data.network

import data.model.Post
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PostApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    suspend fun getPosts(page: Int, limit: Int, userId: Int? = null): List<Post> {
        return client.get("https://jsonplaceholder.typicode.com/posts") {
            parameter("_page", page)
            parameter("_limit", limit)
            if (userId != null) {
                parameter("userId", userId)
            }
        }.body()
    }
}
