package com.example.xiaoyi.model

data class UserAddress(
    val id: Long,//主键id
    val userId: Long,//所属用户的ID
    val receiverName: String,//收件人姓名
    val phone: String,//收件人手机号
    val province: String,//省份名称
    val city: String,//城市名称
    val district: String,//区/县名称
    val detailAddress: String,//详细地址
    val isDefault: Int,//是否默认地址：0-否，1-是
    val createdAt: String?,//创建时间
    val updatedAt: String?//更新时间
)