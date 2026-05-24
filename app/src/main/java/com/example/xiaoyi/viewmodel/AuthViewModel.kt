package com.example.xiaoyi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.User
import com.example.xiaoyi.utils.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(username: String, password: String) {
        _isLoading.value = true
        _loginError.value = null

        val loginData = mapOf(
            "username" to username,
            "password" to password
        )

        val call = RetrofitClient.userApi.login(loginData)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>?>,
                response: Response<Map<String, Any>?>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val success = responseBody["success"] as? Boolean ?: false

                        if (success) {
                            val userData = responseBody["user"] as? Map<String, Any>
                            if (userData != null) {
                                val user = User(
                                    id = (userData["id"] as? Number)?.toLong() ?: 0L,
                                    username = userData["username"] as? String ?: "",
                                    studentId = userData["studentId"] as? String ?: "",
                                    passwordHash = userData["passwordHash"] as? String ?: "",
                                    avatarUrl = userData["avatarUrl"] as? String ?: "",
                                    phone = userData["phone"] as? String ?: "",
                                    school = userData["school"] as? String ?: "",
                                    creditScore = userData["creditScore"] as? String ?: "",
                                    createdAt = (userData["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                )
                                UserManager.login(user)
                                _isLoggedIn.value = true
                            } else {
                                _loginError.value = "获取用户信息失败"
                            }
                        } else {
                            val message = responseBody["message"] as? String ?: "登录失败"
                            _loginError.value = message
                        }
                    }
                } else {
                    _loginError.value = "登录失败: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>?>, t: Throwable) {
                _isLoading.value = false
                _loginError.value = "网络错误: ${t.message}"
            }
        })
    }

    fun logout() {
        UserManager.logout()
        _isLoggedIn.value = false
    }
}