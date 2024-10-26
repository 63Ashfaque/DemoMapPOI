package com.ashfaque.demopoi.shared_preference

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferenceManager? = null

        fun getInstance(context: Context): SharedPreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPreferenceManager(context).also { INSTANCE = it }
            }
        }
    }

    // Function to save a string value
    fun saveString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    // Function to retrieve a string value
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // Function to save an integer value
    fun saveInt(key: String, value: Int) {
        with(sharedPreferences.edit()) {
            putInt(key, value)
            apply()
        }
    }

    // Function to retrieve an Float value
    fun getFloat(key: String, defaultValue: Float = 10f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }
    // Function to save an Float value
    fun saveFloat(key: String, value: Float) {
        with(sharedPreferences.edit()) {
            putFloat(key, value)
            apply()
        }
    }

    // Function to retrieve an integer value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Function to save a boolean value
    fun saveBoolean(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    // Function to retrieve a boolean value
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Function to clear all shared preferences
    fun clearAll() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
