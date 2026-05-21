package com.example.xiaoyi.model



data class Message(
    val id: Long = 0L,//消息ID
    val conversationId: Long = 0L,//会话ID
    val senderId: Long = 0L,//发送者ID
    val receiverId: Long = 0L,//接收者ID
    val content: String = "",//消息内容
    val type: String = "",//消息类型
    val timestamp: String = "",//时间戳
    val isRead: Boolean = false,//是否已读
    val createdAt: String = ""//创建时间
)
