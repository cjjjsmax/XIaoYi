package com.example.xiaoyi.api

import com.example.xiaoyi.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

interface UserApi {
    //用户注册
    @POST("/api/users/register")
    fun register(@Body user: User): Call<Map<String, Any>>
    //用户登录
    @POST("/api/users/login")
    fun login(@Body loginData: Map<String, String>): Call<Map<String, Any>>
    //获取用户信息
    @GET("/api/users/{userId}")
    fun getUserById(@Path("userId") userId: Long): Call<Map<String, Any>>
    @Multipart
    @POST("/api/users/upload-avatar")
    fun uploadAvatar(@Part file: MultipartBody.Part,@Part("userId")userId: RequestBody): Call<Map<String, Any>>

    @POST("/api/users/update")
    fun updateUser(
        @Query("userId") userId: Long,
        @Query("username") username: String,
        @Query("studentId") studentId: String,
        @Query("school") school: String
    ): Call<Map<String, Any>>

    @POST("/api/users/update-security")
    fun updateAccountSecurity(
        @Query("userId") userId: Long,
        @Query("phone") phone: String,
        @Query("oldPassword") oldPassword: String?,
        @Query("newPassword") newPassword: String?
    ): Call<Map<String, Any>>
}