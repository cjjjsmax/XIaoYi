package com.example.xiaoyi.api

import com.example.xiaoyi.model.CreateConversationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ConversationApi {
    @GET("/api/conversations/list")
    fun getConversations(@Query("userId") userId: Long): Call<Map<String, Any>>

    @GET("/api/conversations/detail/{conversationId}")
    fun getConversationDetail(@Path("conversationId") conversationId: Long): Call<Map<String, Any>>

    @POST("/api/conversations/create")
    fun createConversation(@Body request: CreateConversationRequest): Call<Map<String, Any>>

    @PUT("/api/conversations/close/{conversationId}")
    fun closeConversation(@Path("conversationId") conversationId: Long): Call<Map<String, Any>>

    @GET("/api/conversations/find")
    fun findConversation(@Query("userId1") userId1: Long, @Query("userId2") userId2: Long): Call<Map<String, Any>>
}