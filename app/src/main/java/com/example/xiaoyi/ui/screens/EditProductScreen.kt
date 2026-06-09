package com.example.xiaoyi.ui.screens

import com.example.xiaoyi.ui.components.DetailScreenTemplate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.PublishProductRequest
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.Result
import com.example.xiaoyi.ui.components.ProductFormFields
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditProductScreen(
    navController: NavController,
    productId: Long,
    paddingValues: PaddingValues
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(productId) {
        loadProductData(
            productId = productId,
            onSuccess = { data ->
                val product = data["product"] as? Map<String, Any>
                title = product?.get("title") as? String ?: ""
                price = product?.get("price")?.toString() ?: ""
                description = product?.get("description") as? String ?: ""
            },
            onComplete = {
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
            }
        )
    }

    DetailScreenTemplate(
        navController = navController,
        title = "编辑商品",
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (errorMessage.isNotEmpty()){
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
            ProductFormFields(
                title = title,
                onTitleChange = {
                    title = it
                },
                price = price,
                onPriceChange = {
                    price = it
                },
                description = description,
                onDescriptionChange = {
                    description = it
                },
                errorMessage = errorMessage
            )

            Button(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "请输入商品标题"
                        price.isBlank() -> errorMessage = "请输入商品价格"
                        else -> {
                            val priceValue = price.toDoubleOrNull()
                            if (priceValue == null || priceValue <= 0) {
                                errorMessage = "请输入有效的商品价格"
                            } else {
                                updateProduct(
                                    productId = productId,
                                    title = title,
                                    price = priceValue,
                                    description = description,
                                    onSuccess = {
                                        navController.popBackStack()
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "更新商品", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun loadProductData(
    productId: Long,
    onSuccess: (Map<String, Any>) -> Unit,
    onComplete: () -> Unit,
    onError: (String) -> Unit
) {
    val call = RetrofitClient.productApi.getProductById(productId)
    call.enqueue(object : Callback<Result<Map<String, Any>>> {
        override fun onResponse(call: Call<Result<Map<String, Any>>>, response: Response<Result<Map<String, Any>>>) {
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null && result.isSuccess()) {
                    val product = result.data
                    product?.let { onSuccess(it) }
                } else {
                    onError(result?.message ?: "加载失败")
                }
            } else {
                onError("加载失败: ${response.code()}")
            }
            onComplete()
        }

        override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) {
            onError("网络错误: ${t.message}")
            onComplete()
        }
    })
}

private fun updateProduct(
    productId: Long,
    title: String,
    price: Double,
    description: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val request = PublishProductRequest(
        title = title,
        price = price,
        description = description,
        categoryId = 0,
        sellerId = 0,
        status = 1,
        images = "",
        viewCount = 0
    )

    val call = RetrofitClient.productApi.updateProduct(productId, request)
    call.enqueue(object : Callback<Result<String>> {
        override fun onResponse(call: Call<Result<String>>, response: Response<Result<String>>) {
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null && result.isSuccess()) {
                    onSuccess()
                } else {
                    onError(result?.message ?: "更新失败")
                }
            } else {
                onError("更新失败: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Result<String>>, t: Throwable) {
            onError("网络错误: ${t.message}")
        }
    })
}