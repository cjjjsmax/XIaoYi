package com.example.xiaoyi.config

//服务器配置
object ServerConfig {
    //服务器地址
    const val HOST = "10.0.2.2"
    //服务器端口
    const val PORT = "8080"
    //完整基础URL
    const val BASE_URL = "http://$HOST:$PORT"

    //构建完整图片URL
    fun getFullImageUrl(imageUrl: String?): String {
        if (imageUrl.isNullOrEmpty()) return ""
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl
        }
        if (imageUrl.startsWith("/")) {
            return "$BASE_URL$imageUrl"
        }
        return "$BASE_URL/$imageUrl"
    }
}