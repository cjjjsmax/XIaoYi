package com.example.xiaoyi.ui.components

import android.widget.Toast
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.xiaoyi.R
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.utils.UserManager

//底部导航栏
@Composable
fun BottomNavBar(navController: NavController){
    val context = LocalContext.current
    
    //使用remember来跟踪当前路由，确保状态更新
    val currentRoute = remember {
        mutableStateOf(Screen.Home.route)
    }
    
    //检查登录状态并导航
    fun checkLoginAndNavigate(targetRoute: String) {
        if (UserManager.isLoggedIn) {
            navController.navigate(targetRoute)
        } else {
            navController.navigate(Screen.Login.route)
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
        }
    }
    
    //监听导航变化，更新当前路由
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener {
            _, destination, _ ->
            currentRoute.value = destination.route ?: Screen.Home.route
        }
        navController.addOnDestinationChangedListener(listener)
        //组件销毁时移除监听器
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    //底部导航栏容器
    NavigationBar{
        NavigationBarItem(
            icon = {Icon(
                painter = painterResource(id = R.drawable.home_page),
                contentDescription = "首页",
                tint = Color.Unspecified//不修改图标颜色
            )},
            label = {Text("首页")},
            selected = currentRoute.value == Screen.Home.route,//是否选中
            onClick = {
                navController.navigate(Screen.Home.route)
            }
        )
        NavigationBarItem(
            icon = {Icon(
                painter = painterResource(id = R.drawable.message),
                contentDescription = "消息",
                tint = Color.Unspecified
            )},
            label = {Text("消息")},
            selected = currentRoute.value == Screen.Conversations.route,
            onClick = {
                checkLoginAndNavigate(Screen.Conversations.route)
            }
        )
        NavigationBarItem(
            icon = {Icon(
                painter = painterResource(id = R.drawable.issue),
                contentDescription = "发布",
                tint = Color.Unspecified
            )},
            label = {Text("发布")},
            selected = currentRoute.value == Screen.Sell.route,
            onClick = {
                checkLoginAndNavigate(Screen.Sell.route)
            }
        )
        NavigationBarItem(
            icon = {Icon(
                painter = painterResource(id = R.drawable.personal_center),
                contentDescription = "我的",
                tint = Color.Unspecified
            )},
            label = {Text("我的")},
            selected = currentRoute.value == Screen.Profile.route,
            onClick = {
                checkLoginAndNavigate(Screen.Profile.route)
            }
        )
    }
}