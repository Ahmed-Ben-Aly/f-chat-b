package com.ahmed.familychat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary colors - a warm family-friendly green/teal palette
private val FamilyGreen = Color(0xFF25D366)      // WhatsApp-like green
private val FamilyDarkGreen = Color(0xFF075E54)
private val FamilyTeal = Color(0xFF128C7E)
private val LightBackground = Color(0xFFECE5DD)  // Warm off-white background
private val MessageBubbleSent = Color(0xFFDCF8C6) // Sent message: light green
private val MessageBubbleReceived = Color(0xFFFFFFFF) // Received message: white

private val LightColorScheme = lightColorScheme(
    primary = FamilyDarkGreen,
    onPrimary = Color.White,
    primaryContainer = FamilyGreen,
    onPrimaryContainer = Color.White,
    secondary = FamilyTeal,
    onSecondary = Color.White,
    background = LightBackground,
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

val MessageBubbleSentColor = MessageBubbleSent
val MessageBubbleReceivedColor = MessageBubbleReceived

@Composable
fun FamilyChatTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
