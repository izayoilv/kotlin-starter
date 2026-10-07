package com.example.notesapp

import android.content.Context

enum class FontSize(val prefValue: String, val sp: Float) {
  SMALL("small", 14f),
  MEDIUM("medium", 18f),
  LARGE("large", 24f),
}

class PrefsManager(context: Context) {
  private val prefs = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)

  var userName: String
    get() = prefs.getString("user_name", "").orEmpty()
    set(value) = prefs.edit().putString("user_name", value).apply()

  var fontSize: FontSize
    get() {
      val stored = prefs.getString("font_size", null)
      return FontSize.entries.firstOrNull { it.prefValue == stored } ?: FontSize.MEDIUM
    }
    set(value) = prefs.edit().putString("font_size", value.prefValue).apply()

  var showUserInfo: Boolean
    get() = prefs.getBoolean("show_user_info", true)
    set(value) = prefs.edit().putBoolean("show_user_info", value).apply()
}
