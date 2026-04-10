package com.ahmed.familychat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.ahmed.familychat.model.User
import com.ahmed.familychat.network.SocketManager
import com.ahmed.familychat.ui.navigation.Routes
import com.ahmed.familychat.utils.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    myUserId: String,
    myUserName: String,
    navController: NavController
) {
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var newMemberPhone by remember { mutableStateOf("") }
    var newMemberName by remember { mutableStateOf("") }
    val familyMembers = remember { mutableStateListOf<User>() }

    // Ensure socket is connected
    LaunchedEffect(Unit) {
        if (!SocketManager.isConnected()) {
            SocketManager.connect()
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة فرد من العائلة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newMemberName,
                        onValueChange = { newMemberName = it },
                        label = { Text("الاسم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMemberPhone,
                        onValueChange = { newMemberPhone = it },
                        label = { Text("رقم الهاتف (يُستخدم كمعرّف)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newMemberName.isNotBlank() && newMemberPhone.isNotBlank()) {
                        familyMembers.add(User(id = newMemberPhone, name = newMemberName, phoneNumber = newMemberPhone))
                        newMemberName = ""
                        newMemberPhone = ""
                        showAddDialog = false
                    }
                }) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("FamilyChat", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("مرحباً، $myUserName", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF075E54)
                ),
                actions = {
                    IconButton(onClick = {
                        scope.launch { UserPreferences.clearUserData() }
                        navController.navigate(Routes.REGISTER) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Lucide.User, contentDescription = "تسجيل الخروج", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF25D366)
            ) {
                Icon(Lucide.Plus, contentDescription = "إضافة فرد", tint = Color.White)
            }
        }
    ) { innerPadding ->
        if (familyMembers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👨‍👩‍👧‍👦", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "أضف أفراد العائلة للبدء",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        "اضغط على + للإضافة",
                        fontSize = 13.sp,
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(familyMembers) { member ->
                    FamilyMemberItem(
                        member = member,
                        onClick = {
                            navController.navigate(
                                Routes.chat(
                                    myId = myUserId,
                                    myName = myUserName,
                                    peerId = member.id,
                                    peerName = member.name
                                )
                            )
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
                }
            }
        }
    }
}

@Composable
fun FamilyMemberItem(member: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF128C7E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = member.phoneNumber,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    if (member.status == "online") Color(0xFF25D366) else Color.Gray
                )
        )
    }
}
