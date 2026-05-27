package com.example.xiaoyi.data.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.xiaoyi.data.database.dao.ProductDao
import com.example.xiaoyi.data.database.dao.UserDao
import com.example.xiaoyi.data.database.entity.ProductEntity
import com.example.xiaoyi.data.database.entity.UserEntity


@Database(
    entities = [ProductEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {


    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xiaoyi_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}