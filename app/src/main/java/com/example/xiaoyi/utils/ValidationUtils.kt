package com.example.xiaoyi.utils

object ValidationUtils{
    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^1[3-9]\\d{9}$"))
    }
}