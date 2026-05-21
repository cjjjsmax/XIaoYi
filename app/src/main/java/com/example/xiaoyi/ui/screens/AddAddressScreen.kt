package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.AddressRequest
import com.example.xiaoyi.model.Region
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import com.example.xiaoyi.ui.components.RegionSelectDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddAddressScreen(
    navController: NavController,
    userId: Long,
) {
    var receiverName by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    var selectedProvince by remember { mutableStateOf<Region?>(null) }
    var selectedCity by remember { mutableStateOf<Region?>(null) }
    var selectedDistrict by remember { mutableStateOf<Region?>(null) }
    var selectedStreet by remember { mutableStateOf<Region?>(null) }

    var provinces by remember { mutableStateOf<List<Region>>(emptyList()) }
    var cities by remember { mutableStateOf<List<Region>>(emptyList()) }
    var districts by remember { mutableStateOf<List<Region>>(emptyList()) }
    var streets by remember { mutableStateOf<List<Region>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var formErrorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    var showProvinceDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showDistrictDialog by remember { mutableStateOf(false) }
    var showStreetDialog by remember { mutableStateOf(false) }

    // 判断是否需要显示下级地区（直辖市和部分自治区不需要）
    fun shouldShowNextLevel(region: Region?): Boolean {
        if (region == null) return false
        // 直辖市：北京、上海、天津、重庆
        // 直辖市既是省也是市，不需要显示下级城市
        val municipalities = listOf("北京市", "上海市", "天津市", "重庆市")
        return !municipalities.contains(region.name)
    }

    fun loadProvinces() {
        isLoading = true
        RetrofitClient.addressApi.getRegions("0").enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val dataObj = responseBody["data"]
                        if (dataObj is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val regionList = dataObj as List<Map<String, Any>>
                            provinces = regionList.mapNotNull { item ->
                                // 后端返回的字段名是 id, name, code, parentId, level
                                val id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null
                                val name = item["name"] as? String ?: return@mapNotNull null
                                val code = item["code"] as? String ?: ""
                                val parentId = item["parentId"] as? String ?: ""
                                val level = (item["level"] as? Number)?.toInt() ?: 0
                                Region(id, name, parentId, level, code)
                            }
                        } else {
                            errorMessage = "数据格式错误"
                        }
                    } else {
                        errorMessage = "网络请求失败"
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun loadCities(parentId: String) {
        isLoading = true
        RetrofitClient.addressApi.getRegions(parentId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val dataObj = responseBody["data"]
                        if (dataObj is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val regionList = dataObj as List<Map<String, Any>>
                            cities = regionList.mapNotNull { item ->
                                val id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null
                                val name = item["name"] as? String ?: return@mapNotNull null
                                val code = item["code"] as? String ?: ""
                                val parentId = item["parentId"] as? String ?: ""
                                val level = (item["level"] as? Number)?.toInt() ?: 0
                                Region(id, name, parentId, level, code)
                            }
                        }
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun loadDistricts(parentId: String) {
        isLoading = true
        RetrofitClient.addressApi.getRegions(parentId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val dataObj = responseBody["data"]
                        if (dataObj is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val regionList = dataObj as List<Map<String, Any>>
                            districts = regionList.mapNotNull { item ->
                                val id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null
                                val name = item["name"] as? String ?: return@mapNotNull null
                                val code = item["code"] as? String ?: ""
                                val parentId = item["parentId"] as? String ?: ""
                                val level = (item["level"] as? Number)?.toInt() ?: 0
                                Region(id, name, parentId, level, code)
                            }
                        }
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun loadStreets(parentId: String) {
        isLoading = true
        RetrofitClient.addressApi.getRegions(parentId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val dataObj = responseBody["data"]
                        if (dataObj is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val regionList = dataObj as List<Map<String, Any>>
                            streets = regionList.mapNotNull { item ->
                                val id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null
                                val name = item["name"] as? String ?: return@mapNotNull null
                                val code = item["code"] as? String ?: ""
                                val parentId = item["parentId"] as? String ?: ""
                                val level = (item["level"] as? Number)?.toInt() ?: 0
                                Region(id, name, parentId, level, code)
                            }
                        }
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun submitAddress() {
        // 清除之前的表单错误
        formErrorMessage = null

        if (receiverName.isEmpty()) {
            formErrorMessage = "请填写收件人姓名"
            return
        }
        if (selectedProvince == null) {
            formErrorMessage = "请选择省份"
            return
        }
        // 城市验证：直辖市情况下 selectedCity 可能等于 selectedProvince
        if (selectedCity == null) {
            formErrorMessage = "请选择城市"
            return
        }
        if (selectedDistrict == null) {
            formErrorMessage = "请选择区县"
            return
        }

        try {
            // 根据是否为直辖市/自治区来决定 city 的值
            val cityValue = if (shouldShowNextLevel(selectedProvince)) {
                selectedCity?.name ?: ""
            } else {
                selectedProvince?.name ?: ""
            }

            val addressRequest = AddressRequest(
                userId = userId,
                receiverName = receiverName,
                province = selectedProvince?.name ?: "",
                city = cityValue,
                district = selectedDistrict?.name ?: "",
                street = selectedStreet?.name ?: "",
                detailAddress = detailAddress,
                isDefault = if (isDefault) 1 else 0
            )

            // 打印发送的数据用于调试
            android.util.Log.d("AddAddress", "Sending address data: $addressRequest")

            isLoading = true
            
            // 添加超时处理
            scope.launch {
                delay(15000) // 15秒超时
                if (isLoading) {
                    isLoading = false
                    errorMessage = "请求超时，请重试"
                }
            }
            
            // 打印即将发送请求的日志
            android.util.Log.d("AddAddress", "About to call createAddress API")
            
            RetrofitClient.addressApi.createAddress(addressRequest).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    android.util.Log.d("AddAddress", "onResponse called, isSuccessful: ${response.isSuccessful}, code: ${response.code()}")
                    isLoading = false
                    if (response.isSuccessful) {
                        val data = response.body()
                        android.util.Log.d("AddAddress", "Response body: $data")
                        if (data != null && data.containsKey("success") && data["success"] == true) {
                            android.util.Log.d("AddAddress", "Address added successfully, navigating back")
                            navController.popBackStack()
                        } else {
                            val message = data?.get("message")?.toString()
                            errorMessage = if (message.isNullOrEmpty()) "添加失败" else message
                            android.util.Log.d("AddAddress", "Add failed: $errorMessage")
                        }
                    } else {
                        errorMessage = "网络请求失败: ${response.code()}"
                        android.util.Log.d("AddAddress", "Request failed with code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    android.util.Log.e("AddAddress", "onFailure called: ${t.message}", t)
                    isLoading = false
                    errorMessage = "网络错误: ${t.message}"
                }
            })
        } catch (e: Exception) {
            android.util.Log.e("AddAddress", "Exception in submitAddress: ${e.message}", e)
            formErrorMessage = "提交失败: ${e.message}"
        }
    }

    LaunchedEffect(Unit) {
        loadProvinces()
    }

    DetailScreenTemplate(
        navController = navController,
        title = "添加收货地址",
        isLoading = isLoading,
        errorMessage = errorMessage
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = receiverName,
                onValueChange = { receiverName = it },
                label = { Text("收件人姓名") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedProvince?.name ?: "",
                        onValueChange = {},
                        label = { Text("省份") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                    // 在输入框上层添加一个透明的可点击层
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showProvinceDialog = true }
                    )
                }

                // 只有当不是直辖市/自治区时才显示城市选择
                if (selectedProvince != null && shouldShowNextLevel(selectedProvince)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedCity?.name ?: "",
                            onValueChange = {},
                            label = { Text("城市") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showCityDialog = true }
                        )
                    }
                }
            }

            if (selectedCity != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedDistrict?.name ?: "",
                            onValueChange = {},
                            label = { Text("区县") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDistrictDialog = true }
                        )
                    }

                    if (selectedDistrict != null) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = selectedStreet?.name ?: "",
                                onValueChange = {},
                                label = { Text("街道") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showStreetDialog = true }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = detailAddress,
                onValueChange = { detailAddress = it },
                label = { Text("详细地址") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it }
                )
                Text("设为默认地址")
            }

            if (formErrorMessage != null) {
                Text(
                    text = formErrorMessage!!,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Button(
                onClick = { submitAddress() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "保存中..." else "保存地址")
            }
        }
    }

    // 弹窗必须放在布局顶层
    if (showProvinceDialog) {
        RegionSelectDialog(
            title = "选择省份",
            regions = provinces,
            selectedRegion = selectedProvince,
            onSelect = { region ->
                selectedProvince = region
                selectedCity = null
                selectedDistrict = null
                selectedStreet = null
                cities = emptyList()
                districts = emptyList()
                streets = emptyList()

                if (shouldShowNextLevel(region)) {
                    loadCities(region.code)
                } else {
                    selectedCity = region
                    loadDistricts(region.code)
                }
                
                showProvinceDialog = false
            },
            onDismiss = { showProvinceDialog = false }
        )
    }

    if (showCityDialog) {
        RegionSelectDialog(
            title = "选择城市",
            regions = cities,
            selectedRegion = selectedCity,
            onSelect = { region ->
                selectedCity = region
                selectedDistrict = null
                selectedStreet = null
                districts = emptyList()
                streets = emptyList()
                loadDistricts(region.code)
                showCityDialog = false
            },
            onDismiss = { showCityDialog = false }
        )
    }

    if (showDistrictDialog) {
        RegionSelectDialog(
            title = "选择区县",
            regions = districts,
            selectedRegion = selectedDistrict,
            onSelect = { region ->
                selectedDistrict = region
                selectedStreet = null
                streets = emptyList()
                loadStreets(region.code)
                showDistrictDialog = false
            },
            onDismiss = { showDistrictDialog = false }
        )
    }

    if (showStreetDialog) {
        RegionSelectDialog(
            title = "选择街道",
            regions = streets,
            selectedRegion = selectedStreet,
            onSelect = { region ->
                selectedStreet = region
                showStreetDialog = false
            },
            onDismiss = { showStreetDialog = false }
        )
    }
}
