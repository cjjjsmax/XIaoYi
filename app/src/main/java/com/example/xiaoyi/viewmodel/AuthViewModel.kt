package com.example.xiaoyi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    fun login(username: String,password: String){
        viewModelScope.launch {
            _isLoggedIn.value = true
        }
    }
    fun logout(){
        _isLoggedIn.value = false
    }
}