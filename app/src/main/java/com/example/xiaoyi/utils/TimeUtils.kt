package com.example.xiaoyi.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    private val monthFormatter = SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault())
    private val dayFormatter = SimpleDateFormat("MM月dd日", Locale.getDefault())
    private val yearFormatter = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())

    /**
     * 格式化会话列表中的时间
     */
    fun formatConversationTime(timeString: String): String {
        return try {
            val date = dateFormatter.parse(timeString) ?: return timeString
            val now = Date()
            val diff = now.time - date.time
            
            // 小于1小时显示分钟
            if (diff < 60 * 60 * 1000) {
                val minutes = (diff / (60 * 1000)).toInt()
                return if (minutes == 0) "刚刚" else "${minutes}分钟前"
            }
            // 今天显示时间
            else if (diff < 24 * 60 * 60 * 1000) {
                return timeFormatter.format(date)
            }
            // 昨天显示"昨天"
            else if (diff < 48 * 60 * 60 * 1000) {
                return "昨天"
            }
            // 一周内显示月日
            else if (diff < 7 * 24 * 60 * 60 * 1000) {
                return dayFormatter.format(date)
            }
            // 一年内显示月日
            else if (diff < 365 * 24 * 60 * 60 * 1000) {
                return monthFormatter.format(date)
            }
            // 超过一年显示年月日
            else {
                return yearFormatter.format(date)
            }
        } catch (e: Exception) {
            timeString
        }
    }
}