package com.example.xiaoyi.model

data class SendMessageRequest(
    val conversationId: Long,//会话ID
    val senderId: Long,//发送者ID
    val receiverId: Long,//接收者ID
    val content: String,//消息内容
    val type: String//消息类型
)
