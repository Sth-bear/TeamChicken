package com.example.teamprojectchicken.data

import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    var name: String,
    var number: Int,
    var email: String,
    var date: Int,
    @DrawableRes
    var userImage: Int,
    var heart: Boolean
) : Parcelable
