package com.example.teamprojectchicken.utils

import java.time.LocalDate

object FormatUtils {
    fun formatNumber(number:Int):String {
        val formatted = "%010d".format(number)
        return "0${formatted.substring(0,2)}-${formatted.substring(2,6)}-${formatted.substring(6)}"
    }

    fun returnAge(birthDate: Int): String {
        val currentDate = LocalDate.now()
        val currentYear = currentDate.year
        val currentMonth = currentDate.monthValue
        val currentDay = currentDate.dayOfMonth

        val birthYear = birthDate / 10000
        val birthMonth = (birthDate % 10000) / 100
        val birthDay = birthDate % 100

        var age = currentYear - birthYear

        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--
        }

        return "${age} years young"
    }

    fun checkPhoneNumber(number:String): Int{
        val input = number.drop(1)
        val formatted = input.replace("-","")
        if (!formatted.all{it.isDigit()}) {
            return -1
        }
        if (formatted.length != 10) {
            return -1
        }
        return formatted.toInt()
    }



}