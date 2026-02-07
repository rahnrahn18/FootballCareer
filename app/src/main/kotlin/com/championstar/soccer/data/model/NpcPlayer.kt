// app/src/main/kotlin/com/championstar/soccer/data/model/NpcPlayer.kt
package com.championstar.soccer.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NpcPlayer(
    @SerializedName("player_id")
    val id: Int,

    @SerializedName("player_name")
    val name: String,

    @SerializedName("player_nickname")
    val nickname: String?,

    @SerializedName("country")
    val country: String
) : Parcelable