package com.example.xiaoyi.model

data class Wanted(
    val id: Long = 0,
    val title: String = "",//求购标题
    val description: String = "",//求购描述
    val categoryId: Long = 0,//分类ID
    val maxPrice: Double = 0.0,//最高价格
    val status: Int = 0,//状态
    val viewCount: Int = 0,//浏览次数
    val buyerId: Long = 0,//买家ID
    val buyerName: String = "未知买家",//买家名称
    val createdAt: String = "",//发布时间
    val updatedAt: String = ""//更新时间
)