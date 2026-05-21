package com.example.xiaoyi.ui.screens

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
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import com.example.xiaoyi.ui.components.ProductFormFields
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditWantedScreen(
    navController: NavController,
    requestId: Long,
    paddingValues: PaddingValues
) {
    var title by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(requestId) {
        loadWantedData(
            requestId = requestId,
            onSuccess = { data ->
                title = data["title"] as? String ?: ""
                maxPrice = data["maxPrice"]?.toString() ?: ""
                description = data["description"] as? String ?: ""
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
        title = "编辑求购",
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
                    errorMessage = ""
                },
                price = maxPrice,
                onPriceChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        maxPrice = it
                        errorMessage = ""
                    }
                },
                description = description,
                onDescriptionChange = {
                    description = it
                    errorMessage = ""
                },
                errorMessage = errorMessage,
                titleLabel = "求购标题",
                priceLabel = "最高预算",
                descriptionLabel = "求购描述"
            )

            Button(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "请输入求购标题"
                        maxPrice.isBlank() -> errorMessage = "请输入最高预算"
                        else -> {
                            val priceValue = maxPrice.toDoubleOrNull()
                            if (priceValue == null || priceValue <= 0) {
                                errorMessage = "请输入有效的预算金额"
                            } else {
                                updateWanted(
                                    requestId = requestId,
                                    title = title,
                                    maxPrice = priceValue,
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
                Text(text = "更新求购", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun loadWantedData(
    requestId: Long,
    onSuccess: (Map<String, Any>) -> Unit,
    onComplete: () -> Unit,
    onError: (String) -> Unit
) {
    val call = RetrofitClient.productApi.getPurchaseRequestById(requestId)
    call.enqueue(object : Callback<Map<String, Any>> {
        override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
            if (response.isSuccessful) {
                val data = response.body()
                val code = (data?.get("code") as? Number)?.toInt() ?: 0
                if (code == 200) {
                    val product = data?.get("data") as? Map<String, Any>
                    product?.let { onSuccess(it) }
                } else {
                    onError(data?.get("message") as? String ?: "加载失败")
                }
            } else {
                onError("加载失败: ${response.code()}")
            }
            onComplete()
        }

        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
            onError("网络错误: ${t.message}")
            onComplete()
        }
    })
}

private fun updateWanted(
    requestId: Long,
    title: String,
    maxPrice: Double,
    description: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val call = RetrofitClient.productApi.updatePurchaseRequest(
        id = requestId,
        title = title,
        maxPrice = maxPrice,
        description = description
    )
    call.enqueue(object : Callback<Map<String, Any>> {
        override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
            if (response.isSuccessful) {
                onSuccess()
            } else {
                onError("更新失败: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
            onError("网络错误: ${t.message}")
        }
    })
}