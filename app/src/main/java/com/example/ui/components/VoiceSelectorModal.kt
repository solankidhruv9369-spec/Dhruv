package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ZoyaBgDark
import com.example.ui.theme.ZoyaPink
import com.example.ui.theme.ZoyaPurple
import com.example.ui.theme.ZoyaSurfaceDark
import com.example.ui.theme.ZoyaSurfaceElevated
import com.example.ui.theme.ZoyaTextMuted
import com.example.ui.theme.ZoyaTextWhite

data class VoiceProfile(
    val name: String,
    val subtitle: String,
    val description: String,
    val defaultPitch: Float,
    val defaultRate: Float
)

@Composable
fun VoiceSelectorModal(
    currentVoice: String,
    currentPitch: Float,
    currentRate: Float,
    onSave: (voiceName: String, pitch: Float, rate: Float) -> Unit,
    onTestVoice: (sampleText: String?) -> Unit,
    onClose: () -> Unit
) {
    val voiceProfiles = listOf(
        VoiceProfile("Kore", "Warm & Empathetic", "Natural, engaging and friendly companion tone", 1.05f, 1.0f),
        VoiceProfile("Aoede", "Melodic & Poetic", "Gentle, calm and soothing cadence", 1.15f, 0.95f),
        VoiceProfile("Puck", "Playful & Bright", "Energetic, witty and lively dialogue", 1.25f, 1.10f),
        VoiceProfile("Fenrir", "Deep & Resonant", "Steady, wise and authoritative presence", 0.85f, 0.95f)
    )

    var selectedVoice by remember { mutableStateOf(currentVoice) }
    var pitch by remember { mutableFloatStateOf(currentPitch) }
    var rate by remember { mutableFloatStateOf(currentRate) }

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .testTag("voice_selector_modal"),
            color = ZoyaSurfaceDark,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ZoyaPink.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = ZoyaPink,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Voice / आवाज़",
                                color = ZoyaTextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Select timbre & modulation",
                                color = ZoyaTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_voice_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ZoyaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Voice Options
                voiceProfiles.forEach { profile ->
                    val isSelected = selectedVoice.equals(profile.name, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) ZoyaPink.copy(alpha = 0.12f)
                                else ZoyaSurfaceElevated
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ZoyaPink.copy(alpha = 0.6f) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedVoice = profile.name
                                pitch = profile.defaultPitch
                                rate = profile.defaultRate
                            }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.name,
                                        color = if (isSelected) ZoyaPink else ZoyaTextWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "• ${profile.subtitle}",
                                        color = ZoyaTextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = profile.description,
                                    color = ZoyaTextMuted.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(ZoyaPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = ZoyaBgDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pitch modulation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pitch Modulation",
                        color = ZoyaTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format("%.2f", pitch)}x",
                        color = ZoyaPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = pitch,
                    onValueChange = { pitch = it },
                    valueRange = 0.75f..1.35f,
                    colors = SliderDefaults.colors(
                        thumbColor = ZoyaPink,
                        activeTrackColor = ZoyaPink,
                        inactiveTrackColor = ZoyaSurfaceElevated
                    ),
                    modifier = Modifier.testTag("pitch_slider")
                )

                // Speech Speed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Speaking Rate",
                        color = ZoyaTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format("%.2f", rate)}x",
                        color = ZoyaPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = rate,
                    onValueChange = { rate = it },
                    valueRange = 0.75f..1.40f,
                    colors = SliderDefaults.colors(
                        thumbColor = ZoyaPink,
                        activeTrackColor = ZoyaPink,
                        inactiveTrackColor = ZoyaSurfaceElevated
                    ),
                    modifier = Modifier.testTag("rate_slider")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onTestVoice("Hello! I am speaking with the $selectedVoice voice profile.")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ZoyaPink
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Voice")
                    }

                    Button(
                        onClick = {
                            onSave(selectedVoice, pitch, rate)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZoyaPink,
                            contentColor = ZoyaBgDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_voice_button")
                    ) {
                        Text("Save Voice", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
