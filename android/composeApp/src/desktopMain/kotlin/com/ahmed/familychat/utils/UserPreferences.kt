package com.ahmed.familychat.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.prefs.Preferences

actual object UserPreferences {
    private val prefs = Preferences.userRoot().node("com.ahmed.familychat")
    
    private val _userId = MutableStateFlow(prefs.get("user_id", null))
    private val _userName = MutableStateFlow(prefs.get("user_name", null))
    private val _phoneNumber = MutableStateFlow(prefs.get("phone_number", null))

    actual fun getUserId(): Flow<String?> = _userId
    actual fun getUserName(): Flow<String?> = _userName
    actual fun getPhoneNumber(): Flow<String?> = _phoneNumber

    actual suspend fun saveUserData(userId: String, name: String, phoneNumber: String) {
        prefs.put("user_id", userId)
        prefs.put("user_name", name)
        prefs.put("phone_number", phoneNumber)
        _userId.value = userId
        _userName.value = name
        _phoneNumber.value = phoneNumber
    }

    actual suspend fun clearUserData() {
        prefs.remove("user_id")
        prefs.remove("user_name")
        prefs.remove("phone_number")
        _userId.value = null
        _userName.value = null
        _phoneNumber.value = null
    }
}
