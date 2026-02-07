package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Enum untuk mendefinisikan tipe buff yang bisa diberikan item
enum class BuffType {
    NONE,
    FINISHING,
    SPEED,
    DRIBBLING,
    STAMINA,
    TACKLING
}

@Parcelize
data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val buffType: BuffType,
    val buffValue: Int,
    val iconResourceName: String // Misal: "boot_starter_01"
) : Parcelable