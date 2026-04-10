package com.ahmed.familychat

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ahmed.familychat.ui.navigation.FamilyChatNavGraph
import com.ahmed.familychat.ui.theme.FamilyChatTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "FamilyChat Desktop"
    ) {
        FamilyChatTheme {
            FamilyChatNavGraph()
        }
    }
}
