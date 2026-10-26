package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.BuildConfig
import com.example.data.model.AppConfig
import com.example.ui.theme.ZoyaBgDark
import com.example.ui.theme.ZoyaCyan
import com.example.ui.theme.ZoyaEmerald
import com.example.ui.theme.ZoyaPurple
import com.example.ui.theme.ZoyaSurfaceDark
import com.example.ui.theme.ZoyaSurfaceElevated
import com.example.ui.theme.ZoyaTextMuted
import com.example.ui.theme.ZoyaTextWhite

@Composable
fun SetupScreen(
    currentConfig: AppConfig,
    onSave: (AppConfig) -> Unit,
    onClose: () -> Unit
) {
    var assistantName by remember { mutableStateOf(currentConfig.assistantName) }
    var userName by remember { mutableStateOf(currentConfig.userName) }
    var role by remember { mutableStateOf(currentConfig.role) }
    var traits by remember { mutableStateOf(currentConfig.personalityTraits) }
    var customApiKey by remember { mutableStateOf(currentConfig.customApiKey) }
    var showApiKey by remember { mutableStateOf(false) }

    val hasEnvKey = BuildConfig.GEMINI_API_KEY.isNotBlank() && !BuildConfig.GEMINI_API_KEY.contains("MY_GEMINI_API_KEY")

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .testTag("settings_setup_modal"),
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
                                .background(ZoyaCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = ZoyaCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Personalisation",
                                color = ZoyaTextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Configure identity and intelligence",
                                color = ZoyaTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_setup_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ZoyaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Assistant Name
                Text(
                    text = "Companion Name",
                    color = ZoyaTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = assistantName,
                    onValueChange = { assistantName = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("assistant_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZoyaTextWhite,
                        unfocusedTextColor = ZoyaTextWhite,
                        focusedBorderColor = ZoyaCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = ZoyaCyan
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // User Name
                Text(
                    text = "Your Name / Nickname",
                    color = ZoyaTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZoyaTextWhite,
                        unfocusedTextColor = ZoyaTextWhite,
                        focusedBorderColor = ZoyaCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = ZoyaCyan
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Role
                Text(
                    text = "Role / Relationship",
                    color = ZoyaTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("role_input"),
                    placeholder = { Text("e.g. Virtual Companion, Creative Muse, Mindful Mentor") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZoyaTextWhite,
                        unfocusedTextColor = ZoyaTextWhite,
                        focusedBorderColor = ZoyaCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = ZoyaCyan
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Personality Traits
                Text(
                    text = "Personality Traits",
                    color = ZoyaTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = traits,
                    onValueChange = { traits = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("traits_input"),
                    placeholder = { Text("e.g. Warm, witty, attentive, curious, empathetic") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZoyaTextWhite,
                        unfocusedTextColor = ZoyaTextWhite,
                        focusedBorderColor = ZoyaCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = ZoyaCyan
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // API Key Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = ZoyaPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gemini API Key",
                        color = ZoyaTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (hasEnvKey) {
                        "Pre-configured via AI Studio Secrets Panel. You can optionally enter an override key below:"
                    } else {
                        "Enter your Google Gemini API Key to enable live cloud reasoning:"
                    },
                    color = ZoyaTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customApiKey,
                    onValueChange = { customApiKey = it },
                    singleLine = true,
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle visibility",
                                tint = ZoyaTextMuted
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_input"),
                    placeholder = {
                        Text(
                            text = if (hasEnvKey) "Using environment key" else "AIzaSy...",
                            color = ZoyaTextMuted.copy(alpha = 0.5f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ZoyaTextWhite,
                        unfocusedTextColor = ZoyaTextWhite,
                        focusedBorderColor = ZoyaPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = ZoyaPurple
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSave(
                            currentConfig.copy(
                                assistantName = assistantName.trim().ifBlank { "Zoya" },
                                userName = userName.trim().ifBlank { "User" },
                                role = role.trim(),
                                personalityTraits = traits.trim(),
                                customApiKey = customApiKey.trim()
                            )
                        )
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZoyaCyan,
                        contentColor = ZoyaBgDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_settings_button")
                ) {
                    Text(
                        text = "Save Changes",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
