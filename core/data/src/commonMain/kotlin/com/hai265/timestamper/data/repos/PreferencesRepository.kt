package com.hai265.timestamper.data.repos

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    fun <T> get(key: Preferences.Key<T>, default: T): Flow<T> =
        dataStore.data.map { it[key] ?: default }

    suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[key] = value
            }
        }
    }

    fun hideKeyboardOnScreenTap() = get(CLOSE_KEYBOARD_TAP_SCREEN, false)
    suspend fun updateHideKeyboardOnScreenTap(enable: Boolean) =
        set(CLOSE_KEYBOARD_TAP_SCREEN, enable)

    fun pauseVideoOnKeyboardVisible() = get(PAUSE_VIDEO_KEYBOARD_VISIBLE, false)
    suspend fun updatePauseVideoOnKeyboardVisible(enable: Boolean) =
        set(PAUSE_VIDEO_KEYBOARD_VISIBLE, enable)

    companion object {
        val CLOSE_KEYBOARD_TAP_SCREEN = booleanPreferencesKey("close_keyboard_tap_screen")
        val PAUSE_VIDEO_KEYBOARD_VISIBLE = booleanPreferencesKey("pause_on_keyboard_visible")
    }
}