package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainScreen
import com.example.ui.theme.ZoyaBgDark
import com.example.ui.theme.ZoyaTheme
import com.example.ui.viewmodel.ZoyaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZoyaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ZoyaBgDark
                ) {
                    val viewModel: ZoyaViewModel = viewModel()
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
