package com.example.handyhood.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create DataStore
private val Context.dataStore by preferencesDataStore(name = "profile_data")

object ProfileRepository {

    private val KEY_NAME = stringPreferencesKey("name")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_NEIGHBORHOOD = stringPreferencesKey("neighborhood")
    private val KEY_BIRTHDAY = stringPreferencesKey("birthday")
    private val KEY_IMAGE = stringPreferencesKey("profile_image")
    private val KEY_VERIFIED = stringPreferencesKey("verified")

    suspend fun saveProfile(
        context: Context,
        name: String,
        email: String,
        neighborhood: String,
        birthday: String,
        imageUri: String,
        verified: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NAME] = name
            prefs[KEY_EMAIL] = email
            prefs[KEY_NEIGHBORHOOD] = neighborhood
            prefs[KEY_BIRTHDAY] = birthday
            prefs[KEY_IMAGE] = imageUri
            prefs[KEY_VERIFIED] = verified.toString()
        }
    }

    fun loadProfile(context: Context): Flow<ProfileData> =
        context.dataStore.data.map { prefs ->
            ProfileData(
                name = prefs[KEY_NAME] ?: "",
                email = prefs[KEY_EMAIL] ?: "",
                neighborhood = prefs[KEY_NEIGHBORHOOD] ?: "",
                birthday = prefs[KEY_BIRTHDAY] ?: "",
                imageUri = prefs[KEY_IMAGE] ?: "",
                verified = prefs[KEY_VERIFIED]?.toBoolean() ?: false
            )
        }
}

data class ProfileData(
    val name: String,
    val email: String,
    val neighborhood: String,
    val birthday: String,
    val imageUri: String,
    val verified: Boolean
)
