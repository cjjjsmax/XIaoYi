package com.example.xiaoyi.model

data class Region(
    val id: Long,//地区ID
    val name: String,//地区名称
    val parentId: String,//父级地区编码
    val level: Int,//地区级别：1-省，2-市，3-区县，4-乡镇街道
    val code: String//地区编码
)
