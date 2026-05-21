package com.example.xiaoyi.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import com.example.xiaoyi.ui.components.ProductCard
import com.example.xiaoyi.ui.components.WantedCard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MyWantedScreen(
    navController: NavController,
    userId: Long,
    paddingValues: PaddingValues
){
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedProductId by remember { mutableStateOf<Long>(0L) }

    fun loadUserProducts(userId: Long){
        println("=== 开始加载用户求购 ===")
        println("用户ID: $userId")

        isLoading = true
        errorMessage = null

        val call = RetrofitClient.productApi.getUserPurchaseRequests(userId)

        call.enqueue(object : Callback<List<Map<String, Any>>>{
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                isLoading = false
                println("=== API响应 ===")
                println("是否成功: ${response.isSuccessful}")
                println("响应码: ${response.code()}")

                if (response.isSuccessful){
                    val data = response.body()
                    println("返回数据数量: ${data?.size ?: 0}")
                    println("返回数据: $data")
                    products = data ?: emptyList()
                }else{
                    errorMessage = "请求失败: ${response.code()}"
                    println("请求失败: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络波动: ${t.message}"
                println("=== 网络错误 ===")
                println("错误信息: ${t.message}")
            }
        })
    }

    fun deletePurchaseRequest(requestId: Long) {
        if (requestId == 0L) return
        val call = RetrofitClient.productApi.deletePurchaseRequest(requestId)

        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    loadUserProducts(userId)
                } else {
                    errorMessage = "删除失败: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                errorMessage = "删除失败: ${t.message}"
            }
        })
    }

    @Composable
    fun ProductCardWithActions(
        product: Map<String, Any>,
        onEdit: () -> Unit,
        onDelete: () -> Unit
    ){
        val wantedName = product["title"] as? String ?: ""
        val maxPrice = product["maxPrice"]?.toString() ?: "0"
        val description = product["description"] as? String ?: ""
        Column {
            WantedCard(
                title = wantedName,
                maxPrice = maxPrice,
                description = description,
                onClick = {

                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onEdit) {
                    Text("编辑")
                }
                TextButton(onClick = onDelete) {
                    Text(
                        text = "删除",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    LaunchedEffect(userId) {
        loadUserProducts(userId)
    }

    DetailScreenTemplate(
        navController = navController,
        title = "我的求购",
        isLoading = isLoading,
        errorMessage = errorMessage
    ) {
        if (products.isEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "暂无发布求购",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }else{
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products.size){ index ->
                    val product = products[index]
                    ProductCardWithActions(
                        product = product,
                        onEdit = {
                            val requestId = (product["id"] as? Number)?.toLong() ?: 0L
                            navController.navigate(Screen.EditWanted.route.replace("{requestId}", requestId.toString()))
                        },
                        onDelete = {
                            selectedProductId = (product["id"] as? Number)?.toLong() ?: 0L
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog){
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("确认删除")
            },
            text = {
                Text("确定要删除这个商品吗？删除后无法恢复。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletePurchaseRequest(selectedProductId)
                        showDeleteDialog = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}