package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContractOffer(
    val clubId: Int,
    val leagueId: Int, // Kita butuh ini untuk tahu di liga mana kita dikontrak
    val weeklyWage: Double,
    val contractLengthYears: Int,
    val role: String // Misal: "Pemain Cadangan", "Pemain Inti"
) : Parcelable