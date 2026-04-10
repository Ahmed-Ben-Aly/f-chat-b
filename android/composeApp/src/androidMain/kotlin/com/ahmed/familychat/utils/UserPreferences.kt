package com.ahmed.familychat.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

actual object UserPreferences {
    private var context: Context? = null
    
    fun init(ctx: Context) {
        context = ctx
    }

    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val PHONE_NUMBER_KEY = stringPreferencesKey("phone_number")

    actual fun getUserId(): Flow<String?> = context!!.dataStore.data.map { it[USER_ID_KEY] }
    actual fun getUserName(): Flow<String?> = context!!.dataStore.data.map { it[USER_NAME_KEY] }
    actual fun getPhoneNumber(): Flow<String?> = context!!.dataStore.data.map { it[PHONE_NUMBER_KEY] }

    actual suspend fun saveUserData(userId: String, name: String, phoneNumber: String) {
        context!!.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = name
            prefs[PHONE_NUMBER_KEY] = phoneNumber
        }
    }

    actual suspend fun clearUserData() {
        context!!.dataStore.edit { it.clear() }
    }
}
