package com.manifestasi.facechat.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeStamp {
    fun convertToHour(timestamp: Number): String {
        val timestampLong = timestamp.toLong()
        val date = Date(timestampLong)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        return sdf.format(date)
    }
}