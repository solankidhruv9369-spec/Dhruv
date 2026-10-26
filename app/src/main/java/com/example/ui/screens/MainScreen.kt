package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VideoLibrary
import com.example.data.model.AppState
import com.example.ui.components.ChatHistorySheet
import com.example.ui.components.CompanionVideoBackground
import com.example.ui.components.PermissionModal
import com.example.ui.components.SetupScreen
import com.example.ui.components.SpecificationModal
import com.example.ui.components.TopicScriptModal
import com.example.ui.components.Visualizer
import com.example.ui.components.VoiceSelectorModal
import com.example.ui.theme.ZoyaAmber
import com.example.ui.theme.ZoyaBgDark
import com.example.ui.theme.ZoyaCyan
import com.example.ui.theme.ZoyaEmerald
import com.example.ui.theme.ZoyaPink
import com.example.ui.theme.ZoyaPurple
import com.example.ui.theme.ZoyaRose
import com.example.ui.theme.ZoyaSurfaceDark
import com.example.ui.theme.ZoyaSurfaceElevated
import com.example.ui.theme.ZoyaSurfaceLight
import com.example.ui.theme.ZoyaTextMuted
import com.example.ui.theme.ZoyaTextWhite
import com.example.ui.viewmodel.ZoyaViewModel

@Composable
fun MainScreen(
    viewModel: ZoyaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsStateWithLifecycle()
    val appState by viewModel.appState.collectAsStateWithLifecycle()
    val isSessionActive by viewModel.isSessionActive.collectAsStateWithLifecycle()
    val audioLevel by viewModel.audioLevel.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val browserLaunchUrl by viewModel.browserLaunchUrl.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }
    var showTopicModal by remember { mutableStateOf(false) }
    var showVoiceModal by remember { mutableStateOf(false) }
    var showSetupModal by remember { mutableStateOf(false) }
    var showSpecsModal by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showTextInput by remember { mutableStateOf(false) }
    var showVolumePopup by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var textInputValue by remember { mutableStateOf("") }

    // Audio Permission Launcher
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            viewModel.startSession()
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setBackgroundVideo(it.toString()) }
    }

    fun handleSessionClick() {
        if (hasAudioPermission) {
            viewModel.toggleSession()
        } else {
            showPermissionDialog = true
        }
    }

    // Handle browser launch actions
    LaunchedEffect(browserLaunchUrl) {
        browserLaunchUrl?.let { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                viewModel.clearBrowserLaunchUrl()
            }
        }
    }

    val spacedName = remember(config.assistantName) {
        config.assistantName.uppercase().map { "$it" }.joinToString(" . ")
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showMenu = false
                showVolumePopup = false
            },
        color = ZoyaBgDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = spacedName,
                    color = ZoyaTextWhite.copy(alpha = 0.95f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chat History Button
                    Box {
                        IconButton(
                            onClick = { showHistorySheet = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ZoyaSurfaceDark)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .testTag("chat_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Dialogue History",
                                tint = if (messages.isNotEmpty()) ZoyaCyan else ZoyaTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (messages.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 6.dp, end = 6.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(ZoyaCyan)
                            )
                        }
                    }

                    // Topic & Script Direct Button
                    Box {
                        IconButton(
                            onClick = { showTopicModal = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ZoyaSurfaceDark)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .testTag("topic_script_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Topic Focus",
                                tint = if (config.activeTopicOrScript.isNotBlank()) ZoyaAmber else ZoyaTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (config.activeTopicOrScript.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 6.dp, end = 6.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ZoyaAmber)
                            )
                        }
                    }

                    // Settings Menu Dropdown
                    Box {
                        IconButton(
                            onClick = { showMenu = !showMenu },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ZoyaSurfaceDark)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .testTag("settings_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = ZoyaTextWhite.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(ZoyaSurfaceDark)
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                .widthIn(min = 210.dp)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Memory, contentDescription = null, tint = ZoyaCyan, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(10.dp))
                                            Text("Specification", color = ZoyaCyan, fontSize = 14.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ZoyaCyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("v1.0", color = ZoyaCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showSpecsModal = true
                                }
                            )

                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = ZoyaAmber, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text("Personalised", color = ZoyaTextWhite, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showSetupModal = true
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = ZoyaPurple, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text("Your API Key", color = ZoyaTextWhite, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showSetupModal = true
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = ZoyaPink, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(10.dp))
                                            Text("Voice / आवाज़", color = ZoyaTextWhite, fontSize = 14.sp)
                                        }
                                        Text(config.voiceName, color = ZoyaPink, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showVoiceModal = true
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Psychology, contentDescription = null, tint = ZoyaAmber, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text("Topic & Script", color = ZoyaTextWhite, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showTopicModal = true
                                }
                            )

                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = ZoyaPink, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text(if (config.backgroundVideoUri != null) "Change Video" else "Upload Video", color = ZoyaTextWhite, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    videoPickerLauncher.launch("video/*")
                                }
                            )

                            if (config.backgroundVideoUri != null) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(10.dp))
                                            Text("Remove Video", color = Color(0xFFFF5252), fontSize = 14.sp)
                                        }
                                    },
                                    onClick = {
                                        showMenu = false
                                        viewModel.setBackgroundVideo(null)
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text("Clear Memory", color = Color(0xFFFF5252), fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    viewModel.clearChatHistory()
                                }
                            )
                        }
                    }
                }
            }

            // Center Stage (Status Pill + Visualizer Card)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Status Pill Indicator
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (appState) {
                        AppState.PROCESSING -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(1.dp, ZoyaCyan.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = ZoyaCyan
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Replying...",
                                    color = ZoyaCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        AppState.LISTENING -> {
                            val isSpeakingVoice = audioLevel > 0.08f
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(1.dp, ZoyaEmerald.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isSpeakingVoice) 10.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(if (isSpeakingVoice) ZoyaEmerald else ZoyaEmerald.copy(alpha = 0.7f))
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isSpeakingVoice) "Hearing your voice..." else "Listening...",
                                    color = ZoyaEmerald,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        AppState.SPEAKING -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(1.dp, ZoyaPink.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = ZoyaPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Speaking...",
                                    color = ZoyaPink,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        AppState.RECONNECTING -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(1.dp, ZoyaAmber.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = ZoyaAmber
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Reconnecting...",
                                    color = ZoyaAmber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        AppState.IDLE -> {
                            Text(
                                text = "Tap the orb or Initialize to talk",
                                color = ZoyaTextMuted.copy(alpha = 0.4f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Visualizer or Companion Video Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 380.dp)
                        .aspectRatio(4f / 4.8f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(ZoyaSurfaceDark)
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(32.dp))
                        .clickable {
                            handleSessionClick()
                        }
                        .testTag("visualizer_card"),
                    contentAlignment = Alignment.Center
                ) {
                    val videoUri = config.backgroundVideoUri
                    if (videoUri != null) {
                        CompanionVideoBackground(
                            videoUriString = videoUri,
                            isPlaying = isSessionActive && appState != AppState.IDLE
                        )
                    } else {
                        Visualizer(
                            state = appState,
                            audioLevel = audioLevel
                        )
                    }
                }
            }

            // Footer Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Optional Text Input Bar
                AnimatedVisibility(
                    visible = showTextInput,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(ZoyaSurfaceDark)
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textInputValue,
                            onValueChange = { textInputValue = it },
                            placeholder = {
                                Text(
                                    text = "Message ${config.assistantName}...",
                                    color = ZoyaTextMuted.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ZoyaTextWhite,
                                unfocusedTextColor = ZoyaTextWhite,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_text_input"),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (textInputValue.isNotBlank()) {
                                    viewModel.sendTextMessage(textInputValue)
                                    textInputValue = ""
                                    showTextInput = false
                                }
                            },
                            enabled = textInputValue.isNotBlank(),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (textInputValue.isNotBlank()) ZoyaCyan else ZoyaSurfaceElevated)
                                .testTag("send_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (textInputValue.isNotBlank()) ZoyaBgDark else ZoyaTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Main Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Keyboard input toggle
                    IconButton(
                        onClick = { showTextInput = !showTextInput },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (showTextInput) ZoyaCyan.copy(alpha = 0.2f) else ZoyaSurfaceDark)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                            .testTag("toggle_keyboard_button")
                    ) {
                        Icon(
                            imageVector = if (showTextInput) Icons.Default.Close else Icons.Default.Keyboard,
                            contentDescription = "Type Message",
                            tint = if (showTextInput) ZoyaCyan else ZoyaTextWhite.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Main Initialize / End Session Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (isSessionActive) ZoyaRose.copy(alpha = 0.2f)
                                else ZoyaSurfaceElevated
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSessionActive) ZoyaRose.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                handleSessionClick()
                            }
                            .testTag("main_session_action_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isSessionActive) {
                                Icon(
                                    imageVector = Icons.Default.MicOff,
                                    contentDescription = null,
                                    tint = ZoyaRose,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "End Session",
                                    color = ZoyaRose,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = ZoyaTextWhite.copy(alpha = 0.9f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Initialize ${config.assistantName.uppercase()}",
                                    color = ZoyaTextWhite.copy(alpha = 0.95f),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    // Volume & Mute Button with popup slider
                    Box {
                        IconButton(
                            onClick = { showVolumePopup = !showVolumePopup },
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (showVolumePopup) ZoyaSurfaceLight else ZoyaSurfaceDark)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                                .testTag("volume_control_button")
                        ) {
                            Icon(
                                imageVector = if (config.isMuted || config.volume <= 0.01f) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Volume",
                                tint = if (config.isMuted) ZoyaTextMuted.copy(alpha = 0.4f) else ZoyaTextWhite.copy(alpha = 0.8f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Floating volume popup slider
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showVolumePopup,
                            enter = fadeIn() + slideInVertically { it / 2 },
                            exit = fadeOut() + slideOutVertically { it / 2 },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 66.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(180.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp)),
                                color = ZoyaSurfaceDark,
                                tonalElevation = 8.dp
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (config.isMuted) "Muted" else "Volume",
                                            color = ZoyaTextMuted,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = if (config.isMuted) "0%" else "${(config.volume * 100).toInt()}%",
                                            color = ZoyaCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Slider(
                                        value = if (config.isMuted) 0f else config.volume,
                                        onValueChange = { newVol ->
                                            viewModel.updateVolume(newVol)
                                        },
                                        valueRange = 0f..1f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = ZoyaCyan,
                                            activeTrackColor = ZoyaCyan,
                                            inactiveTrackColor = ZoyaSurfaceElevated
                                        ),
                                        modifier = Modifier.testTag("volume_slider")
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ZoyaSurfaceElevated)
                                            .clickable { viewModel.toggleMute() }
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (config.isMuted) "Unmute Audio" else "Mute Voice",
                                            color = if (config.isMuted) ZoyaCyan else ZoyaTextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modals
    if (showTopicModal) {
        TopicScriptModal(
            assistantName = config.assistantName,
            currentTopic = config.activeTopicOrScript,
            onSave = { newTopic ->
                viewModel.updateActiveTopic(newTopic)
            },
            onClose = { showTopicModal = false }
        )
    }

    if (showVoiceModal) {
        VoiceSelectorModal(
            currentVoice = config.voiceName,
            currentPitch = config.speechPitch,
            currentRate = config.speechRate,
            onSave = { voiceName, pitch, rate ->
                viewModel.updateVoice(voiceName, pitch, rate)
            },
            onTestVoice = { sampleText ->
                viewModel.testVoice(sampleText)
            },
            onClose = { showVoiceModal = false }
        )
    }

    if (showSetupModal) {
        SetupScreen(
            currentConfig = config,
            onSave = { updated ->
                viewModel.updateConfig(updated)
            },
            onClose = { showSetupModal = false }
        )
    }

    if (showSpecsModal) {
        SpecificationModal(
            assistantName = config.assistantName,
            onClose = { showSpecsModal = false }
        )
    }

    if (showHistorySheet) {
        ChatHistorySheet(
            assistantName = config.assistantName,
            messages = messages,
            onClearHistory = {
                viewModel.clearChatHistory()
            },
            onDismiss = { showHistorySheet = false }
        )
    }

    if (showPermissionDialog) {
        PermissionModal(
            assistantName = config.assistantName,
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            },
            onDismiss = { showPermissionDialog = false }
        )
    }
}
