package com.example.xiaoyi.model

data class Product(
    val id: Long,
    val name: String,//商品名
    val price: Double,//商品价格
    val description: String,//商品描述
    val location: String,//商品位置
    val imageUrl: String,//商品图片
    val userId: Long,//卖家ID
    val createdAt: String//商品发布时间
)