package com.example.xiaoyi.model

import com.google.gson.annotations.SerializedName


//统一响应类
data class Result<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
) {

    fun isSuccess(): Boolean {
        return code == 200
    }


    fun isError(): Boolean {
        return code != 200
    }
}