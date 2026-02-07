package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Enum untuk mendefinisikan spesialisasi seorang agen
enum class AgentSpecialty {
    NEGOTIATOR, // Ahli dalam mendapatkan gaji tinggi
    MARKETING_GURU, // Ahli dalam mencari sponsor besar
    TALENT_SCOUT, // Ahli dalam menemukan klub yang tepat untuk pengembangan
    BALANCED
}

@Parcelize
data class Agent(
    val id: String,
    val name: String,
    val reputation: Int, // Skala 1-100, mempengaruhi kualitas tawaran
    val commissionRate: Double, // Persentase potongan dari gaji pemain
    val specialty: AgentSpecialty,
    val description: String,
    val portraitResourceName: String // Misal: "agent_portrait_1"
) : Parcelable