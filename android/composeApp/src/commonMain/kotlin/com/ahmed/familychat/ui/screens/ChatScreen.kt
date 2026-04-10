package com.ahmed.familychat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ahmed.familychat.model.Message
import com.ahmed.familychat.network.SocketManager
import com.ahmed.familychat.ui.theme.MessageBubbleReceivedColor
import com.ahmed.familychat.ui.theme.MessageBubbleSentColor
import com.ahmed.familychat.ui.navigation.Routes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    myId: String,
    myName: String,
    peerId: String,
    peerName: String,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember { mutableStateListOf<Message>() }
    var textInput by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    
    // Voice recording stubs for now
    var mediaRecorder by remember { mutableStateOf<Any?>(null) }

    // Listen for incoming messages
    LaunchedEffect(Unit) {
        SocketManager.messageFlow.collect { json ->
            val senderId = json.optString("senderId")
            val receiverId = json.optString("receiverId")
            
            // Only display messages relevant to this chat
            if (senderId == peerId && (receiverId == myId || receiverId.isEmpty())) {
                messages.add(
                    Message(
                        id = json.optString("_id"),
                        senderId = senderId,
                        receiverId = receiverId,
                        content = json.optString("content"),
                        type = json.optString("type", "text"),
                        mediaUrl = json.optString("mediaUrl"),
                        createdAt = json.optString("createdAt")
                    )
                )
                scope.launch { listState.animateScrollToItem(messages.size - 1) }
            }
        }
    }


    fun startRecording() {
        // TODO: Implement multiplatform recording
        println("Recording started - Stub")
        isRecording = true
    }

    fun stopRecording() {
        // TODO: Implement multiplatform stop/upload
        println("Recording stopped - Stub")
        isRecording = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF128C7E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(peerName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(peerName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        SocketManager.initiateCall(myId, peerId, myName)
                        navController.navigate(Routes.call(peerId, peerName, false, myId))
                    }) {
                        Icon(Lucide.Phone, contentDescription = "مكالمة صوتية", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF075E54))
            )
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("اكتب رسالة...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF25D366),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (textInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val msg = textInput.trim()
                                textInput = ""
                                messages.add(Message(senderId = myId, receiverId = peerId, content = msg, type = "text"))
                                SocketManager.sendTextMessage(myId, peerId, msg)
                                scope.launch { listState.animateScrollToItem(messages.size - 1) }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366))
                        ) {
                            Icon(Lucide.Send, contentDescription = "إرسال", tint = Color.White)
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (isRecording) stopRecording() else startRecording()
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) Color.Red else Color(0xFF25D366))
                        ) {
                            Icon(
                                if (isRecording) Lucide.Square else Lucide.Mic,
                                contentDescription = if (isRecording) "إيقاف التسجيل" else "تسجيل رسالة صوتية",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFECE5DD))
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message, myId = myId)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, myId: String) {
    val isSentByMe = message.senderId == myId

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isSentByMe) 16.dp else 4.dp,
                        bottomEnd = if (isSentByMe) 4.dp else 16.dp
                    )
                )
                .background(if (isSentByMe) MessageBubbleSentColor else MessageBubbleReceivedColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                when (message.type) {
                    "voice" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Lucide.Mic, contentDescription = null, tint = Color(0xFF075E54), modifier = Modifier.size(20.dp))
                            Text("رسالة صوتية", fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }
                    else -> {
                        Text(
                            text = message.content ?: "",
                            fontSize = 15.sp,
                            color = Color(0xFF1C1B1F)
                        )
                    }
                }

                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                Text(
                    text = time,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
