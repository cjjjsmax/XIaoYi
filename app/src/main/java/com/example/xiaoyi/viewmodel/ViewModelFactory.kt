package com.example.xiaoyi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.xiaoyi.data.database.AppDatabase

//ViewModel工厂
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory{
    //抑制类型转换警告
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //判断modelClass是否是AuthViewModel或其子类
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)){
            //获取数据库实例
            val database = AppDatabase.getInstance(context.applicationContext)
            //创建AuthViewModel实例传入数据库
            return AuthViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

}