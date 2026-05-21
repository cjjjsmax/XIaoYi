package com.example.xiaoyi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.xiaoyi.R

private const val BASE_URL = "http://192.168.2.4:8080"

private fun getFullImageUrl(imageUrl: String): String {
    if (imageUrl.isEmpty()) return ""
    if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
        return imageUrl
    }
    if (imageUrl.startsWith("/")) {
        return BASE_URL + imageUrl
    }
    return "$BASE_URL/$imageUrl"
}

@Composable
fun ProductCard(
    productName: String,
    price: String,
    location: String,
    imageUrl: String = "",
    onClick: () -> Unit
){
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable{(onClick())},
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ){
            AsyncImage(
                model = getFullImageUrl(imageUrl),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_foreground),
                placeholder = painterResource(R.drawable.ic_launcher_foreground)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ){
                Text(
                    text = productName,
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "￥$price",
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color(0xFFF44336),
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 8.dp)
                    )
            }
        }
    }

}