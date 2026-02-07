package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model data utama yang merepresentasikan pemain di dalam game.
 * Ini menggabungkan profil statis, atribut dinamis, dan status game saat ini.
 */
@Parcelize
data class Player(
    val profile: PlayerProfile,
    var attributes: PlayerAttributes,
    var club: Club = Club.unattached(),
    var energy: Int = 100,
    var cash: Double = 500.0,
    var activeSponsorships: MutableList<SponsorshipDeal> = mutableListOf(),
    var ownedBootIds: MutableSet<String> = mutableSetOf(),
    var equippedBootId: String? = null,
    var currentContract: ContractOffer? = null,
    var careerStats: PlayerCareerStats = PlayerCareerStats(),

    // *** PROPERTI BARU UNTUK MENYIMPAN FASE KARIER ***
    var careerState: PlayerCareerState = PlayerCareerState.UNATTACHED_NO_AGENT

) : Parcelable