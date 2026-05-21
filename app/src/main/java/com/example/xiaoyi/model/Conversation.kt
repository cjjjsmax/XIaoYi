package com.example.xiaoyi.model

data class Conversation(
    val id: Long = 0L,//会话ID
    val initiatorId: Long = 0L,//发起者用户ID
    val receiverId: Long = 0L,//接收者用户ID
    val productId: Long? = null,//关联的商品ID
    val status: String = "",//会话状态
    val createdAt: String = "",//创建时间
    val lastMessage: String = "",//最新消息内容
    val lastMessageTime: String = "",//最新消息时间
    val otherUserId: Long = 0L,//对方用户ID
    val otherUserName: String = "",//对方用户名
    val otherUserAvatar: String = "",//对方用户头像
    val unreadCount: Int = 0//未读消息数量
)