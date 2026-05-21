package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.User
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit = {}
){
    var username by remember{ mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "欢迎回来",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "登陆你的账户",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = ""
            },
            label = {Text("请输入用户名")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = {Text("请输入密码")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "隐藏" else "显示")
                }
            },
        )
        if (!errorMessage.isEmpty()){
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()){
                    errorMessage = "请填写用户名与密码"
                    return@Button
                }
                isLoading = true
                errorMessage = ""

                // 调用后端 API 进行登录
                val loginData = mapOf("username" to username, "password" to password)
                RetrofitClient.userApi.login(loginData).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            val body = response.body()
                            println("Response body: $body")
                            if (body != null) {
                                val code = body["code"]
                                println("Response body: $body")
                                println("Response code: $code, type: ${code?.javaClass}")
                                if (code == 200 || code == "200" || code == 200.0) {
                                    // 登录成功
                                    val data = body["data"] as? Map<String, Any>
                                    if (data != null) {
                                        val userData = data["user"] as? Map<String, Any>
                                        if (userData != null) {
                                            val id = (userData["id"] as? Number)?.toLong() ?: 0L
                                            val username = userData["username"] as? String ?: ""
                                            val studentId = userData["studentId"] as? String ?: ""
                                            val avatarUrl = userData["avatarUrl"] as? String ?: ""
                                            val phone = userData["phone"] as? String ?: ""
                                            val school = userData["school"] as? String ?: ""
                                            val creditScore = userData["creditScore"]?.toString() ?: ""
                                            
                                            val user = User(
                                                id = id,
                                                username = username,
                                                studentId = studentId,
                                                avatarUrl = avatarUrl,
                                                phone = phone,
                                                creditScore = creditScore
                                            )
                                            
                                            UserManager.login(user)
                                            println("Login successful, user: $user")
                                            println("Login successful, calling onLoginSuccess")
                                            onLoginSuccess()
                                        } else {
                                            errorMessage = "登录失败: 用户数据格式错误"
                                            println("Login failed: 用户数据格式错误")
                                        }
                                    } else {
                                        errorMessage = "登录失败: 数据格式错误"
                                        println("Login failed: 数据格式错误")
                                    }
                                } else {
                                    errorMessage = body?.get("message") as? String ?: "登录失败"
                                    println("Login failed: $errorMessage")
                                }
                            } else {
                                errorMessage = "登录失败: 响应体为空"
                                println("Login failed: 响应体为空")
                            }
                        } else {
                            errorMessage = "登录失败: ${response.code()}"
                            println("Login failed with code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        isLoading = false
                        errorMessage = "网络错误: ${t.message}"
                    }
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }else {
                Text("登录", fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "还没有账户？",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = {
                    navController.navigate(Screen.Register.route)
                }
            ) {
                Text("立即注册")
            }
        }
    }
}