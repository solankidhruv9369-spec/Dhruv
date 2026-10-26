package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.ZoyaAmber
import com.example.ui.theme.ZoyaBgDark
import com.example.ui.theme.ZoyaCyan
import com.example.ui.theme.ZoyaSurfaceDark
import com.example.ui.theme.ZoyaSurfaceElevated
import com.example.ui.theme.ZoyaTextMuted
import com.example.ui.theme.ZoyaTextWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicScriptModal(
    assistantName: String,
    currentTopic: String,
    onSave: (String) -> Unit,
    onClose: () -> Unit
) {
    var topicText by remember { mutableStateOf(currentTopic) }

    val presetTopics = listOf(
        "Mock Technical & System Design Interview" to "Act as a senior engineering interviewer. Ask me structured questions and critique my answers.",
        "Daily Reflection & Emotional Support" to "Act as a compassionate, mindful confidant. Help me decompress and reflect on my day.",
        "Sci-Fi Worldbuilding & Storytelling" to "Collaborate with me on building an immersive futuristic cyberpunk world and interactive story.",
        "Conversational Language Practice" to "Converse with me to sharpen my conversational fluency, providing gentle corrections and vocabulary tips.",
        "Philosophy & Ethical Paradoxes" to "Explore deep philosophical paradoxes and cognitive biases with witty and profound observations."
    )

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .testTag("topic_script_modal"),
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
                                .background(ZoyaAmber.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = ZoyaAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Topic & Script Focus",
                                color = ZoyaTextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Guide $assistantName's focus scenario",
                                color = ZoyaTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_topic_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ZoyaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Specify a scenario or script guidelines for $assistantName to follow in real-time conversation:",
                    color = ZoyaTextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = topicText,
                    onValueChange = { topicText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("topic_text_field"),
                    placeholder = {
                        Text(
                            text = "e.g., Pretend we are planning an expedition to Mars, or ask me questions for a mock interview...",
                            color = ZoyaTextMuted.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZoyaTextWhite,
                        unfocusedTextColor = ZoyaTextWhite,
                        focusedBorderColor = ZoyaAmber,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = ZoyaAmber
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quick Focus Scenarios",
                    color = ZoyaTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetTopics.forEach { (label, script) ->
                        val isSelected = topicText.trim() == script.trim()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) ZoyaAmber.copy(alpha = 0.25f)
                                    else ZoyaSurfaceElevated
                                )
                                .clickable {
                                    topicText = script
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) ZoyaAmber else ZoyaTextWhite.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (topicText.isNotBlank()) {
                        Button(
                            onClick = {
                                topicText = ""
                                onSave("")
                                onClose()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(alpha = 0.15f),
                                contentColor = Color(0xFFFF5252)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("clear_topic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onSave(topicText.trim())
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZoyaAmber,
                            contentColor = ZoyaBgDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_topic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (topicText.isBlank()) "Keep General" else "Apply Scenario",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
