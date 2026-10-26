package com.example.data.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechAudioEngine(
    private val context: Context,
    private val onSpeechRecognized: (String) -> Unit,
    private val onSpeechStateChanged: (isListening: Boolean) -> Unit,
    private val onSpeakingStateChanged: (isSpeaking: Boolean) -> Unit
) : TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        initSpeechRecognizer()
        textToSpeech = TextToSpeech(context, this)
    }

    private fun initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        onSpeechStateChanged(true)
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize rmsdB (-2 to ~10 dB) into 0.0f .. 1.0f range for visualizer
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        _audioLevel.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _audioLevel.value = 0f
                        _isListening.value = false
                        onSpeechStateChanged(false)
                    }

                    override fun onError(error: Int) {
                        _audioLevel.value = 0f
                        _isListening.value = false
                        onSpeechStateChanged(false)
                    }

                    override fun onResults(results: Bundle?) {
                        _audioLevel.value = 0f
                        _isListening.value = false
                        onSpeechStateChanged(false)
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim()
                        if (!text.isNullOrEmpty()) {
                            onSpeechRecognized(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim()
                        if (!text.isNullOrEmpty()) {
                            // Can be used for live transcription if desired
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    onSpeakingStateChanged(true)
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeakingStateChanged(false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeakingStateChanged(false)
                }
            })
            isTtsReady = true
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }
        stopSpeaking()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
            onSpeechStateChanged(true)
        } catch (e: Exception) {
            e.printStackTrace()
            _isListening.value = false
            onSpeechStateChanged(false)
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _isListening.value = false
        _audioLevel.value = 0f
        onSpeechStateChanged(false)
    }

    fun speak(text: String, config: AppConfig) {
        if (!isTtsReady || config.isMuted || config.volume <= 0f) {
            return
        }

        stopSpeaking()

        textToSpeech?.apply {
            // Apply voice profile settings
            setPitch(config.speechPitch)
            setSpeechRate(config.speechRate)

            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, config.volume.coerceIn(0f, 1f))
            }

            speak(text, TextToSpeech.QUEUE_FLUSH, params, "zoya_utterance_${System.currentTimeMillis()}")
        }
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _isSpeaking.value = false
        onSpeakingStateChanged(false)
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
