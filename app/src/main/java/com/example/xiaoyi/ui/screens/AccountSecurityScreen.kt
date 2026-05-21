package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.User
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AccountSecurityScreen(
    navController: NavController,
    userId: Long,
    paddingValues: PaddingValues
) {
    var phone by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        loadUserInfo(userId) { user ->
            phone = user.phone
        }
        isLoading = false
    }

    DetailScreenTemplate(
        navController = navController,
        title = "账号安全",
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "修改手机号",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    errorMessage = ""
                },
                label = { Text("手机号") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "修改密码",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = oldPassword,
                onValueChange = {
                    oldPassword = it
                    errorMessage = ""
                },
                label = { Text("原密码") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                },
                label = { Text("新密码") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = { Text("确认新密码") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    when {
                        phone.isBlank() -> errorMessage = "手机号不能为空"
                        oldPassword.isNotBlank() && newPassword.isBlank() -> errorMessage = "请输入新密码"
                        newPassword.isNotBlank() && oldPassword.isBlank() -> errorMessage = "请输入原密码"
                        newPassword.isNotBlank() && newPassword != confirmPassword -> errorMessage = "两次密码不一致"
                        newPassword.isNotBlank() && newPassword.length < 6 -> errorMessage = "密码长度至少6位"
                        else -> {
                            updateAccountSecurity(
                                userId = userId,
                                phone = phone,
                                oldPassword = oldPassword.ifBlank { null },
                                newPassword = newPassword.ifBlank { null },
                                onSuccess = {
                                    successMessage = "更新成功"
                                    showSuccessDialog = true
                                },
                                onError = { error ->
                                    errorMessage = error
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("保存", fontSize = 18.sp)
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("保存成功") },
            text = { Text(successMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.popBackStack()
                }) {
                    Text("确定")
                }
            }
        )
    }
}

private fun loadUserInfo(
    userId: Long,
    onSuccess: (User) -> Unit
) {
    val call = RetrofitClient.userApi.getUserById(userId)
    call.enqueue(object : Callback<Map<String, Any>> {
        override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
            if (response.isSuccessful) {
                val userData = response.body()
                val data = userData?.get("data") as? Map<*, *> ?: emptyMap<Any, Any>()

                val user = User(
                    id = (data["id"] as? Number)?.toLong() ?: 0L,
                    username = data["username"]?.toString() ?: "",
                    studentId = data["studentId"]?.toString() ?: "",
                    phone = data["phone"]?.toString() ?: "",
                    avatarUrl = data["avatarUrl"]?.toString() ?: "",
                    school = data["school"]?.toString() ?: "",
                    creditScore = data["creditScore"]?.toString() ?: "",
                    createdAt = 0L
                )
                onSuccess(user)
            }
        }

        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) { }
    })
}

private fun updateAccountSecurity(
    userId: Long,
    phone: String,
    oldPassword: String?,
    newPassword: String?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val call = RetrofitClient.userApi.updateAccountSecurity(
        userId = userId,
        phone = phone,
        oldPassword = oldPassword,
        newPassword = newPassword
    )
    call.enqueue(object : Callback<Map<String, Any>> {
        override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
            if (response.isSuccessful) {
                val body = response.body()
                val code = (body?.get("code") as? Number)?.toInt() ?: 0
                if (code == 200) {
                    onSuccess()
                } else {
                    val message = body?.get("message") as? String ?: "更新失败"
                    onError(message)
                }
            } else {
                onError("更新失败: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
            onError("网络错误: ${t.message}")
        }
    })
}