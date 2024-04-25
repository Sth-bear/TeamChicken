package com.example.teamprojectchicken.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource


class ConViewModel(private val dataSource: DataSource): ViewModel() {
    private val _contactLiveData = MutableLiveData(dataSource.getContactList())
    val contactLiveData : LiveData<MutableList<Contact>> get() = _contactLiveData

    fun addContact(contact: Contact) {
        val currentList:MutableList<Contact> = contactLiveData.value?: mutableListOf()
        currentList.add(contact)
        currentList.sortBy { it.name }
        _contactLiveData.value = currentList
    }

    fun removeContact(position:Int) {
        val currentList = contactLiveData.value
        currentList?.removeAt(position)
        _contactLiveData.value = currentList
    }

    fun changeContact(contact: Contact) {
        val currentList = contactLiveData.value?: mutableListOf()
        val index = contactLiveData.value?.indexOfFirst { it.number == contact.number }
        if (index != null) {
            currentList[index] = contact
            _contactLiveData.value = currentList
        }
    }

    fun pushLike(name: String) {
        val currentList = contactLiveData.value?: mutableListOf()
        val index = contactLiveData.value?.indexOfFirst { it.name ==  name}?:-1
        val currentHeart = currentList[index].heart
        _contactLiveData.value?.get(index)?.heart = !currentHeart
    }


}

class ContactViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ConViewModel::class.java)) {
            return ConViewModel(dataSource = DataSource.getDataSource()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}