package com.example.xiaoyi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.data.database.AppDatabase
import com.example.xiaoyi.extensions.toEntity
import com.example.xiaoyi.extensions.toModel
import com.example.xiaoyi.model.Result
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
    //登录错误信息
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError
    //是否登录
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    //是否正在加载中
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //用户登录
    fun login(username: String, password: String) {
        println("AuthViewModel: login() called with username=$username")
        _isLoading.value = true
        _loginError.value = null
        //构建登录函数
        val loginData = mapOf(
            "username" to username,
            "password" to password  // 直接发送明文，后端使用 BCrypt 验证
        )

        //调用后端登录API
        val call = RetrofitClient.userApi.login(loginData)
        call.enqueue(object : Callback<Result<Map<String, Any>>> {
            override fun onResponse(
                call: Call<Result<Map<String, Any>>>,
                response: Response<Result<Map<String, Any>>>
            ) {
                println("AuthViewModel: onResponse called, isSuccessful=${response.isSuccessful}")
                _isLoading.value = false
                if (response.isSuccessful) {
                    //获取响应体
                    val result = response.body()
                    println("AuthViewModel: result=$result")
                    if (result != null) {
                        if (result.isSuccess()) {
                            //提取data字段
                            val data = result.data
                            println("AuthViewModel: data=$data")
                            if (data != null) {
                                //提取用户信息和token
                                val userData = data["user"] as? Map<String, Any>
                                val token = data["token"] as? String ?: ""
                                println("AuthViewModel: userData=$userData")
                                if (userData != null) {
                                    //解析用户信息，创建User对象
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
                                    //更新全局用户状态
                                    UserManager.login(user, token)
                                    println("AuthViewModel: Setting isLoggedIn to true")
                                    _isLoggedIn.value = true
                                    //异步保存用户信息到本地数据库，使用Dispatchers.IO线程池避免线程阻塞
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
                            println("AuthViewModel: Login failed, message=${result.message}")
                            _loginError.value = result.message
                        }
                    }
                } else {
                    println("AuthViewModel: Response not successful, code=${response.code()}")
                    _loginError.value = "登录失败: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) {
                println("AuthViewModel: onFailure, error=${t.message}")
                _isLoading.value = false
                _loginError.value = "网络错误: ${t.message}"
            }
        })
    }

    //退出登录
    fun logout() {
        //异步清楚本地数据库中所有用户信息，使用Dispatchers.IO线程池避免线程阻塞
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.userDao().deleteAll()
        }
        //清空全局用户状态
        UserManager.logout()
        _isLoggedIn.value = false
    }

    //自动登录恢复
    fun recoverLogin() {
        //异步从本地数据库获取所有用户信息，使用Dispatchers.IO线程池避免线程阻塞
        viewModelScope.launch(Dispatchers.IO) {
            val users = appDatabase.userDao().getAllUsers()
            if (users.isNotEmpty()) {
                val cachedUser = users.first()
                //将数据库实体转换为User模型
                val user = cachedUser.toModel()
                if (user.token.isNotEmpty()) {
                    //恢复全局登录状态
                    UserManager.login(user, user.token)
                    _isLoggedIn.value = true
                }
            }
        }
    }

    //更新用户信息
    fun updateUserInfo(updatedUser: User) {
        //异步保存用户信息到本地数据库，使用Dispatchers.IO线程池避免线程阻塞
        viewModelScope.launch(Dispatchers.IO) {
            //将User模型转换为UserEntity，保存到数据库
            appDatabase.userDao().insert(updatedUser.toEntity())
            //更新全局用户状态
            UserManager.login(updatedUser)
        }
    }

    fun loginSuccess() {
        _isLoggedIn.value = true
    }
}