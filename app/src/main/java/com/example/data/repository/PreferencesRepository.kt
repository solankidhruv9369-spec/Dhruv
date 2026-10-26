package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppConfig
import com.example.data.model.ChatMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("zoya_companion_prefs", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder().build()
    private val chatListType = Types.newParameterizedType(List::class.java, ChatMessage::class.java)
    private val chatAdapter = moshi.adapter<List<ChatMessage>>(chatListType)
    private val configAdapter = moshi.adapter(AppConfig::class.java)

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private val _messages = MutableStateFlow(loadMessages())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private fun loadConfig(): AppConfig {
        val json = prefs.getString(KEY_CONFIG, null) ?: return AppConfig()
        return try {
            configAdapter.fromJson(json) ?: AppConfig()
        } catch (e: Exception) {
            AppConfig()
        }
    }

    fun saveConfig(newConfig: AppConfig) {
        _config.value = newConfig
        try {
            val json = configAdapter.toJson(newConfig)
            prefs.edit().putString(KEY_CONFIG, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMessages(): List<ChatMessage> {
        val json = prefs.getString(KEY_MESSAGES, null) ?: return emptyList()
        return try {
            chatAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMessages(messages: List<ChatMessage>) {
        _messages.value = messages
        try {
            val json = chatAdapter.toJson(messages)
            prefs.edit().putString(KEY_MESSAGES, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addMessage(message: ChatMessage) {
        val updated = _messages.value + message
        saveMessages(updated)
    }

    fun clearMessages() {
        _messages.value = emptyList()
        prefs.edit().remove(KEY_MESSAGES).apply()
    }

    companion object {
        private const val KEY_CONFIG = "key_app_config"
        private const val KEY_MESSAGES = "key_chat_history"
    }
}
