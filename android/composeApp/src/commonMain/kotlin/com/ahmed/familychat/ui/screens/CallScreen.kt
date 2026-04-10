package com.ahmed.familychat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.composables.icons.lucide.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ahmed.familychat.network.SocketManager
import kotlinx.coroutines.launch

@Composable
fun CallScreen(
    callerId: String,
    callerName: String,
    isIncoming: Boolean,
    myId: String,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var callStatus by remember { mutableStateOf(if (isIncoming) "مكالمة واردة..." else "جاري الاتصال...") }
    var isCallActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Listen for call events
        launch {
            SocketManager.callAcceptedFlow.collect {
                callStatus = "متصل"
                isCallActive = true
            }
        }
        launch {
            SocketManager.callRejectedFlow.collect {
                callStatus = "تم رفض المكالمة"
                navController.popBackStack()
            }
        }
        launch {
            SocketManager.callEndedFlow.collect {
                callStatus = "انتهت المكالمة"
                navController.popBackStack()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF075E54)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // User Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF128C7E)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                    text = callerName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = callerName,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = callStatus,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isIncoming && !isCallActive) {
                    // Accept Button
                    IconButton(
                        onClick = {
                            SocketManager.acceptCall(callerId, myId)
                            isCallActive = true
                            callStatus = "متصل"
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                    ) {
                        Icon(Lucide.Phone, contentDescription = "رد", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // Reject Button
                    IconButton(
                        onClick = {
                            SocketManager.rejectCall(callerId, myId)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    ) {
                        Icon(Lucide.X, contentDescription = "رفض", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                } else {
                    // End Call Button
                    IconButton(
                        onClick = {
                            SocketManager.endCall(callerId)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    ) {
                        Icon(Lucide.PhoneOff, contentDescription = "إنهاء", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}
