package com.example.xiaoyi.ui.screens

import android.R.attr.text
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xiaoyi.R
import com.example.xiaoyi.model.Result
import com.example.xiaoyi.model.User
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.viewmodel.AuthViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


@Composable
fun ProfileScreen(
    navController: NavController,
    userId: Long,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showImagePicker by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()//选择单张图片
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            uploadAvatar(
                context = context,
                imageUri = uri,
                userId = userId,
                onSuccess = { avatarUrl ->
                    user = user?.copy(avatarUrl = avatarUrl)
                    user?.let { authViewModel.updateUserInfo(it) }
                    Toast.makeText(context, "头像上传成功", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    LaunchedEffect(userId) {
        loadUserInfo(
            userId = userId,
            onSuccess = {
                user = it
                isLoading = false
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "个人中心",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (user != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding( 16.dp)
                        .clickable {
                            showImagePicker = true//点击弹出选择对话框
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        //显示用户名首字母作为默认头像
                        text = user!!.username.firstOrNull()?.toString() ?: "U",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user!!.username,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)

                        )
                        IconButton(
                            onClick = {
                                navController.navigate(
                                    Screen.Settings.route.replace(
                                        "{userId}",
                                        userId.toString()
                                    )
                                )
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.set),
                                contentDescription = "设置",
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    Text(
                        text = "信誉值: ${user!!.creditScore}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileMenuItem(
                title = "我的发布",
                iconRes = R.drawable.release_commodities,
                onClick = {
                    navController.navigate(
                        Screen.MyPosts.route.replace(
                            "{userId}",
                            userId.toString()
                        )
                    )
                }
            )
            ProfileMenuItem(
                title = "我的求购",
                iconRes = R.drawable.publish_purchase,
                onClick = {
                    navController.navigate(
                        Screen.MyWanted.route.replace(
                            "{userId}",
                            userId.toString()
                        )
                    )
                }
            )
            ProfileMenuItem(
                title = "我的订单",
                iconRes = R.drawable.transaction_completed,
                onClick = {
                    navController.navigate(
                        Screen.MyOrders.route.replace(
                            "{userId}",
                            userId.toString()
                        )
                    )
                }
            )
        }
    }
    if (showImagePicker) {
        AlertDialog(
            onDismissRequest = { showImagePicker = false },
            title = {
                Text(
                    text = "选择头像",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "请选择获取头像的方式",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImagePicker = false
                        imagePickerLauncher.launch("image/*")//打开相册
                    }
                ) {
                    Text("从相册选择")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImagePicker = false
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

//菜单组件
@Composable
private fun ProfileMenuItem(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        onClick = onClick,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 16.dp)
            )
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = " >",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

//加载用户信息
private fun loadUserInfo(
    userId: Long,
    onSuccess: (User) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val call = RetrofitClient.userApi.getUserById(userId)
        call.enqueue(object : Callback<Result<Map<String, Any>>> {
            override fun onResponse(
                call: Call<Result<Map<String, Any>>>,
                response: Response<Result<Map<String, Any>>>
            ) {
                try {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess()) {
                            // 获取data字段
                            val data = result.data ?: emptyMap<String, Any>()

                            // 安全处理各个字段 - 支持多种命名方式
                            val id = (data["id"] as? Number)?.toLong() ?: 0L
                            val username = data["username"]?.toString() ?: ""
                            val studentId = when {
                                data.containsKey("studentId") -> data["studentId"]?.toString() ?: ""
                                data.containsKey("student_id") -> data["student_id"]?.toString()
                                    ?: ""

                                else -> ""
                            }
                            val phone = data["phone"]?.toString() ?: ""
                            val avatarUrl = when {
                                data.containsKey("avatarUrl") -> data["avatarUrl"]?.toString() ?: ""
                                data.containsKey("avatar_url") -> data["avatar_url"]?.toString()
                                    ?: ""

                                else -> ""
                            }
                            val school = data["school"]?.toString() ?: ""
                            val creditScore = when {
                                data.containsKey("creditScore") -> data["creditScore"]?.toString()
                                    ?: ""

                                data.containsKey("credit_score") -> data["credit_score"]?.toString()
                                    ?: ""

                                else -> ""
                            }
                            val createdAt =
                                data["createdAt"]?.toString() ?: data["created_at"]?.toString()
                                ?: ""

                            // 处理 createdAt 可能是不同格式的情况
                            val createdAtLong = when {
                                data.containsKey("createdAt") && data["createdAt"] is Number -> (data["createdAt"] as Number).toLong()
                                data.containsKey("createdAt") && data["createdAt"] is String -> (data["createdAt"] as String).toLongOrNull()
                                    ?: System.currentTimeMillis()

                                data.containsKey("created_at") && data["created_at"] is Number -> (data["created_at"] as Number).toLong()
                                data.containsKey("created_at") && data["created_at"] is String -> (data["created_at"] as String).toLongOrNull()
                                    ?: System.currentTimeMillis()

                                else -> System.currentTimeMillis()
                            }

                            val user = User(
                                id = id,
                                username = username,
                                studentId = studentId,
                                phone = phone,
                                avatarUrl = avatarUrl,
                                school = school,
                                creditScore = creditScore,
                                createdAt = createdAtLong
                            )
                            onSuccess(user)
                        } else {
                            onError("获取用户信息失败: 响应数据为空")
                        }
                    } else {
                        onError("请求失败: ${response.code()}")
                    }
                } catch (e: Exception) {
                    onError("解析用户信息失败: ${e.message}")
                }
            }

            override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) {
                onError("网络错误: ${t.message} ")
            }
        })
    } catch (e: Exception) {
        onError("加载用户信息失败: ${e.message}")
    }
}

//上传头像
private fun uploadAvatar(
    context: Context,
    imageUri: Uri,
    userId: Long,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    Thread {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)

            if (inputStream != null) {
                val tempFile = File(context.cacheDir, "avatar_temp.jpg")
                tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
                //创建Multipart请求体
                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "file",
                    tempFile.name,
                    requestFile
                )
                val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                //上传头像
                val call = RetrofitClient.userApi.uploadAvatar(body, userIdBody)
                call.enqueue(object : Callback<Result<Map<String, Any>>> {
                    override fun onResponse(
                        call: Call<Result<Map<String, Any>>>,
                        response: Response<Result<Map<String, Any>>>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null && result.isSuccess()) {
                                val data = result.data ?: emptyMap<String, Any>()
                                val avatarUrl = data["avatarUrl"] as? String ?: data["avatar_url"] as? String ?: ""
                                onSuccess(avatarUrl)
                            } else {
                                onError(result?.message ?: "上传失败")
                            }
                        } else {
                            onError("上传失败: ${response.code()}")
                        }
                        tempFile.delete()
                    }

                    override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) {
                        onError("网络错误: ${t.message}")
                        tempFile.delete()
                    }
                })
            } else {
                onError("无法读取图片")
            }
        } catch (e: Exception) {
            onError("上传失败: ${e.message}")
        }
    }.start()
}
