package com.example.teamprojectchicken.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource

class ContactViewModel(val dataSource: DataSource): ViewModel() {
    private var liveData = MutableLiveData<Contact>()


    val fragmentValue: LiveData<Contact>
        get() = liveData
    fun getData(): MutableLiveData<Contact> = liveData


}
//viewModel 생성
class ContactViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            return ContactViewModel(dataSource = DataSource.getDataSource()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}