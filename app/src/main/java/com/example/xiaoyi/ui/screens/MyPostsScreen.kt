package com.example.xiaoyi.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.xiaoyi.model.Product
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import com.example.xiaoyi.ui.components.ProductCard
import com.example.xiaoyi.viewmodel.ProductViewModel

@Composable
fun MyPostsScreen(
    navController: NavController,
    userId: Long,
    viewModel: ProductViewModel = viewModel()
) {
    val userProducts by viewModel.userProducts.collectAsState()
    val isLoading by viewModel.isUserProductsLoading.collectAsState()
    val errorMessage by viewModel.userProductsErrorMessage.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedProductId by remember { mutableStateOf<Long>(0L) }

    LaunchedEffect(userId) {
        viewModel.loadUserProducts(userId)
    }

    fun deleteProduct(productId: Long) {
        viewModel.deleteProduct(productId)//调用ViewModel执行删除
        viewModel.loadUserProducts(userId)//重新加载用户商品列表
    }

    @Composable
    fun ProductCardWithActions(
        product: Product,
        onEdit: () -> Unit,
        onDelete: () -> Unit
    ) {
        Column {
            ProductCard(
                productName = product.name,
                price = product.price.toString(),
                location = "卖家: ${product.sellerName}",
                imageUrl = product.imageUrl,
                onClick = { }//卡片点击无响应有单独的编辑按钮
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onEdit) {
                    Text("编辑")
                }
                TextButton(onClick = onDelete) {
                    Text(
                        text = "删除",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    DetailScreenTemplate(
        navController = navController,
        title = "我的发布",
        isLoading = isLoading,
        errorMessage = errorMessage
    ) {
        if (userProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无发布商品",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            //垂直网格布局
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userProducts) { product ->
                    ProductCardWithActions(
                        product = product,
                        onEdit = {
                            navController.navigate(Screen.EditProduct.route.replace("{productId}", product.id.toString()))
                        },
                        onDelete = {
                            selectedProductId = product.id
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("确认删除")
            },
            text = {
                Text("确定要删除这个商品吗？删除后无法恢复。")
            },
            confirmButton = {//确认按钮
                TextButton(
                    onClick = {
                        deleteProduct(selectedProductId)
                        showDeleteDialog = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {//取消按钮
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}