package com.example.xiaoyi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val BASE_URL = "http://192.168.2.4:8080"

private fun getFullImageUrl(imageUrl: String?): String {
    if (imageUrl.isNullOrEmpty()) return ""
    if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
        return imageUrl
    }
    if (imageUrl.startsWith("/")) {
        return BASE_URL + imageUrl
    }
    return "$BASE_URL/$imageUrl"
}

@Composable
fun OrderCard(
    order: Map<String, Any>,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 商品图片
            AsyncImage(
                model = getFullImageUrl(order["productImage"] as? String),
                contentDescription = "商品图片",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 商品标题
                Text(
                    text = order["productTitle"] as? String ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 卖家/买家信息
                val sellerName = order["sellerName"] as? String ?: "未知用户"
                val buyerName = order["buyerName"] as? String ?: "未知用户"
                Text(
                    text = "卖家: $sellerName",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "买家: $buyerName",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 价格和状态
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "￥${order["price"]}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Text(
                        text = getStatusText((order["status"] as? Number)?.toInt() ?: 0),
                        fontSize = 14.sp,
                        color = getStatusColor((order["status"] as? Number)?.toInt() ?: 0)
                    )
                }
            }
        }

        // 时间信息
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = formatTime(order["createdAt"] as? String ?: ""),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getStatusText(status: Int): String {
    return when (status) {
        0 -> "待确认"
        1 -> "已确认"
        else -> "未知状态"
    }
}

private fun getStatusColor(status: Int): Color {
    return when (status) {
        0 -> Color(0xFFFF9800)
        1 -> Color(0xFF2196F3)
        else -> Color(0xFF9E9E9E)
    }
}

private fun formatTime(timeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timeString) ?: Date()
        outputFormat.format(date)
    } catch (e: Exception) {
        timeString
    }
}