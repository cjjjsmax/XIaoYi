package com.example.xiaoyi.ui.screens

import android.R.attr.x
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.xiaoyi.R
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.config.ServerConfig
import com.example.xiaoyi.model.CreateConversationRequest
import com.example.xiaoyi.model.Result
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.utils.TimeUtils
import com.example.xiaoyi.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class DetailType {
    PRODUCT,
    PURCHASE_REQUEST
}

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: Long,
    paddingValues: PaddingValues,
    detailType: DetailType = DetailType.PRODUCT
) {
    var product by remember { mutableStateOf<Map<String, Any>?>(null) }
    var seller by remember { mutableStateOf<Map<String, Any>?>(null) }
    var flaws by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        when (detailType) {
            DetailType.PRODUCT -> {
                loadProductDetail(productId) { result, sellerData, flawsData, error ->
                    if (error != null) {
                        errorMessage = error
                        isLoading = false
                    } else {
                        product = result
                        seller = sellerData
                        flaws = flawsData
                        isLoading = false
                    }
                }
            }

            DetailType.PURCHASE_REQUEST -> {
                loadPurchaseRequestDetail(productId) { result, error ->
                    if (error != null) {
                        errorMessage = error
                        isLoading = false
                    } else {
                        product = result
                        isLoading = false
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"

                )
            }
        }
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "加载中...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            errorMessage != null -> {
                // 错误状态显示
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "加载失败",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = {
                            isLoading = true
                            when (detailType) {
                                DetailType.PRODUCT -> {
                                    loadProductDetail(productId) { result, sellerData, flawsData, error ->
                                        if (error != null) {
                                            errorMessage = error
                                            isLoading = false
                                        } else {
                                            product = result
                                            seller = sellerData
                                            flaws = flawsData
                                            isLoading = false
                                        }
                                    }
                                }

                                DetailType.PURCHASE_REQUEST -> {
                                    loadPurchaseRequestDetail(productId) { result, error ->
                                        if (error != null) {
                                            errorMessage = error
                                            isLoading = false
                                        } else {
                                            product = result
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        }) {
                            Text("重试")
                        }
                    }
                }
            }

            product != null -> {
                val productData: Map<String, Any> = product!!
                val isPurchaseRequest = detailType == DetailType.PURCHASE_REQUEST
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //商品内容区域
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (!isPurchaseRequest) {
                            item {
                                //获取商品图片URL并转换为完整URL
                                val imageUrl = productData["images"] as? String ?: ""
                                val fullImageUrl = ServerConfig.getFullImageUrl(imageUrl)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    coil.compose.AsyncImage(
                                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                            .data(if (fullImageUrl.isNotEmpty()) fullImageUrl else R.drawable.ic_launcher_foreground)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = productData["title"] as? String ?: "商品图片",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        error = painterResource(R.drawable.ic_launcher_foreground)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = productData["title"] as? String
                                        ?: if (isPurchaseRequest) "求购标题" else "商品标题",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (isPurchaseRequest) {
                                    Text(
                                        text = "最高价格: ￥${productData["maxPrice"] as? Double ?: 0.0}",
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "￥${productData["price"] as? Double ?: 0.0}",
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isPurchaseRequest) "求购描述" else "商品描述",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = productData["description"] as? String ?: "暂无描述",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isPurchaseRequest) "求购信息" else "商品信息",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "分类: ${getCategoryName(productData["categoryId"] as? Number ?: 0)}",
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "发布时间: ${TimeUtils.formatConversationTime(productData["createdAt"] as? String ?: "未知")}",
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "AI质检结果",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val overallCondition = productData["overallCondition"] as? String
                                if (!overallCondition.isNullOrEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "成色: ",
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = overallCondition,
                                            fontSize = 14.sp,
                                            color = Color(0xFF00BFFF),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                } else {
                                    Text(
                                        text = "成色: 暂无质检",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                if (!flaws.isNullOrEmpty()) {
                                    Text(
                                        text = "缺陷列表:",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    flaws!!.forEach { flaw ->
                                        val part = flaw["part"] as? String
                                        val desc = flaw["desc"] as? String
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFFFF5F5))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "• ",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Column {
                                                Text(
                                                    text = "$part: ",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = desc ?: "",
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                } else {
                                    Text(
                                        text = "缺陷列表: 无",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isPurchaseRequest) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "浏览次数: ${productData["viewCount"] as? Number ?: 0}",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    //底部按钮区域
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {

                            },
                            modifier = Modifier.size(28.dp),
                            shape = RoundedCornerShape(0.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.comment),
                                contentDescription = "评论"
                            )
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        IconButton(
                            onClick = {

                            },
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(0.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.collect),
                                contentDescription = "收藏"
                            )
                            Text(
                                "收藏"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                val sellerId = (seller?.get("id") as? Number)?.toLong() ?: 0L
                                val currentUserId = UserManager.currentUserId
                                val call = RetrofitClient.conversationApi.findConversation(
                                    currentUserId,
                                    sellerId
                                )
                                call.enqueue(object : Callback<Result<Map<String, Any>>> {
                                    override fun onResponse(
                                        call: Call<Result<Map<String, Any>>>,
                                        response: Response<Result<Map<String, Any>>>
                                    ) {
                                        println("findConversation onResponse: ${response.code()}, body: ${response.body()}")
                                        if (response.isSuccessful) {
                                            val result = response.body()
                                            println(
                                                "findConversation success: ${result?.isSuccess()}, data: ${
                                                    result?.data
                                                }"
                                            )
                                            val conversation =
                                                result?.data

                                            if (conversation != null) {
                                                val conversationId =
                                                    (conversation["id"] as? Number)?.toLong() ?: 0L
                                                val otherUserName =
                                                    conversation["username"] as? String ?: "卖家"
                                                navController.navigate(
                                                    Screen.ConversationDetail.route
                                                        .replace(
                                                            "{conversationId}",
                                                            conversationId.toString()
                                                        )
                                                        .replace("{otherUserName}", otherUserName)
                                                )
                                            } else {
                                                val request = CreateConversationRequest(
                                                    initiatorId = currentUserId,
                                                    receiverId = sellerId,
                                                    productId = productId,
                                                    type = "product"
                                                )
                                                val createCall =
                                                    RetrofitClient.conversationApi.createConversation(
                                                        request
                                                    )
                                                createCall.enqueue(object :
                                                    Callback<Result<Long>> {
                                                    override fun onResponse(
                                                        call: Call<Result<Long>>,
                                                        response: Response<Result<Long>>
                                                    ) {
                                                        println("createConversation onResponse: ${response.code()}, body: ${response.body()}")
                                                        if (response.isSuccessful) {
                                                            val result = response.body()
                                                            println(
                                                                "createConversation success: ${result?.isSuccess()}, data: ${result?.data}"
                                                            )
                                                            if (result != null && result.isSuccess()) {
                                                                val newId = result.data ?: 0L
                                                                println("new conversationId: $newId")
                                                                val sellerName =
                                                                    productData["username"] as? String
                                                                        ?: "卖家"
                                                                navController.navigate(
                                                                    Screen.ConversationDetail.route
                                                                        .replace(
                                                                            "{conversationId}",
                                                                            newId.toString()
                                                                        )
                                                                        .replace(
                                                                            "{otherUserName}",
                                                                            sellerName
                                                                        )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<Result<Long>>,
                                                        t: Throwable
                                                    ) {
                                                        println("createConversation onFailure: ${t.message}")
                                                        t.printStackTrace()
                                                    }
                                                })
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<Result<Map<String, Any>>>,
                                        t: Throwable
                                    ) {
                                        println("findConversation onFailure: ${t.message}")
                                        t.printStackTrace()
                                    }
                                })
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .width(120.dp),
                            shape = RoundedCornerShape(
                                topStart = 32.dp,
                                bottomStart = 32.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00BFFF),
                                contentColor = Color.Black
                            )

                        ) {
                            Text(
                                text = if (isPurchaseRequest) "联系发布者" else "聊一聊",
                                fontSize = 16.sp
                            )
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .height(48.dp)
                                .width(115.dp),
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 32.dp,
                                bottomEnd = 32.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDCDCDC),
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "立即购买",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun loadProductDetail(
    productId: Long,
    callback: (Map<String, Any>?, Map<String, Any>?, List<Map<String, Any>>?, String?) -> Unit
) {
    val call = RetrofitClient.productApi.getProductById(productId)
    call.enqueue(object : Callback<Result<Map<String, Any>>> {
        override fun onResponse(
            call: Call<Result<Map<String, Any>>>,
            response: Response<Result<Map<String, Any>>>
        ) {
            if (response.isSuccessful) {
                val result = response.body()
                println("Response body: $result")
                if (result != null && result.isSuccess()) {
                    val data = result.data ?: emptyMap<String, Any>()
                    println("Response data: $data")
                    val product = data["product"] as? Map<String, Any>
                    val seller = data["seller"] as? Map<String, Any>
                    val flaws = data["flaws"] as? List<Map<String, Any>>
                    println("Response product: $product, seller: $seller, flaws: $flaws")
                    if (product != null) {
                        callback(product, seller, flaws, null)
                    } else {
                        callback(null, null, null, "商品数据为空")
                    }
                } else {
                    val message = result?.message ?: "加载失败"
                    callback(null, null, null, message)
                }
            } else {
                callback(null, null, null, "加载失败: ${response.code()}")
            }
        }

        override fun onFailure(
            call: Call<Result<Map<String, Any>>>, t: Throwable
        ) {
            callback(null, null, null, "网络错误: ${t.message}")
        }
    })
}

private fun getCategoryName(categoryId: Number): String {
    return when (categoryId.toInt()) {
        1 -> "数码产品"
        2 -> "服装鞋包"
        3 -> "书籍文具"
        4 -> "运动户外"
        5 -> "其他"
        else -> "未知分类"
    }
}

private fun loadPurchaseRequestDetail(
    purchaseRequestId: Long,
    callback: (Map<String, Any>?, String?) -> Unit
) {
    val call = RetrofitClient.productApi.getPurchaseRequestById(purchaseRequestId)
    call.enqueue(object : Callback<Result<Map<String, Any>>> {
        override fun onResponse(
            call: Call<Result<Map<String, Any>>>,
            response: Response<Result<Map<String, Any>>>
        ) {
            if (response.isSuccessful) {
                val result = response.body()
                println("Response body: $result")
                if (result != null && result.isSuccess()) {
                    val data = result.data ?: emptyMap<String, Any>()
                    println("Response data: $data")
                    if (data.isNotEmpty()) {
                        callback(data, null)
                    } else {
                        callback(null, "求购数据为空")
                    }
                } else {
                    val message = result?.message ?: "加载失败"
                    callback(null, message)
                }
            } else {
                callback(null, "加载失败: ${response.code()}")
            }
        }

        override fun onFailure(
            call: Call<Result<Map<String, Any>>>, t: Throwable
        ) {
            callback(null, "网络错误: ${t.message}")
        }
    })
}