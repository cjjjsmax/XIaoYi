package com.example.xiaoyi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.xiaoyi.model.Region

//地区选择
@Composable
fun RegionSelectDialog(
    title: String,
    regions: List<Region>,
    selectedRegion: Region?,
    onSelect: (Region) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,//点击外部关闭
        title = { Text(title) },
        text = {
            if (regions.isEmpty()) {
                Text("暂无数据")
            } else {
                //滚动列表
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(regions) { region ->
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(region) },
                            headlineContent = { Text(region.name) },//显示地区名称
                            // 高亮选中项
                            trailingContent = {
                                if (selectedRegion?.id == region.id) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "已选中",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}