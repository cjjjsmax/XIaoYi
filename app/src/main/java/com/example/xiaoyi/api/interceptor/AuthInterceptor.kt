package com.example.xiaoyi.api.interceptor

import com.example.xiaoyi.utils.UserManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        if (url.contains("/api/users/login") || url.contains("/api/users/register")) {
            return chain.proceed(originalRequest)
        }
        val token = UserManager.token
        val requestWithAuth: Request = if (token.isNotEmpty()){
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }else{
            originalRequest
        }
        return chain.proceed(requestWithAuth)
    }
}