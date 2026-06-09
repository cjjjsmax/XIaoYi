package com.example.xiaoyi.api.interceptor

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.xiaoyi.MainActivity
import com.example.xiaoyi.data.database.AppDatabase
import com.example.xiaoyi.utils.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class TokenExpiredInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        
        //公开接口列表
        val publicPaths = listOf(
            "/api/users/login",
            "/api/users/register",
            "/api/products/list",
            "/api/products/search",
            "/api/products/detail",
            "/api/purchase-requests/list",
            "/api/purchase-requests/detail",
            "/images/",
            "/uploads/"
        )
        
        //检查是否是公开接口
        val isPublicPath = publicPaths.any { url.contains(it) }
        
        if (isPublicPath) {
            return chain.proceed(request)
        }
        
        val response = chain.proceed(request)
        if (response.code == 401){
            UserManager.logout()
            GlobalScope.launch(Dispatchers.IO) {
                AppDatabase.getInstance(context).userDao().deleteAll()
            }
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