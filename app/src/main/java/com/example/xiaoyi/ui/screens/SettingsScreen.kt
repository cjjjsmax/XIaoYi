package com.example.xiaoyi.ui.screens

import android.R.id.icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.ui.components.DetailScreenTemplate

@Composable
fun SettingsScreen(
    navController: NavController,
    userId: Long,
    paddingValues: PaddingValues,
    onLogout: () -> Unit
){
    DetailScreenTemplate(
        navController = navController,
        title = "设置",
        isLoading = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingsMenuItem(
                title = "个人资料",
                onClick = {
                    navController.navigate(Screen.EditProfile.route.replace("{userId}", userId.toString()))
                }
            )
            SettingsMenuItem(
                title = "地址管理",
                onClick = {
                    navController.navigate(Screen.AddressManagement.route.replace("{userId}",userId.toString()))
                }
            )
            SettingsMenuItem(
                title = "账号安全",
                onClick = {
                    navController.navigate(Screen.AccountSecurity.route.replace("{userId}", userId.toString()))
                }
            )

            Button(
                onClick = { onLogout() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("退出登录")
            }
        }
    }
}
@Composable
private fun SettingsMenuItem(
    title: String,
    onClick: ()-> Unit
){
    Column{
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(">")
            }
        }
        Divider(
            thickness = 1.dp,
            color = Color.Black
        )
    }
}