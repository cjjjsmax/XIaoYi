package com.example.xiaoyi.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class QualityInspectionRequest(
    val productId: Long
)

interface QualityInspectionApi {
    @POST("api/products/inspect")
    fun generateProductReport(@Body request: QualityInspectionRequest): Call<Map<String, Any>>
}