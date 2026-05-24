package com.example.xiaoyi.data.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.xiaoyi.data.database.entity.ProductEntity


@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun getAllProducts(): List<ProductEntity>


    @Query("SELECT * FROM product WHERE id = :id")
    fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM product ORDER BY id DESC")
    fun getAllProductsOrderByIdDesc(): List<ProductEntity>

    @Query("SELECT * FROM product WHERE userId = :userId")
    fun getProductsByUserId(userId: Long): List<ProductEntity>

    @Insert
    fun insert(product: ProductEntity): Long


    @Update
    fun update(product: ProductEntity)


    @Query("DELETE FROM product WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM product")
    fun deleteAll()
}