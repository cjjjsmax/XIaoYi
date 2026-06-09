package com.example.xiaoyi.api.interceptor

import com.example.xiaoyi.utils.UserManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {//认证拦截器
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()//获取原始请求
        val url = originalRequest.url.toString()//获取请求url

        //公开接口列表（不需要登录的接口，不添加token）
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
            return chain.proceed(originalRequest)//直接放行，不添加token
        }
        
        val token = UserManager.token//从UserManager获取token
        val requestWithAuth: Request = if (token.isNotEmpty()){//构建请求判断是否有token
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")//添加认证头
                .build()
        }else{
            originalRequest//没有就不加
        }
    //发送请求给下一个拦截器
        return chain.proceed(requestWithAuth)
    }
}