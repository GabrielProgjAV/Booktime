package com.example.booktime.tadeo.data.chat

import com.example.booktime.tadeo.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)


data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: ContentResponse?
)

data class ContentResponse(
    val parts: List<PartResponse>?
)

data class PartResponse(
    val text: String?
)


interface GeminiApi {
    @POST("models/gemini-flash-latest:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}


class GeminiRepository {

    companion object {
        // Prefix used to mark a response as an error so callers (e.g. ChatBottomSheet)
        // can tell it apart from a real AI reply and style/handle it accordingly.
        const val ERROR_PREFIX = "[ERROR_IA]"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeminiApi::class.java)


    suspend fun ask(prompt: String): String {
        return try {

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt)
                        )
                    )
                )
            )

            val response = api.generateContent(
                BuildConfig.GEMINI_API_KEY,
                request
            )

            response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: "No tengo respuesta "

        } catch (e: Exception) {
            e.printStackTrace()
            return "$ERROR_PREFIX No se pudo obtener respuesta de la IA: ${e.message}"
        }
    }
}