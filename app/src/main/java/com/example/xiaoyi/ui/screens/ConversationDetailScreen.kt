package com.example.xiaoyi.ui.screens

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.R
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.Message
import com.example.xiaoyi.model.SendMessageRequest
import com.example.xiaoyi.utils.TimeUtils
import com.example.xiaoyi.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

@Composable
fun ConversationDetailScreen(
    navController: NavController,
    conversationId: Long,
    paddingValues: PaddingValues
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var messageText by remember { mutableStateOf("") }
    var otherUserName by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    fun startVoiceInput(context: android.content.Context) {
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说话...")

        try {
            recognizer.setRecognitionListener(
                object : RecognitionListener {
                    override fun onResults(results: Bundle?) {
                        val matches =
                            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (matches != null && matches.isNotEmpty()) {
                            messageText = matches[0]
                        }
                    }
                    override fun onError(error: Int) {
                        // 处理错误
                    }
                    // 其他回调方法...
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                }
            )
            recognizer.startListening(intent)
        } catch (e: Exception) {

        }
    }

    fun markMessagesAsRead() {
        val currentUserId = UserManager.currentUserId
        val call = RetrofitClient.messageApi.markAsRead(conversationId, currentUserId)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {

            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {

            }
        })
    }

    fun loadMessages() {
        isLoading = true
        errorMessage = null

        val call = RetrofitClient.messageApi.getConversationMessages(conversationId)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.get("success") == true) {
                        val messageData = responseBody["data"] as? List<Map<String, Any>>
                        val messageList = messageData?.map {
                            Message(
                                id = (it["id"] as? Number)?.toLong() ?: 0L,
                                conversationId = (it["conversationId"] as? Number)?.toLong() ?: 0L,
                                senderId = (it["senderId"] as? Number)?.toLong() ?: 0L,
                                receiverId = (it["receiverId"] as? Number)?.toLong() ?: 0L,
                                content = it["content"] as? String ?: "",
                                type = it["type"] as? String ?: "",
                                timestamp = it["createdAt"] as? String ?: "",
                                isRead = (it["isRead"] as? Number)?.toInt() == 1,
                                createdAt = it["createdAt"] as? String ?: ""
                            )
                        } ?: emptyList()
                        messages = messageList
                        markMessagesAsRead()
                    } else {
                        errorMessage = responseBody?.get("message") as? String ?: "加载失败"
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(
                call: Call<Map<String, Any>>,
                t: Throwable
            ) {
                errorMessage = "网络错误: ${t.message}"
                isLoading = false
            }
        })
    }

    fun sendMessage() {
        if (messageText.isBlank()) return
        val currentUserId = UserManager.currentUserId
        val request = SendMessageRequest(
            conversationId = conversationId,
            senderId = currentUserId,
            receiverId = messages.firstOrNull()
                ?.let { if (it.senderId == currentUserId) it.receiverId else it.senderId } ?: 0L,
            content = messageText,
            type = "text"
        )
        val call = RetrofitClient.messageApi.sendMessage(request)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    messageText = ""
                    loadMessages()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {

            }
        })
    }
    LaunchedEffect(conversationId) {
        loadMessages()
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回"
                    )
                }
                Text(
                    text = otherUserName.ifEmpty { "会话" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { /* 更多操作 */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多"
                    )
                }
            }
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
                            text = errorMessage ?: "加载失败",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { loadMessages() }) {
                            Text("重试")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    state = listState
                ) {
                    itemsIndexed(messages) { index, message ->
                        val isCurrentUser = message.senderId == UserManager.currentUserId

                        // 检查是否需要显示时间分隔符
                        if (index == 0 || TimeUtils.formatConversationTime(message.timestamp) != TimeUtils.formatConversationTime(messages[index - 1].timestamp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                        text = TimeUtils.formatConversationTime(message.timestamp),
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.Center),
                                        textAlign = TextAlign.Center
                                    )
                            }
                        }

                        if (isCurrentUser) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(
                                            Color(0xFF2196F3),
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = 16.dp
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = message.content,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = TimeUtils.formatConversationTime(message.timestamp),
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(
                                            Color.White,
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = message.content,
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = TimeUtils.formatConversationTime(message.timestamp),
                                        color = Color.Gray,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val context = LocalContext.current
                        IconButton(onClick = { startVoiceInput(context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.mic),
                                contentDescription = "语音",
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("输入消息...") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            shape = RoundedCornerShape(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { sendMessage() },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(48.dp),
                            enabled = messageText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "发送",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

