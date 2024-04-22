package com.example.teamprojectchicken.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val name: String,
    val number: Int
) : Parcelable
