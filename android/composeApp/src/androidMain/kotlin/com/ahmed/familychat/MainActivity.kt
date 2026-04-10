package com.ahmed.familychat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ahmed.familychat.ui.navigation.FamilyChatNavGraph
import com.ahmed.familychat.ui.theme.FamilyChatTheme
import com.ahmed.familychat.utils.UserPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize UserPreferences for Android
        UserPreferences.init(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            FamilyChatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FamilyChatNavGraph()
                }
            }
        }
    }
}
