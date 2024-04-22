package com.example.teamprojectchicken.utils

import java.text.DecimalFormat

object FormatUtils {
    fun formatNumber(number:Int):String {
        val formatted = "%010d".format(number)
        return "0${formatted.substring(0,2)}-${formatted.substring(2,6)}-${formatted.substring(6)}"
    }
}