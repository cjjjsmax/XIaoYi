package com.example.xiaoyi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.api.interceptor.TokenExpiredInterceptor
import com.example.xiaoyi.navigation.AppNavigation
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.ui.components.BottomNavBar
import com.example.xiaoyi.ui.theme.XiaoYiTheme
import com.example.xiaoyi.utils.UserManager
import com.example.xiaoyi.viewmodel.AuthViewModel
import com.example.xiaoyi.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        UserManager.init(applicationContext)
        RetrofitClient.init(applicationContext)
        setContent {
            XiaoYiTheme {
                MainScreen()

            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory(context)
    )
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    var userId by remember { mutableStateOf(0L) }
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    val currentRoute = remember {
        mutableStateOf(navController.currentBackStackEntry?.destination?.route)
    }

    LaunchedEffect(Unit) {
        authViewModel.recoverLogin()
    }
    // 监听导航变化，更新当前路由
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener {
            _, destination, _ ->
            currentRoute.value = destination.route
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            userId = UserManager.currentUserId
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            val showBottomBar = isLoggedIn && 
                currentRoute.value in listOf(
                    Screen.Home.route,
                    Screen.Conversations.route,
                    Screen.Sell.route,
                    Screen.Profile.route
                )
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        }
    ) {
        AppNavigation(
            navController = navController,
            startDestination = startDestination,
            authViewModel = authViewModel,
            onLoginSuccess = { 
                println("MainActivity: onLoginSuccess called, setting isLoggedIn to true")
                authViewModel.loginSuccess()
                userId = UserManager.currentUserId  // 更新 userId
            },
            onLogout = { 
                println("MainActivity: onLogout called, setting isLoggedIn to false")
                authViewModel.logout()
                userId = UserManager.currentUserId  // 更新 userId
            },
            userId = userId,
            paddingValues = it
        )
    }
}