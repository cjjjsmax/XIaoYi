package com.example.xiaoyi.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,//商品名
    val price: Double,//商品价格
    val description: String,//商品描述
    val location: String,//商品位置
    val imageUrl: String,//商品图片
    val userId: Long,//卖家ID
    val createdAt: String,//商品发布时间
    val sellerName: String = "",//卖家名称
    val categoryId: Long = 0 //分类ID
)