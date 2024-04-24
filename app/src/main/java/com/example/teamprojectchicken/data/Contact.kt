package com.example.teamprojectchicken.data

import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    var name: String,
    val number: Int,
    val email: String,
    val date: Int,
    @DrawableRes
    var userImage: Int,
    var heart: Boolean
) : Parcelable
