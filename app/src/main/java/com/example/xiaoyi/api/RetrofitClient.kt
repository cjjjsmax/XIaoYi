package com.example.xiaoyi.api

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.example.xiaoyi.api.MessageApi
import com.example.xiaoyi.api.ConversationApi
import com.example.xiaoyi.api.AddressApi
import com.example.xiaoyi.api.interceptor.AuthInterceptor
import com.example.xiaoyi.api.interceptor.RetryInterceptor
import com.example.xiaoyi.api.interceptor.TokenExpiredInterceptor
import com.example.xiaoyi.config.ServerConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object RetrofitClient {
    //基础配置
    private const val BASE_URL = ServerConfig.BASE_URL
    //创建Context实例
    private lateinit var applicationContext: Context
    //认证拦截器
    private val authInterceptor = AuthInterceptor()
    fun init(context: Context){
        applicationContext = context.applicationContext
    }
    //创建lenient模式的Gson实例
    private val lenientGson = GsonBuilder()
        .setLenient()
        .create()

    //创建日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    //Token过期拦截器
    private val tokenExpiredInterceptor by lazy {
        TokenExpiredInterceptor(applicationContext)
    }

    //创建带超时配置的OkHttpClient
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(
                RetryInterceptor(
                    maxRetries = 3,
                    retryDelayMs = 1000
                )
            )
            .addInterceptor(tokenExpiredInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }
    //创建接口对象
    val productApi: ProductApi by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()
        retrofit.create(ProductApi::class.java)
    }

    val userApi: UserApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()

        retrofit.create(UserApi::class.java)
    }

    val messageApi: MessageApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()

        retrofit.create(MessageApi::class.java)
    }

    val conversationApi: ConversationApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()

        retrofit.create(ConversationApi::class.java)
    }

    val addressApi: AddressApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()

        retrofit.create(AddressApi::class.java)
    }

    val qualityInspectionApi: QualityInspectionApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()

        retrofit.create(QualityInspectionApi::class.java)
    }
}