package com.example.xiaoyi.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
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
