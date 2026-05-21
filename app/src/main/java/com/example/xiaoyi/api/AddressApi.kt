package com.example.xiaoyi.api

import com.example.xiaoyi.model.AddressRequest
import retrofit2.Call
import retrofit2.http.*

interface AddressApi {
    @GET("/api/addresses")
    fun getAddresses(@Query("userId") userId: Long): Call<Map<String, Any>>

    @GET("/api/addresses/{id}")
    fun getAddressById(@Path("id") id: Long): Call<Map<String, Any>>

    @POST("/api/addresses")
    fun createAddress(@Body request: AddressRequest): Call<Map<String, Any>>

    @PUT("/api/addresses/{id}")
    fun updateAddress(@Path("id") id: Long, @Body request: AddressRequest): Call<Map<String, Any>>

    @DELETE("/api/addresses/{id}")
    fun deleteAddress(@Path("id") id: Long): Call<Map<String, Any>>

    @GET("/api/regions")
    fun getRegions(@Query("parentId") parentId: String): Call<Map<String, Any>>

    @GET("/api/regions/level")
    fun getRegionsByLevel(@Query("level") level: Int): Call<Map<String, Any>>
}