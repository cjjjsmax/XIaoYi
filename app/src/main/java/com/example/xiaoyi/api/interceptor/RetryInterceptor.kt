package com.example.xiaoyi.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

//okHttp请求重试拦截器
class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val retryDelayMs: Long = 1000) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var lastException: Exception? = null

        for (attempt in 0..maxRetries){
            try {
                if (attempt > 0){
                    Thread.sleep(retryDelayMs * attempt)
                    println("RetryInterceptor: 第 ${attempt + 1} 次重试请求: ${request.url}")
                }
                response = chain.proceed(request)

                if (response.isSuccessful){
                    println("RetryInterceptor: 请求成功: ${request.url}")
                    return response
                }

                if (response.code in 500..599){
                    println("RetryInterceptor: 服务器错误 ${response.code}，准备重试")
                    response.close()
                    continue
                }
                println("RetryInterceptor: 客户端错误 ${response.code}，不重试")
                return response
            }catch (e : IOException){
                lastException = e
                println("RetryInterceptor: 网络异常: ${e.message}，准备重试")
            }
        }
        println("RetryInterceptor: 重试次数用完，请求失败: ${request.url}")
        if (response != null) {
            return response
        }
        throw lastException ?: IOException("Unknown error")
    }
}