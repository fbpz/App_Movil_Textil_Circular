package com.example.textilcircular

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.textilcircular.ui.navigation.AppNavHost
import com.example.textilcircular.ui.theme.TextilCircularTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Las dependencias (Room, DataStore, GPS, almacenamiento) se crean en TextilCircularApp
        // y llegan a cada ViewModel mediante AppViewModelProvider.
        setContent {
            TextilCircularTheme {
                AppNavHost()
            }
        }
    }
}
