package com.gquesada.notes.data.datasources

import android.content.Context

class SharedPreferencesDataSource(
    private val context: Context
) {

    private companion object {
        const val SHARED_PREFERENCES_KEY = "SHARED_PREFERENCES_KEY"
        const val SHARED_PREFERENCES_TOKEN_KEY = "SHARED_PREFERENCES_TOKEN_KEY"
    }

    fun saveDeviceToken(token: String) {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        sharedPref.edit()
            .putString(SHARED_PREFERENCES_TOKEN_KEY, token)
            .apply()
    }

    fun getDeviceToken(): String? {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return sharedPref?.getString(SHARED_PREFERENCES_TOKEN_KEY, "")
    }
}