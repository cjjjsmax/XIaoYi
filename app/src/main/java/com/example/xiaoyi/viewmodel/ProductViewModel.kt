package com.example.xiaoyi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.xiaoyi.api.PublishProductRequest
import com.example.xiaoyi.api.PublishWantedRequest
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.Product
import com.example.xiaoyi.model.Result
import com.example.xiaoyi.model.Wanted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.emptyList

class ProductViewModel : ViewModel() {
    //商品列表状态
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products
    private val _isProductsLoading = MutableStateFlow(false)
    val isProductsLoading: StateFlow<Boolean> = _isProductsLoading
    private val _productsErrorMessage = MutableStateFlow<String?>(null)
    val productsErrorMessage: StateFlow<String?> = _productsErrorMessage

    //求购列表状态
    private val _wantedList = MutableStateFlow<List<Wanted>>(emptyList())
    val wantedList: StateFlow<List<Wanted>> = _wantedList
    private val _isWantedLoading = MutableStateFlow(false)
    val isWantedLoading: StateFlow<Boolean> = _isWantedLoading
    private val _wantedErrorMessage = MutableStateFlow<String?>(null)
    val wantedErrorMessage: StateFlow<String?> = _wantedErrorMessage

    //用户商品列表状态
    private val _userProducts = MutableStateFlow<List<Product>>(emptyList())
    val userProducts: StateFlow<List<Product>> = _userProducts
    private val _isUserProductsLoading = MutableStateFlow(false)
    val isUserProductsLoading: StateFlow<Boolean> = _isUserProductsLoading
    private val _userProductsErrorMessage = MutableStateFlow<String?>(null)
    val userProductsErrorMessage: StateFlow<String?> = _userProductsErrorMessage

    //求购列表状态
    private val _userWantedList = MutableStateFlow<List<Wanted>>(emptyList())
    val userWantedList: StateFlow<List<Wanted>> = _userWantedList
    private val _isUserWantedLoading = MutableStateFlow(false)
    val isUserWantedLoading: StateFlow<Boolean> = _isUserWantedLoading
    private val _userWantedErrorMessage = MutableStateFlow<String?>(null)
    val userWantedErrorMessage: StateFlow<String?> = _userWantedErrorMessage

    //发布状态
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

    //分页状态
    private var currentPage = 1
    private val pageSize = 20
    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    //加载商品列表
    fun loadProducts(isRefresh: Boolean = false) {
        if (isRefresh){
            currentPage = 1
            _hasMoreData.value = true
            _isProductsLoading.value = true
        }else{
            _isLoadingMore.value = true
        }
        _productsErrorMessage.value = null

        //构建网络请求
        val call = RetrofitClient.productApi.getProducts(
            page = currentPage,
            size = pageSize
        )
        call.enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                if (isRefresh){
                    _isProductsLoading.value = false
                }else{
                    _isLoadingMore.value = false
                }
                _productsErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<List<Map<String, Any>>>>,
                response: Response<Result<List<Map<String, Any>>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        //请求成功，解析数据
                        val apiProducts = result.data ?: emptyList()
                        //将Map转换为Product对象
                        val productList = apiProducts.mapNotNull { productMap ->
                            val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                            val seller = productMap["seller"] as? Map<String, Any>

                            Product(
                                id = (productData["id"] as? Number)?.toLong() ?: 0L,
                                name = productData["title"] as? String ?: "",
                                price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                                description = productData["description"] as? String ?: "",
                                location = "",
                                imageUrl = productData["images"] as? String ?: "",
                                userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                                createdAt = productData["createdAt"]?.toString() ?: "",
                                sellerName = seller?.get("username") as? String ?: "未知卖家",
                                categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                            )
                        }
                        if (isRefresh){
                            _products.value = productList
                            _isProductsLoading.value = false
                        }else{
                            _products.value = _products.value + productList
                            _isLoadingMore.value = false
                        }
                        _hasMoreData.value = productList.size >= pageSize
                        if (productList.isNotEmpty()) {
                            currentPage++
                        }
                    } else {
                        if (isRefresh){
                            _isProductsLoading.value = false
                        }else{
                            _isLoadingMore.value = false
                        }
                        _productsErrorMessage.value = result?.message ?: "加载失败"
                    }
                } else {
                    if (isRefresh){
                        _isProductsLoading.value = false
                    }else{
                        _isLoadingMore.value = false
                    }

                    _productsErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }
    fun loadMoreProducts() {
        if (!_hasMoreData.value || _isLoadingMore.value) return
        loadProducts(isRefresh = false)
    }

    //按分类加载
    fun loadProductsByCategory(categoryId: Int) {
        _isProductsLoading.value = true
        _productsErrorMessage.value = null

        val call = RetrofitClient.productApi.getProductsByCategory(categoryId)
        call.enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                _isProductsLoading.value = false
                _productsErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<List<Map<String, Any>>>>,
                response: Response<Result<List<Map<String, Any>>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val apiProducts = result.data ?: emptyList()
                        val productList = apiProducts.mapNotNull { productMap ->
                            val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                            val seller = productMap["seller"] as? Map<String, Any>

                            Product(
                                id = (productData["id"] as? Number)?.toLong() ?: 0L,
                                name = productData["title"] as? String ?: "",
                                price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                                description = productData["description"] as? String ?: "",
                                location = "",
                                imageUrl = productData["images"] as? String ?: "",
                                userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                                createdAt = productData["createdAt"]?.toString() ?: "",
                                sellerName = seller?.get("username") as? String ?: "未知卖家",
                                categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                            )
                        }

                        _products.value = productList
                        _isProductsLoading.value = false
                    } else {
                        _isProductsLoading.value = false
                        _productsErrorMessage.value = result?.message ?: "加载失败"
                    }
                } else {
                    _isProductsLoading.value = false
                    _productsErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    //搜索商品
    fun searchProducts(keyword: String, categoryId: Int = 0,isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            _hasMoreData.value = true
        }
        _isProductsLoading.value = true
        _productsErrorMessage.value = null

        val call = RetrofitClient.productApi.searchProducts(keyword, categoryId)
        call.enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                _isProductsLoading.value = false
                _productsErrorMessage.value = "搜索失败: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<List<Map<String, Any>>>>,
                response: Response<Result<List<Map<String, Any>>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val apiProducts = result.data ?: emptyList()

                        val searchResults = apiProducts.mapNotNull { productMap ->
                            val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                            val seller = productMap["seller"] as? Map<String, Any>

                            Product(
                                id = (productData["id"] as? Number)?.toLong() ?: 0L,
                                name = productData["title"] as? String ?: "",
                                price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                                description = productData["description"] as? String ?: "",
                                location = "",
                                imageUrl = productData["images"] as? String ?: "",
                                userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                                createdAt = productData["createdAt"]?.toString() ?: "",
                                sellerName = seller?.get("username") as? String ?: "未知卖家",
                                categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                            )
                        }

                        _products.value = searchResults
                        _isProductsLoading.value = false
                    } else {
                        _isProductsLoading.value = false
                        _productsErrorMessage.value = result?.message ?: "搜索失败"
                    }
                } else {
                    _isProductsLoading.value = false
                    _productsErrorMessage.value = "搜索失败: ${response.code()}"
                }
            }
        })
    }

    //加载求购列表
    fun loadPurchaseRequests(keyword: String = "", categoryId: Int = 0) {
        _isWantedLoading.value = true
        _wantedErrorMessage.value = null

        val call = RetrofitClient.productApi.getPurchaseRequests(keyword, categoryId)

        call.enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                _isWantedLoading.value = false
                _wantedErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<List<Map<String, Any>>>>,
                response: Response<Result<List<Map<String, Any>>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val apiWantedList = result.data ?: emptyList()
                        val wantedEntities = apiWantedList.mapNotNull { wantedMap ->
                            Wanted(
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
                        _wantedErrorMessage.value = result?.message ?: "加载失败"
                    }
                } else {
                    _isWantedLoading.value = false
                    _wantedErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    //加载用户商品
    fun loadUserProducts(userId: Long) {
        _isUserProductsLoading.value = true
        _userProductsErrorMessage.value = null

        val call = RetrofitClient.productApi.getUserProducts(userId)
        call.enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                _isUserProductsLoading.value = false
                _userProductsErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<List<Map<String, Any>>>>,
                response: Response<Result<List<Map<String, Any>>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val apiProducts = result.data ?: emptyList()
                        val productList = apiProducts.mapNotNull { productMap ->
                            val productData = productMap["product"] as? Map<String, Any> ?: return@mapNotNull null
                            val seller = productMap["seller"] as? Map<String, Any>

                            Product(
                                id = (productData["id"] as? Number)?.toLong() ?: 0L,
                                name = productData["title"] as? String ?: "",
                                price = (productData["price"] as? Number)?.toDouble() ?: 0.0,
                                description = productData["description"] as? String ?: "",
                                location = "",
                                imageUrl = productData["images"] as? String ?: "",
                                userId = (productData["sellerId"] as? Number)?.toLong() ?: 0L,
                                createdAt = productData["createdAt"]?.toString() ?: "",
                                sellerName = seller?.get("username") as? String ?: "未知卖家",
                                categoryId = (productData["categoryId"] as? Number)?.toLong() ?: 0L
                            )
                        }

                        _userProducts.value = productList
                        _isUserProductsLoading.value = false
                    } else {
                        _isUserProductsLoading.value = false
                        _userProductsErrorMessage.value = result?.message ?: "加载失败"
                    }
                } else {
                    _isUserProductsLoading.value = false
                    _userProductsErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    //加载用户求购
    fun loadUserPurchaseRequests(userId: Long) {
        _isUserWantedLoading.value = true
        _userWantedErrorMessage.value = null

        val call = RetrofitClient.productApi.getUserPurchaseRequests(userId)
        call.enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                _isUserWantedLoading.value = false
                _userWantedErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<List<Map<String, Any>>>>,
                response: Response<Result<List<Map<String, Any>>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val apiWantedList = result.data ?: emptyList()
                        val wantedEntities = apiWantedList.mapNotNull { wantedMap ->
                            Wanted(
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
                        _userWantedErrorMessage.value = result?.message ?: "加载失败"
                    }
                } else {
                    _isUserWantedLoading.value = false
                    _userWantedErrorMessage.value = "加载失败: ${response.code()}"
                }
            }
        })
    }

    //发布商品
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

        //构建请求对象
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

        //发起请求
        val call = RetrofitClient.productApi.publishProduct(request)
        call.enqueue(object : Callback<Result<String>> {
            override fun onFailure(call: Call<Result<String>>, t: Throwable) {
                _isPublishProductLoading.value = false
                _publishProductErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<String>>,
                response: Response<Result<String>>
            ) {
                _isPublishProductLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        _publishProductSuccess.value = true
                    } else {
                        _publishProductErrorMessage.value = result?.message ?: "发布失败"
                    }
                } else {
                    _publishProductErrorMessage.value = "发布失败: ${response.code()}"
                }
            }
        })
    }

    //发布求购
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
        call.enqueue(object : Callback<Result<String>> {
            override fun onFailure(call: Call<Result<String>>, t: Throwable) {
                _isPublishWantedLoading.value = false
                _publishWantedErrorMessage.value = "网络错误: ${t.message}"
            }

            override fun onResponse(
                call: Call<Result<String>>,
                response: Response<Result<String>>
            ) {
                _isPublishWantedLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        _publishWantedSuccess.value = true
                    } else {
                        _publishWantedErrorMessage.value = result?.message ?: "发布失败"
                    }
                } else {
                    _publishWantedErrorMessage.value = "发布失败: ${response.code()}"
                }
            }
        })
    }

    //删除商品
    fun deleteProduct(productId: Long) {
        val call = RetrofitClient.productApi.deleteProduct(productId)
        call.enqueue(object : Callback<Result<String>> {
            override fun onFailure(call: Call<Result<String>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Result<String>>,
                response: Response<Result<String>>
            ) {
                _isUserProductsLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        //从列表中移除该商品
                        _userProducts.value = _userProducts.value.filter { it.id != productId }
                    } else {
                        _userProductsErrorMessage.value = result?.message ?: "删除失败"
                    }
                } else {
                    _userProductsErrorMessage.value = "删除失败: ${response.code()}"
                }
            }
        })
    }

    //删除求购
    fun deletePurchaseRequest(requestId: Long) {
        val call = RetrofitClient.productApi.deletePurchaseRequest(requestId)
        call.enqueue(object : Callback<Result<String>> {
            override fun onFailure(call: Call<Result<String>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<Result<String>>,
                response: Response<Result<String>>
            ) {
                _isUserWantedLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        //从用户求购列表中移除该求购
                        _userWantedList.value = _userWantedList.value.filter { it.id != requestId }
                    } else {
                        _userWantedErrorMessage.value = result?.message ?: "删除失败"
                    }
                } else {
                    _userWantedErrorMessage.value = "删除失败: ${response.code()}"
                }
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