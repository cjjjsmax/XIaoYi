package com.example.xiaoyi.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.xiaoyi.model.User

object UserManager {

    private lateinit var sharedPreferences: SharedPreferences
    var currentUserId: Long = 0L
    var user: User? = null
    val isLoggedIn: Boolean get() = currentUserId > 0L

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        // 从SharedPreferences读取保存的用户ID
        currentUserId = sharedPreferences.getLong("user_id", 0L)
    }

    fun login(user: User) {
        this.user = user
        this.currentUserId = user.id
        val editor = sharedPreferences.edit()
        editor.putLong("user_id", user.id)
        editor.apply()
    }

    fun logout() {
        this.user = null
        this.currentUserId = 0L
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.apply()
    }
}