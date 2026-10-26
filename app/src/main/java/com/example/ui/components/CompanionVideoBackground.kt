package com.example.ui.components

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CompanionVideoBackground(
    videoUriString: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uri = remember(videoUriString) { Uri.parse(videoUriString) }
    val mediaPlayer = remember { MediaPlayer() }

    LaunchedEffect(isPlaying) {
        try {
            if (mediaPlayer.isPlaying != isPlaying) {
                if (isPlaying) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(uri) {
        var surface: Surface? = null
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0f, 0f) // Video is muted, audio comes from TTS/Gemini
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                if (isPlaying) {
                    it.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            try {
                mediaPlayer.stop()
                mediaPlayer.release()
                surface?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).apply {
                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                            try {
                                val surf = Surface(surfaceTexture)
                                mediaPlayer.setSurface(surf)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

                        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                            try {
                                mediaPlayer.setSurface(null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            return true
                        }

                        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
