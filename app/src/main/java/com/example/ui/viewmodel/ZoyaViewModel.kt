package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppConfig
import com.example.data.model.AppState
import com.example.data.model.ChatMessage
import com.example.data.repository.GeminiRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.service.CommandProcessor
import com.example.data.service.SpeechAudioEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ZoyaViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository(application)
    private val geminiRepository = GeminiRepository()

    val config: StateFlow<AppConfig> = preferencesRepository.config
    val messages: StateFlow<List<ChatMessage>> = preferencesRepository.messages

    private val _appState = MutableStateFlow(AppState.IDLE)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _browserLaunchUrl = MutableStateFlow<String?>(null)
    val browserLaunchUrl: StateFlow<String?> = _browserLaunchUrl.asStateFlow()

    private var audioEngine: SpeechAudioEngine? = null
    private var speakingJob: Job? = null

    init {
        audioEngine = SpeechAudioEngine(
            context = application,
            onSpeechRecognized = { transcript ->
                processUserSpeech(transcript)
            },
            onSpeechStateChanged = { isListening ->
                if (isListening) {
                    _appState.value = AppState.LISTENING
                } else if (_appState.value == AppState.LISTENING) {
                    // Speech ended or timeout
                }
            },
            onSpeakingStateChanged = { isSpeaking ->
                if (isSpeaking) {
                    _appState.value = AppState.SPEAKING
                } else {
                    if (_isSessionActive.value) {
                        // After speaking, if continuous session is active, resume listening!
                        viewModelScope.launch {
                            delay(400)
                            if (_isSessionActive.value) {
                                audioEngine?.startListening()
                            } else {
                                _appState.value = AppState.IDLE
                            }
                        }
                    } else {
                        _appState.value = AppState.IDLE
                    }
                }
            }
        )

        // Forward audio levels
        viewModelScope.launch {
            audioEngine?.audioLevel?.collect { level ->
                if (_appState.value == AppState.LISTENING) {
                    _audioLevel.value = level
                }
            }
        }
    }

    fun toggleSession() {
        if (_isSessionActive.value) {
            endSession()
        } else {
            startSession()
        }
    }

    fun startSession() {
        _isSessionActive.value = true
        audioEngine?.startListening()
    }

    fun endSession() {
        _isSessionActive.value = false
        audioEngine?.stopListening()
        audioEngine?.stopSpeaking()
        _appState.value = AppState.IDLE
        _audioLevel.value = 0f
    }

    fun processUserSpeech(transcript: String) {
        if (transcript.isBlank()) return
        handleUserMessage(transcript)
    }

    fun sendTextMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        handleUserMessage(trimmed)
    }

    private fun handleUserMessage(inputText: String) {
        val currentCfg = config.value
        val userMsg = ChatMessage(
            sender = "user",
            text = inputText
        )
        preferencesRepository.addMessage(userMsg)

        _appState.value = AppState.PROCESSING

        viewModelScope.launch {
            // 1. Check for command actions (open youtube, spotify, maps, etc.)
            val commandResult = CommandProcessor.processCommand(inputText)

            if (commandResult.isBrowserAction) {
                val zoyaMsg = ChatMessage(
                    sender = "zoya",
                    text = commandResult.action,
                    isAction = true,
                    actionUrl = commandResult.url
                )
                preferencesRepository.addMessage(zoyaMsg)

                _appState.value = AppState.SPEAKING
                audioEngine?.speak(commandResult.action, currentCfg)

                // Trigger URL intent after a moment
                commandResult.url?.let { url ->
                    delay(1200)
                    _browserLaunchUrl.value = url
                }

                if (currentCfg.isMuted) {
                    _appState.value = if (_isSessionActive.value) AppState.LISTENING else AppState.IDLE
                }
            } else {
                // 2. Chit-Chat / Reasoning via Gemini
                val reply = geminiRepository.getCompanionResponse(
                    userInput = inputText,
                    history = messages.value,
                    config = currentCfg
                )

                val zoyaMsg = ChatMessage(
                    sender = "zoya",
                    text = reply
                )
                preferencesRepository.addMessage(zoyaMsg)

                _appState.value = AppState.SPEAKING
                audioEngine?.speak(reply, currentCfg)

                if (currentCfg.isMuted) {
                    _appState.value = if (_isSessionActive.value) AppState.LISTENING else AppState.IDLE
                }
            }
        }
    }

    fun clearBrowserLaunchUrl() {
        _browserLaunchUrl.value = null
    }

    fun updateConfig(newConfig: AppConfig) {
        preferencesRepository.saveConfig(newConfig)
    }

    fun updateVoice(voiceName: String, pitch: Float, rate: Float) {
        val current = config.value
        updateConfig(current.copy(voiceName = voiceName, speechPitch = pitch, speechRate = rate))
    }

    fun updateActiveTopic(topic: String) {
        val current = config.value
        updateConfig(current.copy(activeTopicOrScript = topic))
    }

    fun updateVolume(newVolume: Float) {
        val current = config.value
        val isMuted = newVolume <= 0.01f
        updateConfig(current.copy(volume = newVolume, isMuted = isMuted))
    }

    fun toggleMute() {
        val current = config.value
        val newMuted = !current.isMuted
        if (newMuted) {
            audioEngine?.stopSpeaking()
        }
        updateConfig(current.copy(isMuted = newMuted))
    }

    fun setBackgroundVideo(uriString: String?) {
        val current = config.value
        updateConfig(current.copy(backgroundVideoUri = uriString))
    }

    fun clearChatHistory() {
        preferencesRepository.clearMessages()
    }

    fun testVoice(sampleText: String? = null) {
        val current = config.value
        val text = sampleText ?: "Hello ${current.userName}! This is ${current.assistantName} with the ${current.voiceName} voice profile."
        audioEngine?.speak(text, current)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine?.release()
    }
}
