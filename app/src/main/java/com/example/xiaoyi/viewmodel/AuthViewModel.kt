package com.example.xiaoyi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.data.database.AppDatabase
import com.example.xiaoyi.extensions.toEntity
import com.example.xiaoyi.extensions.toModel
import com.example.xiaoyi.model.User
import com.example.xiaoyi.utils.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val appDatabase: AppDatabase) : ViewModel() {
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(username: String, password: String) {
        println("AuthViewModel: login() called with username=$username")
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
                println("AuthViewModel: onResponse called, isSuccessful=${response.isSuccessful}")
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    println("AuthViewModel: responseBody=$responseBody")
                    if (responseBody != null) {
                        val code = (responseBody["code"] as? Number)?.toInt() ?: 0
                        println("AuthViewModel: code=$code")

                        if (code == 200) {
                            val data = responseBody["data"] as? Map<String, Any>
                            println("AuthViewModel: data=$data")
                            if (data != null) {
                                val userData = data["user"] as? Map<String, Any>
                                val token = data["token"] as? String ?: ""
                                println("AuthViewModel: userData=$userData")
                                if (userData != null) {
                                    val user = User(
                                        id = (userData["id"] as? Number)?.toLong() ?: 0L,
                                        username = userData["username"] as? String ?: "",
                                        studentId = userData["studentId"] as? String ?: "",
                                        passwordHash = userData["passwordHash"] as? String ?: "",
                                        avatarUrl = userData["avatarUrl"] as? String ?: "",
                                        phone = userData["phone"] as? String ?: "",
                                        school = userData["school"] as? String ?: "",
                                        creditScore = (userData["creditScore"] as? Number)?.toString() ?: "",
                                        createdAt = System.currentTimeMillis(),
                                        token = token
                                    )
                                    println("AuthViewModel: User created, id=${user.id}, username=${user.username}")
                                    UserManager.login(user, token)
                                    println("AuthViewModel: Setting isLoggedIn to true")
                                    _isLoggedIn.value = true
                                    viewModelScope.launch(Dispatchers.IO) {
                                        appDatabase.userDao().insert(user.toEntity())
                                        println("AuthViewModel: User saved to database")
                                    }
                                } else {
                                    println("AuthViewModel: userData is null")
                                    _loginError.value = "获取用户信息失败"
                                }
                            } else {
                                println("AuthViewModel: data is null")
                                _loginError.value = "获取数据失败"
                            }
                        } else {
                            val message = responseBody["message"] as? String ?: "登录失败"
                            println("AuthViewModel: Login failed, message=$message")
                            _loginError.value = message
                        }
                    }
                } else {
                    println("AuthViewModel: Response not successful, code=${response.code()}")
                    _loginError.value = "登录失败: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>?>, t: Throwable) {
                println("AuthViewModel: onFailure, error=${t.message}")
                _isLoading.value = false
                _loginError.value = "网络错误: ${t.message}"
            }
        })
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.userDao().deleteAll()
        }
        UserManager.logout()
        _isLoggedIn.value = false
    }

    fun recoverLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = appDatabase.userDao().getAllUsers()
            if (users.isNotEmpty()) {
                val cachedUser = users.first()
                val user = cachedUser.toModel()
                if (user.token.isNotEmpty()) {
                    UserManager.login(user, user.token)
                    _isLoggedIn.value = true
                }
            }
        }
    }
    fun updateUserInfo(updatedUser: User) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.userDao().insert(updatedUser.toEntity())
            UserManager.login(updatedUser)
        }
    }

    fun loginSuccess() {
        _isLoggedIn.value = true
    }
}