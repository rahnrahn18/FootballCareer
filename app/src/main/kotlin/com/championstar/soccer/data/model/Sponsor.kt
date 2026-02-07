package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class BrandType {
    SPORTSWEAR,
    ENERGY_DRINK,
    LUXURY_WATCH,
    AUTOMOTIVE
}

@Parcelize
data class Sponsor(
    val id: String,
    val name: String,
    val brandType: BrandType,
    val baseWeeklyPayout: Double,
    val reputationRequired: Int // Pemain harus mencapai reputasi ini untuk ditawari
) : Parcelable

// Ini adalah kontrak aktif yang dimiliki pemain
@Parcelize
data class SponsorshipDeal(
    val sponsorId: String,
    val weeklyPayout: Double,
    val durationInWeeks: Int,
    var weeksRemaining: Int
) : Parcelable