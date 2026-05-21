package com.example.xiaoyi.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.example.xiaoyi.api.MessageApi
import com.example.xiaoyi.api.ConversationApi
import com.example.xiaoyi.api.AddressApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080"

    // 创建 lenient 模式的 Gson 实例
    private val lenientGson = GsonBuilder()
        .setLenient()
        .create()

    // 创建日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    // 创建带超时配置的 OkHttpClient（AI质检需要较长时间，设置为300秒）
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

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