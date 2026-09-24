package org.deltacv.myapplication.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rover_session_prefs", Context.MODE_PRIVATE)

    fun saveSession(userOrEmail: String, cum: String, pass: String, userId: String) {
        prefs.edit()
            .putString("session_user_email", userOrEmail)
            .putString("session_cum", cum)
            .putString("session_pass", pass)
            .putString("session_user_id", userId)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun hasActiveSession(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getSavedUserId(): String? = prefs.getString("session_user_id", null)
    fun getSavedUserOrEmail(): String? = prefs.getString("session_user_email", null)
    fun getSavedCum(): String? = prefs.getString("session_cum", null)
    fun getSavedPass(): String? = prefs.getString("session_pass", null)
}
