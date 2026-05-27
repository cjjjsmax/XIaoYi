package com.example.xiaoyi.model

data class User(
    val id: Long = 0L,//用户唯一标识符
    val username: String = "",//用户名
    val studentId: String = "",//学号
    val passwordHash: String = "",//hash密码
    val avatarUrl: String = "",//头像
    val phone: String = "",//手机号码
    val school: String = "",//学校
    val creditScore: String = "",//信誉值
    val createdAt: Long = System.currentTimeMillis(),//创建时间
    val token: String = ""
)

data class LoginRequest(
    val username: String,//用户名
    val passwordHash: String//密码hash
)

data class RegisterRequest(
    val username: String,//用户名
    val password: String,//密码
    val studentId: String = "",//学号
    val phone: String = ""//手机号
)

data class AuthResponse(
    val success: Boolean,//是否成功
    val message: String,//消息
    val user: User? = null,//用户信息
    val token: String? = null//token
)
