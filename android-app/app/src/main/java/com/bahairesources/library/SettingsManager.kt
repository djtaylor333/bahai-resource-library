package com.bahairesources.library

import android.content.Context
import android.content.SharedPreferences

object SettingsManager {
    private const val PREFS_NAME = "bahai_library_settings"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_FONT_SIZE = "font_size"
    private const val KEY_LOCATION_MODE = "location_mode" // "gps", "manual", "disabled"
    private const val KEY_MANUAL_LOCATION = "manual_location"
    private const val KEY_SHOW_OTHER_RELIGIONS = "show_other_religions"
    private const val KEY_AUTO_LOCATION = "auto_location"
    
    // Font sizes
    const val FONT_SMALL = 14f
    const val FONT_MEDIUM = 16f
    const val FONT_LARGE = 18f
    const val FONT_EXTRA_LARGE = 20f
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // Theme Settings
    fun isDarkMode(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_DARK_MODE, false)
    }
    
    fun setDarkMode(context: Context, isDark: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DARK_MODE, isDark).apply()
    }
    
    fun toggleDarkMode(context: Context): Boolean {
        val newMode = !isDarkMode(context)
        setDarkMode(context, newMode)
        return newMode
    }
    
    // Font Settings
    fun getFontSize(context: Context): Float {
        return getPrefs(context).getFloat(KEY_FONT_SIZE, FONT_MEDIUM)
    }
    
    fun setFontSize(context: Context, size: Float) {
        getPrefs(context).edit().putFloat(KEY_FONT_SIZE, size).apply()
    }
    
    fun getFontSizeName(context: Context): String {
        return when (getFontSize(context)) {
            FONT_SMALL -> "Small"
            FONT_MEDIUM -> "Medium"
            FONT_LARGE -> "Large"
            FONT_EXTRA_LARGE -> "Extra Large"
            else -> "Medium"
        }
    }
    
    // Location Settings
    fun getLocationMode(context: Context): String {
        return getPrefs(context).getString(KEY_LOCATION_MODE, "gps") ?: "gps"
    }
    
    fun setLocationMode(context: Context, mode: String) {
        getPrefs(context).edit().putString(KEY_LOCATION_MODE, mode).apply()
    }
    
    fun getManualLocation(context: Context): String {
        return getPrefs(context).getString(KEY_MANUAL_LOCATION, "New York, NY, USA") ?: "New York, NY, USA"
    }
    
    fun setManualLocation(context: Context, location: String) {
        getPrefs(context).edit().putString(KEY_MANUAL_LOCATION, location).apply()
    }
    
    // Other Settings
    fun getShowOtherReligions(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SHOW_OTHER_RELIGIONS, false)
    }
    
    fun setShowOtherReligions(context: Context, show: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_OTHER_RELIGIONS, show).apply()
    }
    
    fun getAutoLocation(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_LOCATION, true)
    }
    
    fun setAutoLocation(context: Context, auto: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_LOCATION, auto).apply()
    }
    
    fun resetToDefaults(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}