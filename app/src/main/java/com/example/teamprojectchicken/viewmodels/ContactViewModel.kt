package com.example.teamprojectchicken.viewmodels

import android.util.MutableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teamprojectchicken.data.Contact

class ContactViewModel(): ViewModel() {
    private var data = MutableLiveData<Boolean>()
    private var viewType = MutableLiveData(1)
    private var contactList = MutableLiveData<MutableList<Contact>>()
    val liveData: LiveData<Boolean>
        get() = data

    val _viewType: LiveData<Int>
        get() = viewType

    val _contactList: LiveData<MutableList<Contact>>
        get() = contactList

    fun getData(): Boolean? {
        return liveData.value
    }

    fun setData(input: Boolean) {
        data.value = input
    }

    fun getType(): Int {
        return _viewType.value!!
    }

    fun setType() {
        if (viewType.value == 1) {
            viewType.value = 2
        } else {
            viewType.value = 1
        }
    }

    fun getContact(): MutableList<Contact> {
        return _contactList.value!!
    }

    fun setContact(list: MutableList<Contact>) {
        contactList.value = list
    }
}

