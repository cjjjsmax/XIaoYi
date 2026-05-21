package com.example.xiaoyi.model

data class AddressRequest(
    val userId: Long,
    val receiverName: String,
    val province: String,
    val city: String,
    val district: String,
    val street: String,
    val detailAddress: String,
    val isDefault: Int
)