package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Kelas utama yang menampung semua pilar atribut
@Parcelize
data class PlayerAttributes(
    var technical: TechnicalSkills = TechnicalSkills(),
    var physical: PhysicalAttributes = PhysicalAttributes(),
    var mental: MentalAttributes = MentalAttributes(),
    var personal: CareerAttributes = CareerAttributes()
) : Parcelable

// 1. KETERAMPILAN TEKNIS
@Parcelize
data class TechnicalSkills(
    // Menyerang
    var finishing: Int = 5,
    var longShots: Int = 5,
    var headingAccuracy: Int = 5,
    var shotPower: Int = 5,
    var volleys: Int = 5,
    // Umpan
    var shortPassing: Int = 5,
    var longPassing: Int = 5,
    var crossing: Int = 5,
    // Dribbling
    var dribbling: Int = 5,
    var ballControl: Int = 5,
    var agility: Int = 5,
    // Bertahan
    var tackling: Int = 5,
    var interceptions: Int = 5,
    var marking: Int = 5,
    // Bola Mati
    var freeKickAccuracy: Int = 5,
    var penalties: Int = 5,
    var corners: Int = 5
) : Parcelable

// 2. ATRIBUT FISIK
@Parcelize
data class PhysicalAttributes(
    var acceleration: Int = 5,
    var sprintSpeed: Int = 5,
    var strength: Int = 5,
    var stamina: Int = 5,
    var jumping: Int = 5,
    var balance: Int = 5,
    var naturalFitness: Int = 5, // Kecepatan pemulihan energi
    var injuryProneness: Int = 50 // Semakin tinggi, semakin rentan
) : Parcelable

// 3. ATRIBUT MENTAL
@Parcelize
data class MentalAttributes(
    // Kecerdasan Taktis
    var positioning: Int = 5,
    var vision: Int = 5,         // Kemampuan melihat peluang umpan
    var anticipation: Int = 5,   // Kemampuan membaca permainan
    var decisionMaking: Int = 5,
    // Sikap
    var composure: Int = 5,      // Ketenangan di bawah tekanan
    var workRate: Int = 5,
    var determination: Int = 5,
    var aggression: Int = 5,
    var leadership: Int = 5,
    var teamwork: Int = 5
) : Parcelable

// 4. ATRIBUT PRIBADI & KARIR
@Parcelize
data class CareerAttributes(
    var professionalism: Int = 50, // Mempengaruhi kecepatan training
    var mediaHandling: Int = 20,   // Kemampuan wawancara
    var financialAcumen: Int = 10, // Kemampuan mengelola bisnis
    var adaptability: Int = 30,    // Kemampuan adaptasi di klub baru
    var loyalty: Int = 50,
    var ambition: Int = 50,
    var reputation: Int = 1       // Reputasi global pemain
) : Parcelable