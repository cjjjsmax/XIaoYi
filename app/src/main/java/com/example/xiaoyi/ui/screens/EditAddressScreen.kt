package com.example.xiaoyi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.xiaoyi.api.RetrofitClient
import com.example.xiaoyi.model.AddressRequest
import com.example.xiaoyi.model.Region
import com.example.xiaoyi.model.Result
import com.example.xiaoyi.ui.components.DetailScreenTemplate
import com.example.xiaoyi.ui.components.RegionSelectDialog
import com.example.xiaoyi.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditAddressScreen(
    navController: NavController,
    addressId: Long,
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

    // 存储地址原始数据
    var addressData by remember { mutableStateOf<Map<String, Any>?>(null) }

    val scope = rememberCoroutineScope()

    var showProvinceDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showDistrictDialog by remember { mutableStateOf(false) }
    var showStreetDialog by remember { mutableStateOf(false) }

    fun shouldShowNextLevel(region: Region?): Boolean {
        if (region == null) return false
        val municipalities = listOf("北京市", "上海市", "天津市", "重庆市")
        return !municipalities.contains(region.name)
    }

    fun loadProvinces() {
        RetrofitClient.addressApi.getRegions("0").enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onResponse(call: Call<Result<List<Map<String, Any>>>>, response: Response<Result<List<Map<String, Any>>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        provinces = (result.data ?: emptyList()).mapNotNull { item ->
                            Region(
                                id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null,
                                name = item["name"] as? String ?: return@mapNotNull null,
                                parentId = item["parentId"] as? String ?: "",
                                level = (item["level"] as? Number)?.toInt() ?: 0,
                                code = item["code"] as? String ?: ""
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun loadCities(parentId: String) {
        RetrofitClient.addressApi.getRegions(parentId).enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onResponse(call: Call<Result<List<Map<String, Any>>>>, response: Response<Result<List<Map<String, Any>>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        cities = (result.data ?: emptyList()).mapNotNull { item ->
                            Region(
                                id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null,
                                name = item["name"] as? String ?: return@mapNotNull null,
                                parentId = item["parentId"] as? String ?: "",
                                level = (item["level"] as? Number)?.toInt() ?: 0,
                                code = item["code"] as? String ?: ""
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun loadDistricts(parentId: String) {
        RetrofitClient.addressApi.getRegions(parentId).enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onResponse(call: Call<Result<List<Map<String, Any>>>>, response: Response<Result<List<Map<String, Any>>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        districts = (result.data ?: emptyList()).mapNotNull { item ->
                            Region(
                                id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null,
                                name = item["name"] as? String ?: return@mapNotNull null,
                                parentId = item["parentId"] as? String ?: "",
                                level = (item["level"] as? Number)?.toInt() ?: 0,
                                code = item["code"] as? String ?: ""
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun loadStreets(parentId: String) {
        RetrofitClient.addressApi.getRegions(parentId).enqueue(object : Callback<Result<List<Map<String, Any>>>> {
            override fun onResponse(call: Call<Result<List<Map<String, Any>>>>, response: Response<Result<List<Map<String, Any>>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        streets = (result.data ?: emptyList()).mapNotNull { item ->
                            Region(
                                id = (item["id"] as? Number)?.toLong() ?: return@mapNotNull null,
                                name = item["name"] as? String ?: return@mapNotNull null,
                                parentId = item["parentId"] as? String ?: "",
                                level = (item["level"] as? Number)?.toInt() ?: 0,
                                code = item["code"] as? String ?: ""
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Result<List<Map<String, Any>>>>, t: Throwable) {
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun findRegionByName(list: List<Region>, name: String): Region? {
        return list.find { it.name == name }
    }

    fun loadAddressDetail() {
        isLoading = true
        RetrofitClient.addressApi.getAddressById(addressId).enqueue(object : Callback<Result<Map<String, Any>>> {
            override fun onResponse(call: Call<Result<Map<String, Any>>>, response: Response<Result<Map<String, Any>>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess()) {
                        val address = result.data ?: emptyMap()
                        addressData = address
                        
                        receiverName = address["receiverName"] as? String ?: ""
                        detailAddress = address["detailAddress"] as? String ?: ""
                        isDefault = (address["isDefault"] as? Int ?: 0) == 1
                    } else {
                        errorMessage = result?.message ?: "获取地址失败"
                    }
                } else {
                    errorMessage = "网络错误: ${response.code()}"
                }
            }
            override fun onFailure(call: Call<Result<Map<String, Any>>>, t: Throwable) {
                isLoading = false
                errorMessage = "网络错误: ${t.message}"
            }
        })
    }

    fun submitAddress() {
        formErrorMessage = null

        if (receiverName.isEmpty()) {
            formErrorMessage = "请填写收件人姓名"
            return
        }
        if (selectedProvince == null) {
            formErrorMessage = "请选择省份"
            return
        }
        if (selectedCity == null) {
            formErrorMessage = "请选择城市"
            return
        }
        if (selectedDistrict == null) {
            formErrorMessage = "请选择区县"
            return
        }

        try {
            val cityValue = if (shouldShowNextLevel(selectedProvince)) {
                selectedCity?.name ?: ""
            } else {
                selectedProvince?.name ?: ""
            }

            val addressRequest = AddressRequest(
                userId = UserManager.currentUserId,
                receiverName = receiverName,
                province = selectedProvince?.name ?: "",
                city = cityValue,
                district = selectedDistrict?.name ?: "",
                street = selectedStreet?.name ?: "",
                detailAddress = detailAddress,
                isDefault = if (isDefault) 1 else 0
            )

            isLoading = true

            scope.launch {
                delay(15000)
                if (isLoading) {
                    isLoading = false
                    errorMessage = "请求超时，请重试"
                }
            }

            RetrofitClient.addressApi.updateAddress(addressId, addressRequest).enqueue(object : Callback<Result<String>> {
                override fun onResponse(call: Call<Result<String>>, response: Response<Result<String>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess()) {
                            navController.popBackStack()
                        } else {
                            val message = result?.message
                            errorMessage = if (message.isNullOrEmpty()) "修改失败" else message
                        }
                    } else {
                        errorMessage = "网络请求失败: ${response.code()}"
                    }
                }
                override fun onFailure(call: Call<Result<String>>, t: Throwable) {
                    isLoading = false
                    errorMessage = "网络错误: ${t.message}"
                }
            })
        } catch (e: Exception) {
            formErrorMessage = "提交失败: ${e.message}"
        }
    }

    LaunchedEffect(Unit) {
        loadProvinces()
    }

    LaunchedEffect(provinces) {
        if (provinces.isNotEmpty()) {
            loadAddressDetail()
        }
    }

    LaunchedEffect(addressData, provinces) {
        if (addressData != null && provinces.isNotEmpty()) {
            val provinceName = addressData!!["province"] as? String ?: ""
            selectedProvince = findRegionByName(provinces, provinceName)
            
            if (selectedProvince != null) {
                if (shouldShowNextLevel(selectedProvince)) {
                    loadCities(selectedProvince!!.code)
                } else {
                    selectedCity = selectedProvince
                    loadDistricts(selectedProvince!!.code)
                }
            }
        }
    }

    LaunchedEffect(cities, addressData) {
        if (cities.isNotEmpty() && addressData != null && selectedProvince != null) {
            val cityName = addressData!!["city"] as? String ?: ""
            selectedCity = findRegionByName(cities, cityName)
            if (selectedCity != null) {
                loadDistricts(selectedCity!!.code)
            }
        }
    }

    LaunchedEffect(districts, addressData) {
        if (districts.isNotEmpty() && addressData != null && selectedCity != null) {
            val districtName = addressData!!["district"] as? String ?: ""
            selectedDistrict = findRegionByName(districts, districtName)
            if (selectedDistrict != null) {
                loadStreets(selectedDistrict!!.code)
            }
        }
    }

    LaunchedEffect(streets, addressData) {
        if (streets.isNotEmpty() && addressData != null && selectedDistrict != null) {
            val streetName = addressData!!["street"] as? String ?: ""
            selectedStreet = findRegionByName(streets, streetName)
        }
    }

    DetailScreenTemplate(
        navController = navController,
        title = "编辑收货地址",
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
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedProvince?.name ?: "",
                        onValueChange = {},
                        label = { Text("省份") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showProvinceDialog = true })
                }

                if (selectedProvince != null && shouldShowNextLevel(selectedProvince)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedCity?.name ?: "",
                            onValueChange = {},
                            label = { Text("城市") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showCityDialog = true })
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
                        Box(modifier = Modifier.matchParentSize().clickable { showDistrictDialog = true })
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
                            Box(modifier = Modifier.matchParentSize().clickable { showStreetDialog = true })
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
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
                Text(if (isLoading) "保存中..." else "保存修改")
            }
        }
    }

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