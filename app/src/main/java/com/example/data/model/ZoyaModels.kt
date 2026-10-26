package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class AppState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    RECONNECTING
}

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "user" or "zoya"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAction: Boolean = false,
    val actionUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class AppConfig(
    val assistantName: String = "Zoya",
    val userName: String = "Dhruv",
    val voiceName: String = "Kore", // "Kore", "Aoede", "Puck", "Fenrir"
    val speechPitch: Float = 1.05f,
    val speechRate: Float = 1.0f,
    val role: String = "Empathetic & Intelligent Virtual Companion",
    val personalityTraits: String = "Warm, attentive, witty, expressive, and thoughtful",
    val activeTopicOrScript: String = "",
    val customApiKey: String = "",
    val isMuted: Boolean = false,
    val volume: Float = 1.0f,
    val backgroundVideoUri: String? = null
)

data class CommandResult(
    val isBrowserAction: Boolean,
    val action: String,
    val url: String? = null
)

// Gemini API DTOs
@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @property:Json(name = "systemInstruction")
    val systemInstruction: GeminiSystemInstruction? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiSystemInstruction(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String, // "user" or "model"
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float = 0.7f,
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 800
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiCandidateContent? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidateContent(
    val role: String? = null,
    val parts: List<GeminiPart>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)
