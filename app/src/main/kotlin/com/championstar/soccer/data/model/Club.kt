package com.championstar.soccer.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Club(
    @SerializedName("club_id")
    val id: Int,

    @SerializedName("club_name")
    val name: String,

    // KEMBALIKAN TIER DENGAN NILAI DEFAULT
    val tier: ClubTier = ClubTier.PROFESSIONAL,

    val reputation: Int = 50,

    @SerializedName("players")
    val players: List<NpcPlayer> = emptyList()

) : Parcelable {

    val isUnattached: Boolean get() = id == 0

    companion object {
        fun unattached(): Club = Club(
            id = 0,
            name = "Unattached",
            tier = ClubTier.AMATEUR // Klub "tanpa klub" adalah amatir
        )
    }
}