package com.example.xiaoyi.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.xiaoyi.model.User

object UserManager {

    private lateinit var sharedPreferences: SharedPreferences
    var currentUserId: Long = 0L
    var user: User? = null
    var token: String = ""
    val isLoggedIn: Boolean get() = currentUserId > 0L

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getLong("user_id", 0L)
        token = sharedPreferences.getString("token", "") ?: ""
    }

    fun login(user: User, token: String = "") {
        this.user = user
        this.currentUserId = user.id
        this.token = token
        val editor = sharedPreferences.edit()
        editor.putLong("user_id", user.id)
        editor.putString("token", token)
        editor.apply()
    }

    fun logout() {
        this.user = null
        this.currentUserId = 0L
        this.token = ""
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.remove("token")
        editor.apply()
    }
}