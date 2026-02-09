package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.Localization
import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.GameDate
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.domain.models.EventType
import java.util.Calendar

/**
 * Advanced Time Engine.
 * Manages the complex progression of the game world, including daily schedules,
 * seasonal phases, transfer windows, and event triggering.
 */
object TimeEngine {

    // --- STATE ---
    var currentDate: GameDate = GameDate(day = 1, month = 8, year = 2024) // Start of Season
    private var isProcessingTurn = false

    // --- ENUMS ---
    enum class SeasonPhase {
        PRE_SEASON,
        TRANSFER_WINDOW_SUMMER,
        REGULAR_SEASON,
        TRANSFER_WINDOW_WINTER,
        SEASON_END
    }

    enum class DayType {
        TRAINING,
        REST,
        MATCH_DAY,
        EVENT_DAY,
        TRAVEL
    }

    // --- PUBLIC API ---

    /**
     * Advances time by ONE day.
     * Returns a GameTurnEvent describing what happened (UI triggers).
     */
    fun advanceDay(player: Player, leagues: List<League>): GameTurnEvent {
        if (isProcessingTurn) return GameTurnEvent.RoutineEvent("Processing...")
        isProcessingTurn = true

        try {
            // 1. Daily Maintenance (All Entities)
            processDailyMaintenance(player, leagues)

            // 2. Identify Day Type & Event
            val event = processDailyEvent(player, leagues)

            // 3. Increment Date
            incrementDate()

            return event
        } finally {
            isProcessingTurn = false
        }
    }

    // --- INTERNAL LOGIC ---

    private fun processDailyMaintenance(player: Player, leagues: List<League>) {
        // Player Recovery
        val recoveryRate = if (player.age < 22) 15.0 else if (player.age < 30) 10.0 else 5.0
        player.stamina = (player.stamina + recoveryRate).coerceAtMost(100.0)

        // Morale Decay/Boost (Random daily flux)
        val moraleFlux = GameMath.gaussian(0.0, 2.0)
        player.morale = (player.morale + moraleFlux).coerceIn(0.0, 100.0)

        // World Simulation (News, Transfers, Injuries for other teams)
        // Only run heavy simulation on specific days to save performance?
        // Or run lightweight simulation daily.
        // For now: Run weekly simulation on Mondays?
        // Let's stick to daily updates if needed.
    }

    private fun processDailyEvent(player: Player, leagues: List<League>): GameTurnEvent {
        val currentTeam = findTeamForPlayer(player, leagues)
        val phase = getSeasonPhase(currentDate)
        val dayType = getDayType(currentDate, currentTeam)

        return when (dayType) {
            DayType.MATCH_DAY -> handleMatchDay(player, currentTeam, leagues)
            DayType.TRAINING -> handleTrainingDay(player)
            DayType.EVENT_DAY -> handleRandomEvent(player)
            else -> GameTurnEvent.RoutineEvent("Rest Day. Recovering energy.")
        }
    }

    private fun handleMatchDay(player: Player, team: Team?, leagues: List<League>): GameTurnEvent {
        if (team == null) return GameTurnEvent.RoutineEvent("Free Agent. Watching matches on TV.")

        val opponent = LeagueEngine.getScheduledOpponent(leagues.find { it.id == team.leagueId }!!, team)
            ?: return GameTurnEvent.RoutineEvent("No match scheduled today despite calendar.")

        // User match is handled by UI via "Play Match" button usually?
        // OR does advanceDay() TRIGGER the match?
        // In this architecture, returning MatchEvent triggers the Match Screen in UI.

        return GameTurnEvent.MatchEvent(
            opponentName = opponent.name,
            opponentId = opponent.id,
            matchId = "${currentDate.year}_${currentDate.month}_${currentDate.day}_${team.id}",
            description = "Match Day vs ${opponent.name}"
        )
    }

    private fun handleTrainingDay(player: Player): GameTurnEvent {
        // Chance of training injury or breakthrough
        if (GameMath.chance(0.01)) {
            player.stamina -= 20.0
            return GameTurnEvent.RoutineEvent("Training Accident! Took a knock in practice.")
        }
        if (GameMath.chance(0.05)) {
            player.form = (player.form + 5.0).coerceAtMost(100.0)
            return GameTurnEvent.RoutineEvent("Great session! Coach is impressed.")
        }
        return GameTurnEvent.RoutineEvent("Training Day. Working on tactics.")
    }

    private fun handleRandomEvent(player: Player): GameTurnEvent {
        val event = EventEngine.generateDailyEvent(player)
        return if (event != null) {
            GameTurnEvent.StoryEvent(event.title, event.description, event.choices)
        } else {
            GameTurnEvent.RoutineEvent("Quiet day.")
        }
    }

    private fun incrementDate() {
        currentDate.day++
        if (currentDate.day > 30) { // Simplified 30-day months for prototype consistency
            currentDate.day = 1
            currentDate.month++
            if (currentDate.month > 12) {
                currentDate.month = 1
                currentDate.year++
            }
        }
        // Update Week/DayOfWeek
        // ... (Logic to update simplified week counter if needed)
    }

    // --- HELPERS ---

    private fun getSeasonPhase(date: GameDate): SeasonPhase {
        return when (date.month) {
            7, 8 -> SeasonPhase.PRE_SEASON
            9, 10, 11, 12 -> SeasonPhase.REGULAR_SEASON
            1 -> SeasonPhase.TRANSFER_WINDOW_WINTER
            2, 3, 4, 5 -> SeasonPhase.REGULAR_SEASON
            6 -> SeasonPhase.SEASON_END
            else -> SeasonPhase.REGULAR_SEASON
        }
    }

    private fun getDayType(date: GameDate, team: Team?): DayType {
        // Simple schedule: Match every Saturday (Day 6, 13, 20, 27)
        // Training: Mon-Fri
        // Rest: Sun

        // Map 1..30 to DayOfWeek (Assuming 1st = Mon for simplicity)
        val dayOfWeek = (date.day - 1) % 7 + 1 // 1=Mon, 6=Sat, 7=Sun

        return when (dayOfWeek) {
            6 -> DayType.MATCH_DAY
            7 -> DayType.REST
            3 -> if (GameMath.chance(0.2)) DayType.EVENT_DAY else DayType.TRAINING // Random event wednesday
            else -> DayType.TRAINING
        }
    }

    private fun findTeamForPlayer(player: Player, leagues: List<League>): Team? {
        return leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }
    }
}

// --- EVENTS ---
sealed class GameTurnEvent {
    data class StoryEvent(val title: String, val description: String, val choices: List<EventChoice>) : GameTurnEvent()
    data class MatchEvent(val opponentName: String, val opponentId: String, val matchId: String, val description: String) : GameTurnEvent()
    data class RoutineEvent(val message: String) : GameTurnEvent()
    data class SeasonEndEvent(val age: Int, val isRetired: Boolean) : GameTurnEvent()
}

// Helper (Assuming existing EventEngine exists, but need to ensure it has generateDailyEvent)
// If not, I will add it or use generateWeeklyEvent as proxy.
