package com.example.xiaoyi.data.database.entity

import androidx.room3.PrimaryKey

data class WantedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, //求购标题
    val description: String, //求购描述
    val categoryId: Long = 0, //分类ID
    val maxPrice: Double,//最高价格
    val status: Int = 0,//状态
    val viewCount: Int = 0,//浏览次数
    val buyerId: Long = 0,//买家ID
    val buyerName: String = "", //买家名称（从 user 对象获取）
    val createdAt: String = "", //发布时间
    val updatedAt: String = ""  //更新时间
)

