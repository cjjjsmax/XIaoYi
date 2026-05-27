package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.viewmodel.AuthViewModel
import com.example.xiaoyi.viewmodel.ViewModelFactory

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }

    println("LoginScreen: isLoggedIn=$isLoggedIn")

    LaunchedEffect(isLoggedIn) {
        println("LoginScreen: LaunchedEffect triggered, isLoggedIn=$isLoggedIn")
        if (isLoggedIn) {
            println("LoginScreen: Navigating to Home...")
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    localError = ""
                },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    localError = ""
                },
                label = { Text("密码") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            val displayError = localError.ifEmpty { loginError ?: "" }
            if (displayError.isNotEmpty()) {
                Text(
                    text = displayError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    when {
                        username.isEmpty() -> localError = "请输入用户名"
                        password.isEmpty() -> localError = "请输入密码"
                        else -> viewModel.login(username, password)
                    }
                },
                enabled = username.isNotEmpty() && password.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "登录")
            }
            TextButton(
                onClick = {
                    navController.navigate(Screen.Register.route)
                }
            ) {
                Text("还没有账号？立即注册")
            }
        }
    }
}