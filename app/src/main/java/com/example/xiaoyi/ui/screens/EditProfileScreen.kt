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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.Result
import com.example.xiaoyi.model.User
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditProfileScreen(
    navController: NavController,
    userId: Long,
    paddingValues: PaddingValues
) {
    var username by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        loadUserInfo(userId) { user ->
            username = user.username
            studentId = user.studentId
            school = user.school
        }
        isLoading = false
    }

    DetailScreenTemplate(
        navController = navController,
        title = "个人资料",
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = ""
                },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = studentId,
                onValueChange = {
                    studentId = it
                    errorMessage = ""
                },
                label = { Text("学号") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = school,
                onValueChange = {
                    school = it
                    errorMessage = ""
                },
                label = { Text("学校") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                        username.isBlank() -> errorMessage = "用户名不能为空"
                        studentId.isBlank() -> errorMessage = "学号不能为空"
                        school.isBlank() -> errorMessage = "学校不能为空"
                        else -> {
                            updateUserProfile(
                                userId = userId,
                                username = username,
                                studentId = studentId,
                                school = school,
                                onSuccess = {
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
            text = { Text("您的个人资料已更新") },
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
    call.enqueue(object : Callback<Result<Map<String, Any>>> {
        override fun onResponse(call: Call<Result<Map<String, Any>>>, response: Response<Result<Map<String, Any>>>) {
            if (response.isSuccessful) {
                val result = response.body()
                val data = result?.data ?: emptyMap<String, Any>()

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

        override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) { }
    })
}

private fun updateUserProfile(
    userId: Long,
    username: String,
    studentId: String,
    school: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val call = RetrofitClient.userApi.updateUser(
        userId = userId,
        username = username,
        studentId = studentId,
        school = school
    )
    call.enqueue(object : Callback<Result<Map<String, Any>>> {
        override fun onResponse(call: Call<Result<Map<String, Any>>>, response: Response<Result<Map<String, Any>>>) {
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null && result.isSuccess()) {
                    onSuccess()
                } else {
                    val message = result?.message ?: "更新失败"
                    onError(message)
                }
            } else {
                onError("更新失败: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) {
            onError("网络错误: ${t.message}")
        }
    })
}