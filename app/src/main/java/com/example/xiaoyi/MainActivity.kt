package com.example.xiaoyi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.xiaoyi.navigation.AppNavigation
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.ui.components.BottomNavBar
import com.example.xiaoyi.ui.theme.XiaoYiTheme
import com.example.xiaoyi.utils.UserManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        UserManager.init(applicationContext)
        setContent {
            XiaoYiTheme {
                MainScreen()

            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(UserManager.isLoggedIn) }
    var userId by remember { mutableStateOf(UserManager.currentUserId) }
    val startDestination = if (UserManager.isLoggedIn) Screen.Home.route else Screen.Login.route
    val currentRoute = remember {
        mutableStateOf(navController.currentBackStackEntry?.destination?.route)
    }
    
    // 监听导航变化，更新当前路由
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener {
            _, destination, _ ->
            currentRoute.value = destination.route
        }
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            // 只在首页、消息、发布和我的页面显示底部导航栏
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
            onLoginSuccess = { 
                println("MainActivity: onLoginSuccess called, setting isLoggedIn to true")
                isLoggedIn = true 
                userId = UserManager.currentUserId  // 更新 userId
            },
            onLogout = { 
                println("MainActivity: onLogout called, setting isLoggedIn to false")
                isLoggedIn = false 
                userId = UserManager.currentUserId  // 更新 userId
            },
            userId = userId,
            paddingValues = it
        )
    }
}