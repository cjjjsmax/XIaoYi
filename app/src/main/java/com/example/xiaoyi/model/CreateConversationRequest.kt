package com.example.xiaoyi.model

data class CreateConversationRequest(
    val initiatorId: Long,//发起者ID
    val receiverId: Long,//接收者ID
    val productId: Long?,//关联的商品ID
    val type: String//会话类型
)
