package com.example.xiaoyi.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

//发布商品/编辑商品
@Composable
fun ProductFormFields(
    title: String,
    onTitleChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    errorMessage: String = "",
    titleLabel: String = "标题",
    priceLabel: String = "价格",
    descriptionLabel: String = "描述"
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("$titleLabel*") },
        placeholder = { Text("请输入$titleLabel") },
        singleLine = true,//单行输入
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)//普通文字键盘
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = price,
        onValueChange = onPriceChange,
        label = { Text("$priceLabel（元）*") },
        placeholder = { Text("请输入$priceLabel") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        prefix = { Text("￥") }
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text(descriptionLabel) },
        placeholder = { Text("请详细描述相关信息") },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        maxLines = 6,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}