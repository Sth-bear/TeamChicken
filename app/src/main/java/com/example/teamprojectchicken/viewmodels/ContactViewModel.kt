package com.example.teamprojectchicken.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import java.io.DataInput

class ContactViewModel(): ViewModel() {
    private var data = MutableLiveData<Boolean>()
    val liveData : LiveData<Boolean>
        get() = data

    fun getData(boolean: Boolean) {
        data.value = boolean
    }

    fun setData(input:Boolean){
        data.value = input
    }
}