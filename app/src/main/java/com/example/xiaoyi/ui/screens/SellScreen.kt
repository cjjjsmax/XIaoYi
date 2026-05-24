package com.example.xiaoyi.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.ui.components.ProductFormFields
import com.example.xiaoyi.viewmodel.ProductViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

data class Category(val id: Int, val name: String)
val categories = listOf(
    Category(1, "数码产品"),
    Category(2, "服装鞋包"),
    Category(3, "书籍文具"),
    Category(4, "运动户外"),
    Category(5, "其他")
)

@Composable
fun SellScreen(
    navController: NavController,
    userId: Long = 1,
    paddingValues: PaddingValues,
    viewModel: ProductViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("发布商品", "发布求购")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            text = "发布",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        when (selectedTabIndex) {
            0 -> PublishProductTab(userId = userId, navController = navController, viewModel = viewModel)
            1 -> PublishWantedTab(userId = userId, navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
private fun PublishProductTab(
    userId: Long,
    navController: NavController,
    viewModel: ProductViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val isLoading by viewModel.isPublishProductLoading.collectAsState()
    val errorMessage by viewModel.publishProductErrorMessage.collectAsState()
    val publishSuccess by viewModel.publishProductSuccess.collectAsState()

    val pickImagesLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        uris -> selectedImages = uris
    }

    fun uploadImagesAndPublish(imageUris: List<Uri>) {
        val currentTitle = title
        val currentPrice = price.toDouble()
        val currentDescription = description
        val currentCategoryId = selectedCategory!!.id
        val currentUserId = userId

        if (imageUris.isEmpty()) {
            viewModel.publishProduct(
                title = currentTitle,
                price = currentPrice,
                description = currentDescription,
                categoryId = currentCategoryId,
                sellerId = currentUserId,
                images = ""
            )
            return
        }

        val urls = mutableListOf<String>()
        var uploadCount = 0
        val timestamp = System.currentTimeMillis()

        imageUris.forEachIndexed { index, uri ->
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_image_${timestamp}_${index}.png")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

            RetrofitClient.productApi.uploadImage(part).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    uploadCount++
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.get("imageUrl") as? String
                        if (imageUrl != null) {
                            urls.add(imageUrl)
                        }
                    }

                    if (uploadCount == imageUris.size) {
                        viewModel.publishProduct(
                            title = currentTitle,
                            price = currentPrice,
                            description = currentDescription,
                            categoryId = currentCategoryId,
                            sellerId = currentUserId,
                            images = urls.joinToString(",")
                        )
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    uploadCount++
                    Log.e("SellScreen", "图片上传失败: ${t.message}")
                    if (uploadCount == imageUris.size) {
                        viewModel.publishProduct(
                            title = currentTitle,
                            price = currentPrice,
                            description = currentDescription,
                            categoryId = currentCategoryId,
                            sellerId = currentUserId,
                            images = urls.joinToString(",")
                        )
                    }
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProductFormFields(
            title = title,
            onTitleChange = { title = it },
            price = price,
            onPriceChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                    price = it
                }
            },
            description = description,
            onDescriptionChange = { description = it },
            errorMessage = errorMessage ?: ""
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                pickImagesLauncher.launch("image/*")
            }) {
                Text("选择图片")
            }
            LazyRow {
                items(selectedImages) { uri ->
                    Image(
                        painter = rememberImagePainter(uri),
                        contentDescription = "Selected image",
                        modifier = Modifier.size(80.dp).padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "商品分类 *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.small)
                    .padding(16.dp)
            ) {
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory?.id == category.id,
                            onClick = { selectedCategory = category }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.name,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "ID: ${category.id}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let { error ->
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        Button(
            onClick = {
                when {
                    title.isBlank() -> viewModel.setPublishProductError("请输入商品标题")
                    price.isBlank() -> viewModel.setPublishProductError("请输入商品价格")
                    selectedCategory == null -> viewModel.setPublishProductError("请选择商品分类")
                    else -> {
                        val priceValue = price.toDoubleOrNull()
                        if (priceValue == null || priceValue <= 0) {
                            viewModel.setPublishProductError("请输入有效的商品价格")
                        } else {
                            uploadImagesAndPublish(selectedImages)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("发布中...")
            } else {
                Text(text = "发布商品", fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "提示：带*的字段为必填项",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (publishSuccess) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetPublishProductState()
                navController.popBackStack()
            },
            title = { Text("发布成功") },
            text = { Text("您的商品已成功发布！AI质检正在进行中...") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetPublishProductState()
                        navController.popBackStack()
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
private fun PublishWantedTab(
    userId: Long,
    navController: NavController,
    viewModel: ProductViewModel
) {
    var title by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val isLoading by viewModel.isPublishWantedLoading.collectAsState()
    val errorMessage by viewModel.publishWantedErrorMessage.collectAsState()
    val publishSuccess by viewModel.publishWantedSuccess.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProductFormFields(
                title = title,
                onTitleChange = { title = it },
                price = maxPrice,
                onPriceChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        maxPrice = it
                    }
                },
                description = description,
                onDescriptionChange = { description = it },
                errorMessage = errorMessage ?: "",
                titleLabel = "求购标题",
                priceLabel = "最高预算",
                descriptionLabel = "求购描述"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "求购分类*",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.small)
                    .padding(16.dp)
            ) {
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory?.id == category.id,
                            onClick = { selectedCategory = category }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.name,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "ID: ${category.id}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                when {
                    title.isBlank() -> viewModel.setPublishWantedError("请输入求购物品标题")
                    maxPrice.isBlank() -> viewModel.setPublishWantedError("请输入期望价格")
                    selectedCategory == null -> viewModel.setPublishWantedError("请选择求购分类")
                    else -> {
                        val priceValue = maxPrice.toDoubleOrNull()
                        if (priceValue == null || priceValue <= 0) {
                            viewModel.setPublishWantedError("请输入有效的期望价格")
                        } else {
                            viewModel.publishWanted(
                                title = title,
                                maxPrice = priceValue,
                                description = description,
                                categoryId = selectedCategory!!.id,
                                buyerId = userId
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("发布中...")
            } else {
                Text(text = "发布求购", fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "提示：带 * 的字段为必填项",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (publishSuccess) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetPublishWantedState()
                navController.popBackStack()
            },
            title = { Text("发布成功") },
            text = { Text("您的求购信息已成功发布！") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetPublishWantedState()
                    navController.popBackStack()
                }) {
                    Text("确定")
                }
            }
        )
    }
}