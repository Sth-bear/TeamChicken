package com.example.teamprojectchicken.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class ContactViewModel(): ViewModel() {
    private var data = MutableLiveData<Boolean>()
    val liveData : LiveData<Boolean>
        get() = data

    fun getData(): Boolean? {
        return liveData.value
    }

    fun setData(input:Boolean){
        data.value = input
    }
}