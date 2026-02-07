package com.championstar.soccer.game.calendar

import com.championstar.soccer.data.model.CalendarEvent
import com.championstar.soccer.data.model.EventType
import com.championstar.soccer.data.model.Player
import com.championstar.soccer.data.model.PlayerCareerState
import com.championstar.soccer.data.repository.EventRepository
import com.championstar.soccer.game.league.LeagueManager
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

object GameCalendar {

    private var currentDate: LocalDate = LocalDate.of(2025, 7, 1)
    // Jadwal ini sekarang hanya untuk event yang dijadwalkan secara spesifik (misal: pertandingan liga)
    private val eventSchedule = mutableListOf<CalendarEvent>()

    fun initialize() {
        currentDate = LocalDate.of(2025, 7, 1)
        eventSchedule.clear()
        // Kita tidak lagi membuat jadwal awal yang panjang di sini.
    }

    fun getCurrentDate(): LocalDate = currentDate

    /**
     * Fungsi inti yang baru. Mencari event terjadwal atau membuat event baru yang logis.
     */
    fun getNextEvent(player: Player): CalendarEvent {
        // 1. Prioritaskan event yang sudah terjadwal (seperti pertandingan liga)
        val scheduledEvent = eventSchedule.sortedBy { it.date }
            .firstOrNull { !it.date.isBefore(currentDate) }

        if (scheduledEvent != null) {
            return scheduledEvent
        }

        // 2. Jika tidak ada, buat event berikutnya secara dinamis berdasarkan fase karier pemain
        return generateNextLogicalEvent(player)
    }

    /**
     * Memajukan tanggal game ke HARI SETELAH event berikutnya.
     */
    fun advanceToNextEvent(player: Player): CalendarEvent {
        val nextEvent = getNextEvent(player)
        currentDate = nextEvent.date.plusDays(1)
        return nextEvent
    }

    /**
     * Otak dari sistem kalender dinamis.
     */
    private fun generateNextLogicalEvent(player: Player): CalendarEvent {
        // Periksa apakah ada event naratif yang memenuhi syarat.
        val eligibleNarrativeEvents = EventRepository.getEligibleEvents(player)
        // Beri kesempatan (misal: 20%) untuk memicu event naratif jika ada.
        if (eligibleNarrativeEvents.isNotEmpty() && Random.nextInt(1, 101) <= 20) {
            val chosenEvent = eligibleNarrativeEvents.random()
            return CalendarEvent(
                date = currentDate.plusDays(1),
                type = EventType.NARRATIVE_EVENT,
                description = chosenEvent.title,
                details = mapOf("eventId" to chosenEvent.eventId)
            )
        }

        // Jika tidak ada event naratif yang terpicu, kembali ke logika awal.
        return when (player.careerState) {
            PlayerCareerState.UNATTACHED_NO_AGENT -> {
                // Tujuan: Mendorong pemain mencari agen.
                val eventDate = currentDate.plusDays(2)
                CalendarEvent(eventDate, EventType.AGENT_MEETING, "Meet Potential Agents")
            }
            PlayerCareerState.UNATTACHED_WITH_AGENT -> {
                // Tujuan: Agen mencarikan tawaran trial.
                val eventDate = currentDate.plusDays(3)
                CalendarEvent(eventDate, EventType.SPECIAL_OPPORTUNITY, "Your agent has found some trial offers!")
            }
            PlayerCareerState.UNDER_CONTRACT -> {
                // Tujuan: Menjalankan siklus mingguan (latihan -> laga -> istirahat).
                val nextMatch = eventSchedule
                    .filter { it.type == EventType.MATCH_DAY }
                    .firstOrNull { !it.date.isBefore(currentDate) }

                if (nextMatch == null) {
                    // Musim berakhir, buat event istirahat panjang (off-season)
                    return CalendarEvent(currentDate.plusDays(1), EventType.REST_DAY, "Off-season break")
                }

                val daysUntilMatch = ChronoUnit.DAYS.between(currentDate, nextMatch.date)

                return when {
                    daysUntilMatch <= 1 -> // H-1 Pertandingan
                        CalendarEvent(currentDate.plusDays(1), EventType.TEAM_MEETING, "Pre-match tactical briefing")
                    daysUntilMatch in 2..4 -> // Pertengahan minggu
                        CalendarEvent(currentDate.plusDays(1), EventType.TRAINING_SESSION, "Team Training Session")
                    else -> // Awal minggu, setelah pertandingan sebelumnya
                        CalendarEvent(currentDate.plusDays(1), EventType.REST_DAY, "Recovery Session")
                }
            }
        }
    }

    /**
     * Menjadwalkan semua pertandingan liga untuk satu musim.
     * Fungsi ini dipanggil saat pemain menandatangani kontrak.
     */
    fun scheduleLeagueMatches(clubId: Int) {
        eventSchedule.removeAll { it.type == EventType.MATCH_DAY }
        val clubFixtures = LeagueManager.getFixturesForClub(clubId)

        clubFixtures.forEach { fixture ->
            val opponentId = if (fixture.homeClubId == clubId) fixture.awayClubId else fixture.homeClubId
            val event = CalendarEvent(
                date = fixture.date,
                type = EventType.MATCH_DAY,
                description = "League Match",
                details = mapOf("opponentId" to opponentId.toString())
            )
            eventSchedule.add(event)
        }
    }
}
