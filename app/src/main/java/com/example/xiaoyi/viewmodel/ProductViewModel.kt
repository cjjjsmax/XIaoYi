package com.example.xiaoyi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.xiaoyi.api.PublishProductRequest
import com.example.xiaoyi.api.PublishWantedRequest
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.data.database.entity.ProductEntity
import com.example.xiaoyi.data.database.entity.WantedEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.emptyList

class ProductViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products

    private val _isProductsLoading = MutableStateFlow(false)
    val isProductsLoading: StateFlow<Boolean> = _isProductsLoading

    private val _productsErrorMessage = MutableStateFlow<String?>(null)
    val productsErrorMessage: StateFlow<String?> = _productsErrorMessage

    private val _wantedList = MutableStateFlow<List<WantedEntity>>(emptyList())
    val wantedList: StateFlow<List<WantedEntity>> = _wantedList

    private val _isWantedLoading = MutableStateFlow(false)
    val isWantedLoading: StateFlow<Boolean> = _isWantedLoading

    private val _wantedErrorMessage = MutableStateFlow<String?>(null)
    val wantedErrorMessage: StateFlow<String?> = _wantedErrorMessage

    private val _userProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val userProducts: StateFlow<List<ProductEntity>> = _userProducts

    private val _isUserProductsLoading = MutableStateFlow(false)
    val isUserProductsLoading: StateFlow<Boolean> = _isUserProductsLoading

    private val _userProductsErrorMessage = MutableStateFlow<String?>(null)
    val userProductsErrorMessage: StateFlow<String?> = _userProductsErrorMessage

    private val _userWantedList = MutableStateFlow<List<WantedEntity>>(emptyList())
    val userWantedList: StateFlow<List<WantedEntity>> = _userWantedList

    private val _isUserWantedLoading = MutableStateFlow(false)
    val isUserWantedLoading: StateFlow<Boolean> = _isUserWantedLoading

    private val _userWantedErrorMessage = MutableStateFlow<String?>(null)
    val userWantedErrorMessage: StateFlow<String?> = _userWantedErrorMessage

    private val _isPublishProductLoading = MutableStateFlow(false)
    val isPublishProductLoading: StateFlow<Boolean> = _isPublishProductLoading

    private val _publishProductSuccess = MutableStateFlow(false)
    val publishProductSuccess: StateFlow<Boolean> = _publishProductSuccess

    private val _publishProductErrorMessage = MutableStateFlow<String?>(null)
    val publishProductErrorMessage: StateFlow<String?> = _publishProductErrorMessage

    private val _isPublishWantedLoading = MutableStateFlow(false)
    val isPublishWantedLoading: StateFlow<Boolean> = _isPublishWantedLoading

    private val _publishWantedSuccess = MutableStateFlow(false)
    val publishWantedSuccess: StateFlow<Boolean> = _publishWantedSuccess

    private val _publishWantedErrorMessage = MutableStateFlow<String?>(null)
    val publishWantedErrorMessage: StateFlow<String?> = _publishWantedErrorMessage

    fun loadProducts() {
        _isProductsLoading.value = true
        _productsErrorMessage.value = null

        val call = RetrofitClient.productApi.getProducts()
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onFailure(call: Call<List<Map<String, Any>>?>, t: Throwable) {
                _isProductsLoading.value = false
                _productsErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val apiProducts = response.body() ?: emptyList()
                    val productList = apiProducts.mapNotNull { productMap ->
                        val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                        val seller = productMap["seller"] as? Map<String, Any>

                        ProductEntity(
                            id = (productData["id"] as? Number)?.toLong() ?: 0L,
                            name = productData["title"] as? String ?: "",
                            price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                            description = productData["description"] as? String ?: "",
                            location = "",
                            imageUrl = productData["images"] as? String ?: "",
                            userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                            createdAt = "",
                            sellerName = seller?.get("username") as? String ?: "未知卖家",
                            categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                        )
                    }

                    _products.value = productList
                    _isProductsLoading.value = false
                } else {
                    _isProductsLoading.value = false
                    _productsErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    fun loadProductsByCategory(categoryId: Int) {
        _isProductsLoading.value = true
        _productsErrorMessage.value = null

        val call = RetrofitClient.productApi.getProductsByCategory(categoryId)
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onFailure(call: Call<List<Map<String, Any>>?>, t: Throwable) {
                _isProductsLoading.value = false
                _productsErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val apiProducts = response.body() ?: emptyList()
                    val productList = apiProducts.mapNotNull { productMap ->
                        val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                        val seller = productMap["seller"] as? Map<String, Any>

                        ProductEntity(
                            id = (productData["id"] as? Number)?.toLong() ?: 0L,
                            name = productData["title"] as? String ?: "",
                            price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                            description = productData["description"] as? String ?: "",
                            location = "",
                            imageUrl = productData["images"] as? String ?: "",
                            userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                            createdAt = "",
                            sellerName = seller?.get("username") as? String ?: "未知卖家",
                            categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                        )
                    }

                    _products.value = productList
                    _isProductsLoading.value = false
                } else {
                    _isProductsLoading.value = false
                    _productsErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    fun searchProducts(keyword: String, categoryId: Int = 0) {
        _isProductsLoading.value = true
        _productsErrorMessage.value = null

        val call = RetrofitClient.productApi.searchProducts(keyword, categoryId)
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onFailure(call: Call<List<Map<String, Any>>?>, t: Throwable) {
                _isProductsLoading.value = false
                _productsErrorMessage.value = "搜索失败: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val apiProducts = response.body() ?: emptyList()

                    val searchResults = apiProducts.mapNotNull { productMap ->
                        val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                        val seller = productMap["seller"] as? Map<String, Any>

                        ProductEntity(
                            id = (productData["id"] as? Number)?.toLong() ?: 0L,
                            name = productData["title"] as? String ?: "",
                            price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                            description = productData["description"] as? String ?: "",
                            location = "",
                            imageUrl = productData["images"] as? String ?: "",
                            userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                            createdAt = "",
                            sellerName = seller?.get("username") as? String ?: "未知卖家",
                            categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                        )
                    }

                    _products.value = searchResults
                    _isProductsLoading.value = false
                } else {
                    _isProductsLoading.value = false
                    _productsErrorMessage.value = "搜索失败: ${response.code()}"
                }
            }
        })
    }

    fun loadPurchaseRequests(keyword: String = "", categoryId: Int = 0) {
        _isWantedLoading.value = true
        _wantedErrorMessage.value = null

        val call = RetrofitClient.productApi.getPurchaseRequests(keyword, categoryId)

        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onFailure(call: Call<List<Map<String, Any>>?>, t: Throwable) {
                _isWantedLoading.value = false
                _wantedErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val apiWantedList = response.body() ?: emptyList()
                    val wantedEntities = apiWantedList.mapNotNull { wantedMap ->
                        WantedEntity(
                            id = (wantedMap["id"] as? Number)?.toLong() ?: 0L,
                            title = wantedMap["title"] as? String ?: "",
                            description = wantedMap["description"] as? String ?: "",
                            categoryId = (wantedMap["categoryId"] as? Number)?.toLong() ?: 0L,
                            maxPrice = (wantedMap["maxPrice"] as? Number)?.toDouble() ?: 0.0,
                            status = (wantedMap["status"] as? Number)?.toInt() ?: 0,
                            viewCount = (wantedMap["viewCount"] as? Number)?.toInt() ?: 0,
                            buyerId = (wantedMap["buyerId"] as? Number)?.toLong() ?: 0L,
                            buyerName = wantedMap["buyerName"] as? String ?: "未知买家",
                            createdAt = wantedMap["createdAt"]?.toString() ?: "",
                            updatedAt = ""
                        )
                    }

                    _wantedList.value = wantedEntities
                    _isWantedLoading.value = false
                } else {
                    _isWantedLoading.value = false
                    _wantedErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    fun loadUserProducts(userId: Long) {
        _isUserProductsLoading.value = true
        _userProductsErrorMessage.value = null

        val call = RetrofitClient.productApi.getUserProducts(userId)
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onFailure(call: Call<List<Map<String, Any>>?>, t: Throwable) {
                _isUserProductsLoading.value = false
                _userProductsErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val apiProducts = response.body() ?: emptyList()
                    val productList = apiProducts.mapNotNull { productMap ->
                        val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                        val seller = productMap["seller"] as? Map<String, Any>

                        ProductEntity(
                            id = (productData["id"] as? Number)?.toLong() ?: 0L,
                            name = productData["title"] as? String ?: "",
                            price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                            description = productData["description"] as? String ?: "",
                            location = "",
                            imageUrl = productData["images"] as? String ?: "",
                            userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                            createdAt = "",
                            sellerName = seller?.get("username") as? String ?: "未知卖家",
                            categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                        )
                    }

                    _userProducts.value = productList
                    _isUserProductsLoading.value = false
                } else {
                    _isUserProductsLoading.value = false
                    _userProductsErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    fun loadUserPurchaseRequests(userId: Long) {
        _isUserWantedLoading.value = true
        _userWantedErrorMessage.value = null

        val call = RetrofitClient.productApi.getUserPurchaseRequests(userId)
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onFailure(call: Call<List<Map<String, Any>>?>, t: Throwable) {
                _isUserWantedLoading.value = false
                _userWantedErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val apiWantedList = response.body() ?: emptyList()
                    val wantedEntities = apiWantedList.mapNotNull { wantedMap ->
                        WantedEntity(
                            id = (wantedMap["id"] as? Number)?.toLong() ?: 0L,
                            title = wantedMap["title"] as? String ?: "",
                            description = wantedMap["description"] as? String ?: "",
                            categoryId = (wantedMap["categoryId"] as? Number)?.toLong() ?: 0L,
                            maxPrice = (wantedMap["maxPrice"] as? Number)?.toDouble() ?: 0.0,
                            status = (wantedMap["status"] as? Number)?.toInt() ?: 0,
                            viewCount = (wantedMap["viewCount"] as? Number)?.toInt() ?: 0,
                            buyerId = (wantedMap["buyerId"] as? Number)?.toLong() ?: 0L,
                            buyerName = wantedMap["buyerName"] as? String ?: "未知买家",
                            createdAt = wantedMap["createdAt"]?.toString() ?: "",
                            updatedAt = ""
                        )
                    }

                    _userWantedList.value = wantedEntities
                    _isUserWantedLoading.value = false
                } else {
                    _isUserWantedLoading.value = false
                    _userWantedErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    fun publishProduct(
        title: String,
        price: Double,
        description: String,
        categoryId: Int,
        sellerId: Long,
        images: String
    ) {
        _isPublishProductLoading.value = true
        _publishProductErrorMessage.value = null
        _publishProductSuccess.value = false

        val request = PublishProductRequest(
            title = title,
            price = price,
            description = description,
            categoryId = categoryId,
            sellerId = sellerId,
            status = 1,
            images = images,
            viewCount = 0
        )

        val call = RetrofitClient.productApi.publishProduct(request)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onFailure(call: Call<Map<String, Any>?>, t: Throwable) {
                _isPublishProductLoading.value = false
                _publishProductErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                _isPublishProductLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val success = responseBody?.get("success") as? Boolean ?: false
                    if (success) {
                        _publishProductSuccess.value = true
                    } else {
                        _publishProductErrorMessage.value = responseBody?.get("message") as? String ?: "发布失败"
                    }
                } else {
                    _publishProductErrorMessage.value = "发布失败: ${response.code()}"
                }
            }
        })
    }

    fun publishWanted(
        title: String,
        maxPrice: Double,
        description: String,
        categoryId: Int,
        buyerId: Long
    ) {
        _isPublishWantedLoading.value = true
        _publishWantedErrorMessage.value = null
        _publishWantedSuccess.value = false

        val request = PublishWantedRequest(
            title = title,
            maxPrice = maxPrice,
            description = description,
            categoryId = categoryId,
            buyerId = buyerId,
            status = 1,
            viewCount = 0
        )

        val call = RetrofitClient.productApi.publishWanted(request)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onFailure(call: Call<Map<String, Any>?>, t: Throwable) {
                _isPublishWantedLoading.value = false
                _publishWantedErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                _isPublishWantedLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val success = responseBody["success"] as? Boolean ?: false
                        if (success) {
                            _publishWantedSuccess.value = true
                        } else {
                            _publishWantedErrorMessage.value = responseBody["message"] as? String ?: "发布失败"
                        }
                    }
                } else {
                    _publishWantedErrorMessage.value = "发布失败: ${response.code()}"
                }
            }
        })
    }

    fun deleteProduct(productId: Long) {
        val call = RetrofitClient.productApi.deleteProduct(productId)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onFailure(call: Call<Map<String, Any>?>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
            }
        })
    }

    fun deletePurchaseRequest(requestId: Long) {
        val call = RetrofitClient.productApi.deletePurchaseRequest(requestId)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onFailure(call: Call<Map<String, Any>?>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
            }
        })
    }

    fun resetPublishState() {
        _publishProductSuccess.value = false
        _publishProductErrorMessage.value = null
        _publishWantedSuccess.value = false
        _publishWantedErrorMessage.value = null
    }

    fun setPublishProductError(message: String) {
        _publishProductErrorMessage.value = message
    }

    fun setPublishWantedError(message: String) {
        _publishWantedErrorMessage.value = message
    }

    fun resetPublishProductState() {
        _publishProductSuccess.value = false
        _publishProductErrorMessage.value = null
        _isPublishProductLoading.value = false
    }

    fun resetPublishWantedState() {
        _publishWantedSuccess.value = false
        _publishWantedErrorMessage.value = null
        _isPublishWantedLoading.value = false
    }
}