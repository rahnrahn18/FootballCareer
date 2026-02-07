package com.championstar.soccer.data.repository

import com.championstar.soccer.data.model.BrandType
import com.championstar.soccer.data.model.Sponsor

object SponsorRepository {

    private val allSponsors = listOf(
        Sponsor("sp_001", "Kinetik Footwear", BrandType.SPORTSWEAR, 50.0, 5),
        Sponsor("sp_002", "Volt-Up Energy", BrandType.ENERGY_DRINK, 75.0, 10),
        Sponsor("sp_003", "Apex Sports", BrandType.SPORTSWEAR, 250.0, 25),
        Sponsor("sp_004", "Elegance Timers", BrandType.LUXURY_WATCH, 1000.0, 50),
        Sponsor("sp_005", "Momentum Motors", BrandType.AUTOMOTIVE, 5000.0, 75)
    )

    // Fungsi untuk mencari sponsor yang cocok berdasarkan reputasi pemain
    fun findPotentialSponsors(playerReputation: Int, existingDealIds: List<String>): List<Sponsor> {
        return allSponsors.filter { sponsor ->
            playerReputation >= sponsor.reputationRequired && sponsor.id !in existingDealIds
        }
    }
}