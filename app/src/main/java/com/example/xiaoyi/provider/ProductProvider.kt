package com.example.xiaoyi.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.xiaoyi.data.database.AppDatabase
import com.example.xiaoyi.data.database.entity.ProductEntity

class ProductProvider : ContentProvider() {

    private val AUTHORITY = "com.example.xiaoyi.provider.products"
    private val PRODUCTS = 1
    private val PRODUCT_ID = 2
    
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "products", PRODUCTS)
        addURI(AUTHORITY, "products/#", PRODUCT_ID)
    }
    
    private lateinit var database: AppDatabase
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            PRODUCT_ID -> {
                val productId = uri.lastPathSegment?.toLong() ?: return 0
                database.productDao().deleteById(productId)
                1
            }
            else -> throw IllegalArgumentException("未知的URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            PRODUCTS -> "vnd.android.cursor.dir/vnd.com.example.xiaoyi.products"
            PRODUCT_ID -> "vnd.android.cursor.item/vnd.com.example.xiaoyi.products"
            else -> throw IllegalArgumentException("未知的URI: $uri")
        }
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        if (uriMatcher.match(uri) == PRODUCTS) {
            val product = ProductEntity(
                name = values?.getAsString("name") ?: "",
                price = values?.getAsDouble("price") ?: 0.0,
                description = values?.getAsString("description") ?: "",
                location = values?.getAsString("location") ?: "",
                imageUrl = values?.getAsString("imageUrl") ?: "",
                userId = values?.getAsLong("userId") ?: 0L,
                createdAt = values?.getAsString("createdAt") ?: ""
            )
            val id = database.productDao().insert(product)
            return Uri.parse("content://$AUTHORITY/products/$id")
        }
        throw IllegalArgumentException("未知的URI: $uri")
    }

    override fun onCreate(): Boolean {
        context?.let {
            database = AppDatabase.getInstance(it)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        val productDao = database.productDao()
        return when(uriMatcher.match(uri)) {
            PRODUCTS -> {
                val products = productDao.getAllProducts()
                createCursor(products)
            }
            PRODUCT_ID -> {
                val productId = uri.lastPathSegment?.toLong() ?: return null
                val product = productDao.getProductById(productId)
                createCursor(listOfNotNull(product))
            }
            else -> {
                throw IllegalArgumentException("未知的URI: $uri")
            }
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            PRODUCT_ID -> {
                val productId = uri.lastPathSegment?.toLong() ?: return 0
                val product = database.productDao().getProductById(productId)

                product?.let { existingProduct ->
                    val updatedProduct = existingProduct.copy(
                        name = values?.getAsString("name") ?: existingProduct.name,
                        price = values?.getAsDouble("price") ?: existingProduct.price,
                        description = values?.getAsString("description") ?: existingProduct.description
                    )
                    database.productDao().update(updatedProduct)
                    1
                } ?: 0
            }
            else -> throw IllegalArgumentException("未知的URI: $uri")
        }
    }
    private fun createCursor(products: List<ProductEntity>): Cursor {
        val cursor = MatrixCursor(arrayOf(
            "id", "name", "price", "description",
            "location", "imageUrl", "userId", "createdAt"
        ))

        products.forEach { product ->
            cursor.addRow(arrayOf(
                product.id,
                product.name,
                product.price,
                product.description,
                product.location,
                product.imageUrl,
                product.userId,
                product.createdAt
            ))
        }

        return cursor
    }
}