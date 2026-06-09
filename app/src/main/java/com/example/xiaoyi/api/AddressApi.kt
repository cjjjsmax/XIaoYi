package com.example.xiaoyi.api

import com.example.xiaoyi.model.AddressRequest
import com.example.xiaoyi.model.Result
import retrofit2.Call
import retrofit2.http.*

interface AddressApi {
    @GET("/api/addresses")
    fun getAddresses(@Query("userId") userId: Long): Call<Result<List<Map<String, Any>>>>

    @GET("/api/addresses/{id}")
    fun getAddressById(@Path("id") id: Long): Call<Result<Map<String, Any>>>

    @POST("/api/addresses")
    fun createAddress(@Body request: AddressRequest): Call<Result<String>>

    @PUT("/api/addresses/{id}")
    fun updateAddress(@Path("id") id: Long, @Body request: AddressRequest): Call<Result<String>>

    @DELETE("/api/addresses/{id}")
    fun deleteAddress(@Path("id") id: Long): Call<Result<String>>

    @GET("/api/regions")
    fun getRegions(@Query("parentId") parentId: String): Call<Result<List<Map<String, Any>>>>

    @GET("/api/regions/level")
    fun getRegionsByLevel(@Query("level") level: Int): Call<Result<List<Map<String, Any>>>>
}