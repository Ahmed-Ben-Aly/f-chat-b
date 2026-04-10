package com.ahmed.familychat.utils

import kotlinx.coroutines.flow.Flow

expect object UserPreferences {
    fun getUserId(): Flow<String?>
    fun getUserName(): Flow<String?>
    fun getPhoneNumber(): Flow<String?>
    suspend fun saveUserData(userId: String, name: String, phoneNumber: String)
    suspend fun clearUserData()
}
