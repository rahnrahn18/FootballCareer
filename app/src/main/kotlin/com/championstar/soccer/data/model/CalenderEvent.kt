package com.championstar.soccer.data.model

import java.time.LocalDate

/**
 * Enum (kumpulan label) untuk mendefinisikan setiap tipe event yang mungkin terjadi.
 * Ini adalah bagian yang hilang.
 */

enum class EventType {
    // Karir Sepak Bola
    MATCH_DAY,
    TRAINING_SESSION,
    TEAM_MEETING,
    INJURY_RECOVERY,

    // Bisnis & Keuangan
    AGENT_MEETING,
    SPONSOR_EVENT,
    BUSINESS_MANAGEMENT,

    // Kehidupan Pribadi & Media
    MEDIA_INTERVIEW,
    PERSONAL_LIFE_EVENT,
    REST_DAY,

    // Event Spesial
    SPECIAL_OPPORTUNITY,

    // *** TIPE EVENT BARU UNTUK CERITA DINAMIS ***
    NARRATIVE_EVENT
}
/**
 * Data class untuk menampung satu event dalam jadwal.
 * Ini menggunakan EventType sebagai kategorinya.
 */
data class CalendarEvent(
    val date: LocalDate,
    val type: EventType,
    val description: String,
    val details: Map<String, String> = emptyMap() // Untuk data tambahan, misal: 'opponent' -> 'Real Madrid'
)