package com.example.xiaoyi.api

import com.example.xiaoyi.model.Product
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class PublishProductRequest(
    val title: String,
    val price: Double,
    val description: String,
    val categoryId: Int,
    val sellerId: Long,
    val status: Int,
    val images: String,
    val viewCount: Int,
    val overallCondition: String? = null,
    val flaws: String? = null
)

data class PublishWantedRequest(
    val title: String,
    val maxPrice: Double,
    val description: String,
    val categoryId: Int,
    val buyerId: Long,
    val status: Int,
    val viewCount: Int
)

interface ProductApi{
    //上传商品图片
    @Multipart
    @POST("/api/products/upload-image")
    fun uploadImage(@Part file: MultipartBody.Part): Call<Map<String, Any>>
    
    //获取商品列表
    @GET("/api/products/list")
    fun getProducts(): Call<List<Map<String, Any>>>

    //根据分类获取商品列表
    @GET("/api/products/list")
    fun getProductsByCategory(@Query("categoryId") categoryId: Int): Call<List<Map<String, Any>>>

    //搜索商品（根据标题模糊匹配，支持分类筛选）
    @GET("/api/products/search")
    fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("categoryId") categoryId: Int = 0
    ): Call<List<Map<String, Any>>>
    
    //获取求购列表（支持关键词搜索和分类筛选）
    @GET("/api/purchase-requests/list")
    fun getPurchaseRequests(
        @Query("keyword") keyword: String = "",
        @Query("categoryId") categoryId: Int = 0
    ): Call<List<Map<String, Any>>>
    
    //发布商品
    @POST("/api/products/publish/json")
    fun publishProduct(@Body product: PublishProductRequest): Call<Map<String, Any>>
    
    //发布求购
    @POST("/api/purchase-requests/publish")
    fun publishWanted(@Body purchaseRequest: PublishWantedRequest): Call<Map<String, Any>>
    
    //获取用户发布的商品
    @GET("/api/products/user")
    fun getUserProducts(@Query("sellerId") sellerId: Long): Call<List<Map<String, Any>>>
    
    //删除商品
    @DELETE("/api/products/delete")
    fun deleteProduct(@Query("id") id: Long): Call<Map<String, Any>>

    @DELETE("/api/purchase-requests/delete")
    fun deletePurchaseRequest(@Query("id") id: Long): Call<Map<String, Any>>

    @PUT("/api/purchase-requests/{id}")
    fun updatePurchaseRequest(
        @Path("id") id: Long,
        @Query("title") title: String,
        @Query("maxPrice") maxPrice: Double,
        @Query("description") description: String
    ): Call<Map<String, Any>>

    @GET("/api/orders/buyer")
    fun getOrdersByBuyer(@Query("buyerId") buyerId: Long): Call<List<Map<String, Any>>>

    @GET("/api/orders/seller")
    fun getOrdersBySeller(@Query("sellerId") sellerId: Long): Call<List<Map<String, Any>>>
    
    //获取商品详情
    @GET("/api/products/detail")
    fun getProductById(@Query("id") id: Long): Call<Map<String, Any>>
    
    //获取求购详情
    @GET("/api/purchase-requests/detail")
    fun getPurchaseRequestById(@Query("id") id: Long): Call<Map<String, Any>>
    
    //更新商品
    @PUT("/api/products/{productId}")
    fun updateProduct(@Path("productId") productId: Long, @Body request: PublishProductRequest): Call<Map<String, Any>>
    
    //获取用户求购
    @GET("/api/purchase-requests/user")
    fun getUserPurchaseRequests(@Query("buyerId") buyerId: Long): Call<List<Map<String, Any>>>
}