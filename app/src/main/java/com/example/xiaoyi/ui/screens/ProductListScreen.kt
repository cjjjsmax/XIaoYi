package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.ui.components.ProductCard
import com.example.xiaoyi.ui.components.SearchBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProductListScreen(
    navController: NavController,
    paddingValues: PaddingValues
) {
    //搜索关键词状态
    var searchQueue by remember { mutableStateOf("") }
    //选中的分类索引
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    //商品求购切换索引
    var selectedListIndex by remember { mutableStateOf(0) }
    //求购列表数据
    var purchaseRequests by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    //商品列表数据
    var products by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    //加载状态
    var isLoading by remember { mutableStateOf(true) }
    //分类列表
    val categories = listOf("全部", "数码产品", "服装鞋包", "书籍文具", "运动户外", "其他")
    // 商品/求购切换标签
    val listTabs = listOf("商品列表", "求购列表")
    //错误消息状态
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 加载商品数据
    fun loadProducts() {
        isLoading = true
        errorMessage = null
        val call = RetrofitClient.productApi.getProducts()
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    products = response.body() ?: emptyList()
                    // 添加日志查看数据
                    println("Products loaded: ${products.size}")
                } else {
                    errorMessage = "加载失败: ${response.code()}"
                    println("Response error: ${response.code()}, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
                println("Network error: ${t.message}")
            }
        })
    }

    //加载求购数据
    fun loadPurchaseRequests() {
        isLoading = true
        errorMessage = null
        val call = RetrofitClient.productApi.getPurchaseRequests()
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    purchaseRequests = response.body() ?: emptyList()
                    // 添加日志查看数据
                    println("Purchase requests loaded: ${purchaseRequests.size}")
                } else {
                    errorMessage = "加载失败: ${response.code()}"
                    println("Response error: ${response.code()}, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
                println("Network error: ${t.message}")
            }
        })
    }

    //搜索商品（调用后端接口）
    fun searchProducts(keyword: String) {
        isLoading = true
        errorMessage = null
        val call = RetrofitClient.productApi.searchProducts(keyword)
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    products = response.body() ?: emptyList()
                    println("Search products loaded: ${products.size}")
                } else {
                    errorMessage = "搜索失败: ${response.code()}"
                    println("Search response error: ${response.code()}, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
                println("Search network error: ${t.message}")
            }
        })
    }

    LaunchedEffect(Unit) {
        loadProducts()
        loadPurchaseRequests()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        SearchBar(onSearch = { keyword ->
            searchQueue = keyword
            if (keyword.isNotEmpty()) {
                searchProducts(keyword)
            } else {
                loadProducts()
            }
        })
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size){index ->
                val category = categories[index]
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selectedCategoryIndex == index) {
                                Color(0xFF2196F3)
                            } else {
                                Color(0xFFE0E0E0)
                            }
                        )
                        .clickable { selectedCategoryIndex = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = category,
                        color = if (selectedCategoryIndex == index) {
                            Color.White
                        } else {
                            Color.Black
                        },
                        fontSize = 14.sp
                    )
                }
            }
        }
        TabRow(
            selectedTabIndex = selectedListIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            listTabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedListIndex == index,
                    onClick = { selectedListIndex = index },
                    text = { Text(tab) }
                )
            }
        }

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "未知错误",
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                if (selectedListIndex == 0) {
                    val filteredProducts = products.filter { product ->
                        val productData =
                            product["product"] as? Map<*, *> ?: return@filter false
                        val title = productData["title"] as? String ?: ""
                        val categoryId = productData["categoryId"] as? Number ?: 0
                        val matchesSearch = searchQueue.isEmpty() || title.contains(
                            searchQueue,
                            ignoreCase = true
                        )
                        val matchesCategory =
                            selectedCategoryIndex == 0 || categoryId.toInt() == selectedCategoryIndex
                        matchesSearch && matchesCategory
                    }
                    if (filteredProducts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "没有找到匹配的商品",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredProducts) { product ->
                                val productData = product["product"] as? Map<*, *> ?: return@items
                                val title = productData["title"] as? String ?: ""
                                val price = productData["price"] as? Double ?: 0.0
                                val seller = product["seller"] as? Map<*, *>
                                val sellerName = seller?.get("username") as? String ?: "未知卖家"

                                val productId = (productData["id"] as? Number)?.toLong() ?: 0L
                                // 获取商品图片URL，后端字段是images（后端已自动添加完整URL）
                                val imageUrl = productData["images"] as? String ?: ""
                                ProductCard(
                                    productName = title,
                                    price = price.toString(),
                                    location = "卖家: $sellerName",
                                    imageUrl = imageUrl,
                                    onClick = {
                                        navController.navigate(Screen.ProductDetail.route.replace("{productId}", productId.toString()))
                                    }
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        // 根据分类和搜索关键词过滤求购
                        val filteredRequests = purchaseRequests.filter { request ->
                            val title = request["title"] as? String ?: ""
                        val categoryId = request["categoryId"] as? Number ?: 0

                        // 检查搜索关键词
                        val matchesSearch = searchQueue.isEmpty() || title.contains(
                            searchQueue,
                            ignoreCase = true
                        )

                        // 检查分类
                        val matchesCategory =
                            selectedCategoryIndex == 0 || categoryId.toInt() == selectedCategoryIndex

                            matchesSearch && matchesCategory
                        }

                        // 显示过滤后的求购
                        if (filteredRequests.isEmpty()) {
                            item {
                                Text(
                                    text = "没有找到匹配的求购",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            items(filteredRequests) { request ->
                                val title = request["title"] as? String ?: ""
                                val maxPrice = request["maxPrice"] as? Double ?: 0.0
                                val description = request["description"] as? String ?: ""

                                val requestId = (request["id"] as? Number)?.toLong() ?: 0L
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            navController.navigate(Screen.PurchaseRequestDetail.route.replace("{purchaseRequestId}", requestId.toString()))
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "发布者: ${request["buyerName"] as? String ?: "未知用户"}",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = title,
                                            fontSize = 16.sp,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                        Text(
                                            text = "最高价格: ￥$maxPrice",
                                            fontSize = 18.sp,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                            color = Color(0xFFF44336),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        Text(
                                            text = description,
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}