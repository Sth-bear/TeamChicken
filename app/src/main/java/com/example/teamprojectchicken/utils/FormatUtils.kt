package com.example.teamprojectchicken.utils

import android.view.View
import com.example.teamprojectchicken.activities.ContactListFragment
import com.example.teamprojectchicken.data.Contact
import com.google.android.material.snackbar.Snackbar
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
        val formatted2 = formatted.replace(".", "")
        if (!formatted2.all{it.isDigit()}) {
            return -1
        }
        if (formatted2.length != 10) {
            return -1
        }
        return formatted2.toInt()
    }

    fun checkDate(date:String): Int{
        val delDash = date.replace("-","")
        val formatted = delDash.replace(".","")
        if (!formatted.all { it.isDigit() }) {
            return -1
        }
        if (formatted.length != 8) {
            return -1
        }
        return formatted.toInt()
    }

    fun formatDate(date:Int):String {
        val formatted = date.toString()
        return "${formatted.substring(0,4)}.${formatted.substring(4,6)}.${formatted.substring(6)}"
    }

    fun checkFormat(view : View, vararg formatted: Int):Boolean {
        formatted.forEach {
            if (it == -1) {
                showSnackBar(view,"정확한 값을 입력해주세요")
                return true
            }
        }
        return false
    }

     fun showSnackBar(view: View, text:String) {
        val snackbar = Snackbar.make(view,"${text}",Snackbar.LENGTH_SHORT)
        snackbar.setAction("확인") {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    fun searchFilter(text : String?): ArrayList<Contact> {
        val searchText = text?.replace("-","")
        val filteredList = ArrayList<Contact>()
        for(item in ContactListFragment.list) {
            if(item.name.contains(searchText?:"") || "0${item.number}".contains(searchText?:"")) {
                filteredList.add(item)
            }
        }
        return filteredList
    }
}