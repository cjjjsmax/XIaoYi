package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import com.example.xiaoyi.ui.components.OrderCard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MyOrdersScreen(
    navController: NavController,
    userId: Long,
    paddingValues: PaddingValues
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var orders by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    fun loadOrders() {
        isLoading = true
        errorMessage = null
        
        // 获取用户作为买家的订单
        val buyerCall = RetrofitClient.productApi.getOrdersByBuyer(userId)
        buyerCall.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val buyerOrders = response.body() ?: emptyList()
                    
                    // 获取用户作为卖家的订单
                    val sellerCall = RetrofitClient.productApi.getOrdersBySeller(userId)
                    sellerCall.enqueue(object : Callback<List<Map<String, Any>>> {
                        override fun onResponse(
                            call: Call<List<Map<String, Any>>>,
                            response: Response<List<Map<String, Any>>>
                        ) {
                            if (response.isSuccessful) {
                                val sellerOrders = response.body() ?: emptyList()
                                // 合并并排序订单
                                orders = (buyerOrders + sellerOrders)
                                    .sortedByDescending { (it["createdAt"] as? String) ?: "" }
                            } else {
                                orders = buyerOrders
                            }
                            isLoading = false
                        }

                        override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                            orders = buyerOrders
                            errorMessage = "加载卖家订单失败: ${t.message}"
                            isLoading = false
                        }
                    })
                } else {
                    errorMessage = "加载买家订单失败: ${response.code()}"
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                errorMessage = "网络错误: ${t.message}"
                isLoading = false
            }
        })
    }

    LaunchedEffect(userId) {
        loadOrders()
    }

    DetailScreenTemplate(
        navController = navController,
        title = "我的订单",
        isLoading = isLoading,
        errorMessage = errorMessage
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (orders.isEmpty() && !isLoading) {
                androidx.compose.material3.Text(
                    text = "暂无订单",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(orders) { order ->
                        OrderCard(
                            order = order,
                            onItemClick = {
                                val productId = (order["productId"] as? Number)?.toLong() ?: 0L
                                navController.navigate("product_detail/$productId")
                            }
                        )
                    }
                }
            }
        }
    }
}