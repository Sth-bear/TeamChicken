package com.example.teamprojectchicken.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teamprojectchicken.utils.FormatUtils.VIEW_TYPE_GRID
import com.example.teamprojectchicken.utils.FormatUtils.VIEW_TYPE_LINEAR

class ContactViewModel(): ViewModel() {
    private var data = MutableLiveData<Boolean>()
    private var viewType = MutableLiveData(1)
    val liveData: LiveData<Boolean>
        get() = data

    val _viewType: LiveData<Int>
        get() = viewType

    fun setData(input: Boolean) {
        data.value = input
    }

    fun getType(): Int {
        return _viewType.value!!
    }

    fun setType() {
        if (viewType.value == VIEW_TYPE_LINEAR) {
            viewType.value = VIEW_TYPE_GRID
        } else {
            viewType.value = VIEW_TYPE_LINEAR
        }
    }

}



