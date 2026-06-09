package com.example.xiaoyi.data.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.xiaoyi.data.database.dao.ProductDao
import com.example.xiaoyi.data.database.dao.UserDao
import com.example.xiaoyi.data.database.entity.ProductEntity
import com.example.xiaoyi.data.database.entity.UserEntity

//数据库配置类
@Database(
    entities = [ProductEntity::class, UserEntity::class],//数据库包含实体类
    version = 4,//版本号
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {


    abstract fun productDao(): ProductDao//获取商品数据访问对象
    abstract fun userDao(): UserDao//获取用户数据访问对象
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        //获取数据库实例
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xiaoyi_database"
                ).fallbackToDestructiveMigration()//版本升级策略，删除旧库重建
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}