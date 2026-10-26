package com.example.data.repository

import com.example.BuildConfig
import com.example.data.model.AppConfig
import com.example.data.model.ChatMessage
import com.example.data.model.GeminiContent
import com.example.data.model.GeminiGenerationConfig
import com.example.data.model.GeminiPart
import com.example.data.model.GeminiRequest
import com.example.data.model.GeminiSystemInstruction
import com.example.data.remote.GeminiApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(
    private val api: GeminiApi = GeminiApi.create()
) {

    suspend fun getCompanionResponse(
        userInput: String,
        history: List<ChatMessage>,
        config: AppConfig
    ): String = withContext(Dispatchers.IO) {
        val apiKey = when {
            config.customApiKey.isNotBlank() -> config.customApiKey.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && !BuildConfig.GEMINI_API_KEY.contains("MY_GEMINI_API_KEY") -> BuildConfig.GEMINI_API_KEY.trim()
            else -> ""
        }

        if (apiKey.isEmpty()) {
            return@withContext getOfflineCompanionReply(userInput, config)
        }

        val systemPrompt = buildString {
            append("You are ${config.assistantName}, an intelligent, deeply engaging, and empathetic virtual companion for ${config.userName}. ")
            append("Your defined role is: ${config.role}. ")
            append("Your personality traits: ${config.personalityTraits}. ")
            if (config.activeTopicOrScript.isNotBlank()) {
                append("\nACTIVE TOPIC & SCENARIO GUIDELINES:\n${config.activeTopicOrScript}\n")
                append("Anchor your persona, tone, and answers to this scenario.\n")
            }
            append("\nCRITICAL VOICE OUTPUT GUIDELINES:")
            append("\n1. Responses will be spoken aloud to the user using text-to-speech.")
            append("\n2. Sound warm, natural, lively, and conversational.")
            append("\n3. Keep spoken replies concise (typically 1 to 3 engaging sentences) unless the user explicitly requests an extended story, breakdown, or lecture.")
            append("\n4. Do NOT use markdown tables, asterisks, hash headers, or bullet symbols, as those sound robotic when read aloud.")
        }

        // Build recent conversation turns (last 10 messages for context)
        val recentHistory = history.takeLast(10)
        val contentsList = mutableListOf<GeminiContent>()

        for (msg in recentHistory) {
            val role = if (msg.sender == "user") "user" else "model"
            contentsList.add(
                GeminiContent(
                    role = role,
                    parts = listOf(GeminiPart(text = msg.text))
                )
            )
        }

        // Add current user prompt
        contentsList.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = userInput))
            )
        )

        val request = GeminiRequest(
            contents = contentsList,
            systemInstruction = GeminiSystemInstruction(
                parts = listOf(GeminiPart(text = systemPrompt))
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.75f,
                topP = 0.95f,
                maxOutputTokens = 600
            )
        )

        try {
            // First attempt with gemini-2.5-flash
            val response = try {
                api.generateContent(
                    model = "gemini-2.5-flash",
                    apiKey = apiKey,
                    request = request
                )
            } catch (e: Exception) {
                // Fallback to gemini-1.5-flash if 2.5 is unavailable
                api.generateContent(
                    model = "gemini-1.5-flash",
                    apiKey = apiKey,
                    request = request
                )
            }

            val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!replyText.isNullOrBlank()) {
                cleanVoiceResponse(replyText)
            } else if (response.error != null) {
                "I encountered an error: ${response.error.message ?: "Unable to process request."}"
            } else {
                "I'm here with you, but I didn't catch that clearly. Could you say that again?"
            }
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Connection error"
            if (errorMsg.contains("API_KEY_INVALID", ignoreCase = true) || errorMsg.contains("400") || errorMsg.contains("403")) {
                "It looks like the Gemini API key needs to be verified. Please check your API key in Settings."
            } else {
                // If temporary network interruption, fallback to smart companion responses
                getOfflineCompanionReply(userInput, config)
            }
        }
    }

    private fun cleanVoiceResponse(text: String): String {
        return text
            .replace(Regex("\\*\\*|\\*|__|#|>|`"), "")
            .replace(Regex("\\n+"), " ")
            .trim()
    }

    private fun getOfflineCompanionReply(input: String, config: AppConfig): String {
        val lower = input.lowercase().trim()
        val name = config.assistantName
        val user = config.userName

        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Hello $user! I'm $name, your virtual companion. I'm listening—what's on your mind today?"

            lower.contains("how are you") ->
                "I'm feeling energized and ready to chat with you, $user! How has your day been going?"

            lower.contains("who are you") || lower.contains("what is your name") ->
                "I am $name, your virtual companion. I'm here to listen, think, and explore ideas with you."

            lower.contains("topic") || lower.contains("script") ->
                if (config.activeTopicOrScript.isNotBlank()) {
                    "Our current active focus is: ${config.activeTopicOrScript}. I'm ready whenever you are!"
                } else {
                    "We don't have a specific topic locked in yet. You can set one in the Topic and Script menu anytime."
                }

            lower.contains("thank") ->
                "You are always welcome, $user! I'm always glad to be here for you."

            lower.contains("bye") || lower.contains("goodbye") || lower.contains("good night") ->
                "Goodbye for now, $user. Take care, and tap the orb whenever you want to talk again!"

            else ->
                "I hear you, $user. You said: '$input'. To unlock full live AI intelligence with real-time reasoning, tap Settings and enter your Gemini API key."
        }
    }
}
