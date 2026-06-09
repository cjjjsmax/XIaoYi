package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.Result
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddressManagementScreen(
    navController: NavController,
    userId: Long
){
    var addressesState by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadAddresses(){
        isLoading = true
        RetrofitClient.addressApi.getAddresses(userId).enqueue(object : Callback<Result<List<Map<String, Any>>>>{
            override fun onResponse(
                call: Call<Result<List<Map<String, Any>>>>,
                response: Response<Result<List<Map<String, Any>>>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        addressesState = result.data ?: emptyList()
                    } else {
                        errorMessage = result?.message ?: "加载失败"
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }
    LaunchedEffect(Unit) {
        loadAddresses()
    }

    DetailScreenTemplate(
        navController = navController,
        title = "收/发货地址",
        isLoading = isLoading,
        errorMessage =errorMessage
    ) {
        if (addressesState.isEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {navController.navigate("add_address/$userId")},
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("添加收/发货地址")
                }
            }
        }else{
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(addressesState){
                        address ->
                    AddressItem(
                        address = address,
                        onEdit = {
                            val addressId = (address["id"] as? Number)?.toLong() ?: 0L
                            navController.navigate("edit_address/${addressId}")
                        },
                        onDelete = {},
                        onRefresh = {loadAddresses()}
                    )
                    }
                }
                
                FloatingActionButton(
                    onClick = { navController.navigate("add_address/$userId") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加地址")
                }
            }
        }
    }
}

@Composable
fun AddressItem(
    address: Map<String, Any>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefresh: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val province = address["province"]?.toString() ?: ""
            val city = address["city"]?.toString() ?: ""
            val district = address["district"]?.toString() ?: ""
            Text(
                text = "$province $city $district",
                fontSize = 14.sp,
                color = Color.Gray
            )

            val detailAddress = address["detailAddress"]?.toString() ?:""

            Text(
                text = detailAddress,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            val receiverName = address["receiverName"]?.toString() ?: ""
            val phone = address["phone"]?.toString() ?: ""
            val isDefault = address["isDefault"] as? Int ?: 0

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = receiverName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = phone, color = Color.Gray)

                if (isDefault == 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "默认",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("编辑") }
                TextButton(onClick = {
                    val addressId = (address["id"] as? Number)?.toLong() ?: 0L
                    if (addressId > 0) {
                        RetrofitClient.addressApi.deleteAddress(addressId).enqueue(object : Callback<Result<String>> {
                            override fun onResponse(
                                call: Call<Result<String>>,
                                response: Response<Result<String>>
                            ) {
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    if (result == null || result.isSuccess()) {
                                        onRefresh()
                                    }
                                }
                            }
                            override fun onFailure(
                                call: Call<Result<String>>,
                                t: Throwable
                            ) {

                            }
                        })
                    }
                }) { Text("删除") }
            }
        }
    }
}