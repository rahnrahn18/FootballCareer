package com.championstar.soccer.simulation.engine

import com.championstar.soccer.data.static.EventDatabase
import com.championstar.soccer.data.static.ShopDatabase
import com.championstar.soccer.domain.models.GameDate
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.MatchResult
import com.championstar.soccer.domain.models.EventType
import kotlin.random.Random

sealed class GameTurnEvent {
    data class StoryEvent(val title: String, val description: String, val choices: List<EventChoice>) : GameTurnEvent()
    data class MatchEvent(val opponentName: String, val resultText: String, val rating: Double, val goals: Int) : GameTurnEvent()
    data class RoutineEvent(val weekSummary: String) : GameTurnEvent()
    data class SeasonEndEvent(val age: Int, val isRetired: Boolean) : GameTurnEvent()
}

object TimeEngine {

    val currentDate = GameDate()
    private val random = Random(System.currentTimeMillis())

    /**
     * Advances time by ONE unit (week) and returns the primary event for that period.
     * This supports the "Daily Quest" style loop where every click is a turn.
     */
    fun advanceTime(player: Player, leagues: List<League>): GameTurnEvent {
        // 1. Process Basic Weekly Logic
        processWeeklyMaintenance(player)

        // 2. Check for Match
        val currentTeam = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }

        if (ScheduleEngine.isMatchScheduled(currentDate)) {
            // Find opponent BEFORE simulation advances
            val opponent = if (currentTeam != null) {
                val league = leagues.find { it.id == currentTeam.leagueId }
                if (league != null) LeagueEngine.getScheduledOpponent(league, currentTeam) else null
            } else null

            // Simulate REST of the world
            LeagueEngine.simulateWeek(leagues, currentTeam?.id)

            // Play User Match
            if (currentTeam != null && opponent != null) {
                val (resultText, rating, goals) = simulateMatchDay(player, currentTeam, opponent)

                // Advance date AFTER the event is processed
                currentDate.nextWeek()

                return GameTurnEvent.MatchEvent(
                    opponentName = opponent.name,
                    resultText = resultText,
                    rating = rating,
                    goals = goals
                )
            }
        }

        // 3. Check for Random Event (Quest)
        // Only generate if NO match happened this week
        val randomEvent = EventEngine.generateWeeklyEvent(player)
        if (randomEvent != null) {
            // Don't advance date automatically here? Usually events happen during the week.
            // But if we return it, the UI will show it. The USER clicking a choice will advance the turn.
            // Actually, let's keep it simple: An event consumes the week.
            currentDate.nextWeek()
            return GameTurnEvent.StoryEvent(randomEvent.title, randomEvent.description, randomEvent.choices)
        }

        // 4. End of Season Check
        if (ScheduleEngine.isEndOfSeason(currentDate)) {
            player.age++
            val isRetired = player.age >= player.retirementAge
            currentDate.nextWeek()
            return GameTurnEvent.SeasonEndEvent(player.age, isRetired)
        }

        // 5. Nothing Special (Routine Week)
        sanitizePlayerStats(player)
        val weekNum = currentDate.week
        currentDate.nextWeek()

        return GameTurnEvent.RoutineEvent("Week $weekNum: Training went well. Focus is sharp.")
    }

    // Keeping the old method for backward compatibility if needed, but likely unused now.
    // Deprecating it mentally.
    fun jumpToNextEvent(player: Player, leagues: List<League>): String {
        return "Legacy method deprecated. Use advanceTime()."
    }

    private fun sanitizePlayerStats(player: Player) {
        player.stamina = player.stamina.coerceIn(0.0, 100.0)
        player.morale = player.morale.coerceIn(0.0, 100.0)
        player.form = player.form.coerceIn(0.0, 100.0)
        player.overallRating = player.overallRating.coerceIn(1.0, 99.9)
    }

    private fun processWeeklyMaintenance(player: Player) {
        // Recover some stamina every week naturally
        player.stamina = (player.stamina + 5.0).coerceAtMost(100.0)
    }

    private fun simulateMatchDay(player: Player, currentTeam: Team, opponent: Team): Triple<String, Double, Int> {
        // Squad Selection
        val squad = SquadEngine.selectMatchSquad(currentTeam)
        val isSelected = squad.starters.any { it.id == player.id } || squad.substitutes.any { it.id == player.id }

        // Match Simulation
        val result = MatchEngine.simulateMatch(currentTeam, opponent)

        // Stats Update
        val playerPerf = result.playerRatings[player.id] ?: 6.0
        val playerGoals = result.events.count { it.playerId == player.id && it.type == EventType.GOAL }

        LeagueEngine.updateTeamStats(currentTeam, opponent, result)

        if (isSelected) {
            // Player was involved
            player.form = (player.form * 0.8 + playerPerf * 10.0 * 0.2).coerceIn(0.0, 100.0)
            player.stamina = (player.stamina - 15.0).coerceAtLeast(0.0)
            player.seasonStats.appearances++
            player.seasonStats.goals += playerGoals

            // Check Achievements
            AchievementEngine.checkAchievements(player, result)

            return Triple(
                "${currentTeam.name} ${result.homeScore}-${result.awayScore} ${opponent.name}",
                playerPerf,
                playerGoals
            )
        } else {
            return Triple(
                "Not selected. ${currentTeam.name} ${result.homeScore}-${result.awayScore} ${opponent.name}",
                0.0,
                0
            )
        }
    }
}
