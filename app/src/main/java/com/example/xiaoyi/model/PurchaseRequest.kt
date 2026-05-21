package com.example.xiaoyi.model

data class PurchaseRequest(
    val id: Long,
    val title: String,//求购标题
    val maxPrice: Double,//最高价格
    val description: String,//求购描述
    val categoryId: Int,//分类ID
    val buyerId: Long,//买家ID
    val status: Int,//状态
    val viewCount: Int,//浏览次数
    val createdAt: String//创建时间
)