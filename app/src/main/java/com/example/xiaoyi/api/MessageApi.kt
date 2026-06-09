package com.example.xiaoyi.api


import com.example.xiaoyi.model.Result
import com.example.xiaoyi.model.SendMessageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApi {
    @GET("/api/messages/list")
    fun getMessages(@Query("userId") userId: Long): Call<Result<List<Map<String, Any>>>>

    @GET("/api/messages/conversation/{conversationId}")
    fun getConversationMessages(@Path("conversationId") conversationId: Long): Call<Result<List<Map<String, Any>>>>

    @POST("/api/messages/send")
    fun sendMessage(@Body request: SendMessageRequest): Call<Result<String>>

    @PUT("/api/messages/read/{messageId}")
    fun markAsRead(@Path("messageId") messageId: Long): Call<Result<String>>

    @PUT("/api/messages/read/conversation/{conversationId}")
    fun markAsRead(@Path("conversationId") conversationId: Long, @Query("userId") userId: Long): Call<Result<String>>

}