package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.xiaoyi.model.Product
import com.example.xiaoyi.model.Wanted
import com.example.xiaoyi.navigation.Screen
import com.example.xiaoyi.ui.components.ProductCard
import com.example.xiaoyi.ui.components.WantedCard
import com.example.xiaoyi.viewmodel.ProductViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private data class ProductCategory(val id: Int, val name: String)

private val productCategories = listOf(
    ProductCategory(0, "全部"),
    ProductCategory(1, "数码产品"),
    ProductCategory(2, "服装鞋包"),
    ProductCategory(3, "书籍文具"),
    ProductCategory(4, "运动户外"),
    ProductCategory(5, "其他")
)

@Composable
fun ProductListScreen(
    navController: NavController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    viewModel: ProductViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val listTabs = listOf("商品列表", "求购列表")
    val products by viewModel.products.collectAsState()
    val isProductsLoading by viewModel.isProductsLoading.collectAsState()
    val productsErrorMessage by viewModel.productsErrorMessage.collectAsState()
    val wantedList by viewModel.wantedList.collectAsState()
    val isWantedLoading by viewModel.isWantedLoading.collectAsState()
    val wantedErrorMessage by viewModel.wantedErrorMessage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var wantedSearchQuery by remember { mutableStateOf("") }
    var selectedCategory: ProductCategory by remember { mutableStateOf(productCategories[0]) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
        viewModel.loadPurchaseRequests()
    }

    fun onSearch() {
        if (selectedTabIndex == 0) {
            // 商品列表：按按钮搜索
            if (searchQuery.isEmpty() && selectedCategory.id == 0) {
                viewModel.loadProducts()
            } else {
                viewModel.searchProducts(searchQuery, selectedCategory.id)
            }
        }
    }

    fun onWantedSearch() {
        if (selectedTabIndex == 1) {
            viewModel.loadPurchaseRequests(wantedSearchQuery, selectedCategory.id)
        }
    }

    fun onCategoryChange(category: ProductCategory) {
        selectedCategory = category
        expanded = false
        if (selectedTabIndex == 0) {
            if (searchQuery.isEmpty()) {
                if (category.id == 0) {
                    viewModel.loadProducts()
                } else {
                    viewModel.loadProductsByCategory(category.id)
                }
            } else {
                viewModel.searchProducts(searchQuery, category.id)
            }
        } else {
            viewModel.loadPurchaseRequests(wantedSearchQuery, category.id)
        }
    }

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(paddingValues)
        ) {
            // 商品列表：搜索框 + 分类下拉框
            if (selectedTabIndex == 0) {
                Box {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("搜索商品") },
                        leadingIcon = {
                            Row(
                                modifier = Modifier
                                    .clickable { expanded = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedCategory.name, fontSize = 14.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "下拉", modifier = Modifier.size(16.dp))
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { onSearch() }) {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        productCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onCategoryChange(category)
                                }
                            )
                        }
                    }
                }
            }

            // 求购列表：搜索框 + 分类下拉框
            if (selectedTabIndex == 1) {
                Box {
                    TextField(
                        value = wantedSearchQuery,
                        onValueChange = { wantedSearchQuery = it },
                        placeholder = { Text("搜索求购") },
                        leadingIcon = {
                            Row(
                                modifier = Modifier
                                    .clickable { expanded = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedCategory.name, fontSize = 14.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "下拉", modifier = Modifier.size(16.dp))
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { onWantedSearch() }) {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        productCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onCategoryChange(category)
                                }
                            )
                        }
                    }
                }
            }

            // 标签页切换
            TabRow(selectedTabIndex = selectedTabIndex) {
                listTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // 内容区域
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> {
                        ProductListContent(
                            products = products,
                            isLoading = isProductsLoading,
                            isLoadingMore = viewModel.isLoadingMore.collectAsState().value,
                            hasMoreData = viewModel.hasMoreData.collectAsState().value,
                            errorMessage = productsErrorMessage,
                            onRetry = {
                                if (searchQuery.isEmpty() && selectedCategory.id == 0) {
                                    viewModel.loadProducts(isRefresh = true)
                                } else {
                                    viewModel.searchProducts(searchQuery, selectedCategory.id,isRefresh = true)
                                }
                            },
                            onLoadMore = { viewModel.loadMoreProducts() },
                            onProductClick = { productId ->
                                navController.navigate(
                                    Screen.ProductDetail.route.replace("{productId}", productId)
                                )
                            }
                        )
                    }
                    1 -> {
                        WantedListContent(
                            wantedList = wantedList,
                            isLoading = isWantedLoading,
                            errorMessage = wantedErrorMessage,
                            onRetry = { viewModel.loadPurchaseRequests(wantedSearchQuery, selectedCategory.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListContent(
    products: List<Product>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMoreData: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState, products, hasMoreData, isLoadingMore) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            if (lastVisibleIndex != null && lastVisibleIndex == products.size - 1 && hasMoreData && !isLoadingMore) {
                onLoadMore()
            }
        }
    }
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = onRetry
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null && products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(onClick = onRetry) {
                            Text("重试")
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(products.size) { index ->
                        val product = products[index]
                        ProductCard(
                            productName = product.name,
                            price = product.price.toString(),
                            location = "卖家: ${product.sellerName}",
                            imageUrl = product.imageUrl,
                            onClick = { onProductClick(product.id.toString()) }
                        )
                    }
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    if (!hasMoreData && products.isNotEmpty()) {
                        item {
                            Text(
                                text = "没有更多商品了",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WantedListContent(
    wantedList: List<Wanted>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = onRetry
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && wantedList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null && wantedList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(onClick = onRetry) {
                            Text("重试")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(wantedList.size) { index ->
                        val wanted = wantedList[index]
                        WantedCard(
                            title = wanted.title,
                            maxPrice = wanted.maxPrice.toString(),
                            description = wanted.description,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}