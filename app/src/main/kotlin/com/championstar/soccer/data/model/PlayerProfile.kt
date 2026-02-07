package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerProfile(
    val name: String,
    val country: String,
    val position: String,
    val avatarResourceName: String
) : Parcelable