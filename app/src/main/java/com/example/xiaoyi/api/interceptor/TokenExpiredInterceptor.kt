package com.example.xiaoyi.api.interceptor

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.xiaoyi.MainActivity
import com.example.xiaoyi.utils.UserManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenExpiredInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        if (url.contains("/api/users/login") || url.contains("/api/users/register")) {
            return chain.proceed(request)
        }
        val response = chain.proceed(request)
        if (response.code == 401){
            UserManager.logout()
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "登录已过期，请重新登录", Toast.LENGTH_LONG).show()

                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
        return response
    }
}