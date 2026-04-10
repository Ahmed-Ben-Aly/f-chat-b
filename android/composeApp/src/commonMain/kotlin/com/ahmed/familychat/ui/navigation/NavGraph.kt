package com.ahmed.familychat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ahmed.familychat.ui.screens.CallScreen
import com.ahmed.familychat.ui.screens.ChatScreen
import com.ahmed.familychat.ui.screens.HomeScreen
import com.ahmed.familychat.ui.screens.RegisterScreen
import com.ahmed.familychat.utils.UserPreferences
import com.ahmed.familychat.network.SocketManager

object Routes {
    const val REGISTER = "register"
    const val HOME = "home/{userId}/{userName}"
    const val CHAT = "chat/{myId}/{myName}/{peerId}/{peerName}"

    fun home(userId: String, userName: String) = "home/$userId/$userName"
    fun chat(myId: String, myName: String, peerId: String, peerName: String) =
        "chat/$myId/$myName/$peerId/$peerName"
    const val CALL = "call/{callerId}/{callerName}/{isIncoming}/{myId}"
    fun call(callerId: String, callerName: String, isIncoming: Boolean, myId: String) = 
        "call/$callerId/$callerName/$isIncoming/$myId"
}

@Composable
fun FamilyChatNavGraph() {
    val navController = rememberNavController()
    val savedUserId by UserPreferences.getUserId().collectAsState(initial = null)
    val savedUserName by UserPreferences.getUserName().collectAsState(initial = null)

    val startDestination = Routes.REGISTER

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.REGISTER) {
            LaunchedEffect(savedUserId, savedUserName) {
                if (!savedUserId.isNullOrBlank() && !savedUserName.isNullOrBlank()) {
                    navController.navigate(Routes.home(savedUserId!!, savedUserName!!)) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            }
            RegisterScreen(navController = navController)
        }

        composable(
            route = Routes.HOME,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Use safe argument access for KMP without getString
            val userId = backStackEntry.savedStateHandle.get<String>("userId") ?: ""
            val userName = backStackEntry.savedStateHandle.get<String>("userName") ?: ""
            HomeScreen(
                myUserId = userId,
                myUserName = userName,
                navController = navController
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(
                navArgument("myId") { type = NavType.StringType },
                navArgument("myName") { type = NavType.StringType },
                navArgument("peerId") { type = NavType.StringType },
                navArgument("peerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val myId = backStackEntry.savedStateHandle.get<String>("myId") ?: ""
            val myName = backStackEntry.savedStateHandle.get<String>("myName") ?: ""
            val peerId = backStackEntry.savedStateHandle.get<String>("peerId") ?: ""
            val peerName = backStackEntry.savedStateHandle.get<String>("peerName") ?: ""
            ChatScreen(
                myId = myId,
                myName = myName,
                peerId = peerId,
                peerName = peerName,
                navController = navController
            )
        }
        composable(
            route = Routes.CALL,
            arguments = listOf(
                navArgument("callerId") { type = NavType.StringType },
                navArgument("callerName") { type = NavType.StringType },
                navArgument("isIncoming") { type = NavType.BoolType },
                navArgument("myId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val callerId = backStackEntry.arguments?.getString("callerId") ?: ""
            val callerName = backStackEntry.arguments?.getString("callerName") ?: ""
            val isIncoming = backStackEntry.arguments?.getBoolean("isIncoming") ?: false
            val myId = backStackEntry.arguments?.getString("myId") ?: ""
            
            CallScreen(
                callerId = callerId,
                callerName = callerName,
                isIncoming = isIncoming,
                myId = myId,
                navController = navController
            )
        }
    }

    // Global Incoming Call Listener
    LaunchedEffect(Unit) {
        SocketManager.incomingCallFlow.collect { json ->
            val callerId = json.optString("callerId")
            val callerName = json.optString("callerName")
            val myId = savedUserId ?: ""
            navController.navigate(Routes.call(callerId, callerName, true, myId))
        }
    }
}
