package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.Conversation
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.utils.TimeUtils
import com.example.xiaoyi.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ConversationsScreen(
    navController: NavController,
    paddingValues: PaddingValues
){
    var conversations by remember { mutableStateOf<List<Conversation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadConversations() {
        isLoading = true
        errorMessage = null

        val currentUserId = UserManager.currentUserId
        val call = RetrofitClient.conversationApi.getConversations(currentUserId)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.get("success") == true) {
                        val data = responseBody["data"] as? List<Map<String, Any>>
                        val conversationList = data?.map {
                            Conversation(
                                id = (it["id"] as? Number)?.toLong() ?: 0L,
                                initiatorId = (it["initiatorId"] as? Number)?.toLong() ?: 0L,
                                receiverId = (it["receiverId"] as? Number)?.toLong() ?: 0L,
                                productId = (it["productId"] as? Number)?.toLong(),
                                status = it["status"] as? String ?: "",
                                createdAt = it["createdAt"] as? String ?: "",
                                lastMessage = it["lastMessage"] as? String ?: "",
                                lastMessageTime = it["lastMessageTime"] as? String ?: "",
                                otherUserId = (it["otherUserId"] as? Number)?.toLong() ?: 0L,
                                otherUserName = it["otherUserName"] as? String ?: "未知用户",
                                otherUserAvatar = it["otherUserAvatar"] as? String ?: "",
                                unreadCount = (it["unreadCount"] as? Number)?.toInt() ?: 0
                            )
                        } ?: emptyList()
                        conversations = conversationList
                    } else {
                        errorMessage = responseBody?.get("message") as? String ?: "加载失败"
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                errorMessage = "网络错误: ${t.message}"
                isLoading = false
            }
        })
    }

    LaunchedEffect(UserManager.currentUserId) {
        loadConversations()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "消息",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF2196F3)
                    )
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "加载失败",
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            conversations.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "暂无会话",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "点击商家可以开始对话",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(conversations.size) {
                            index -> ConversationItem(
                        conversation = conversations[index],
                        onClick = {
                            navController.navigate(Screen.ConversationDetail.route.replace("{conversationId}", conversations[index].id.toString()))
                        }
                    )
                        if (index < conversations.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color(0xFFE0E0E0)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (conversation.unreadCount > 0) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.otherUserName.first().toString(),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.otherUserName,
                        fontSize = 16.sp,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = TimeUtils.formatConversationTime(conversation.lastMessageTime),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = conversation.lastMessage,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF44336), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${conversation.unreadCount}条未读",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}