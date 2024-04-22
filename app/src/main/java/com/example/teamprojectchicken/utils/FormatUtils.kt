package com.example.teamprojectchicken.utils

import java.text.DecimalFormat

object FormatUtils {
    private val decimalFormat = DecimalFormat("###-####-####")
    fun formatNumber(number:Int):String {
        return decimalFormat.format(number)
    }
}