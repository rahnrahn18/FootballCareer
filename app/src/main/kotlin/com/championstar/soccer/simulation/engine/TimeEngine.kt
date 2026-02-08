package com.championstar.soccer.simulation.engine

import com.championstar.soccer.data.static.EventDatabase
import com.championstar.soccer.data.static.ShopDatabase
import com.championstar.soccer.domain.models.GameDate
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.MatchResult
import kotlin.random.Random

object TimeEngine {

    val currentDate = GameDate()
    private val random = Random(System.currentTimeMillis())

    /**
     * Skips time until the next significant event (Match, Transfer Offer, or random Event).
     */
    fun jumpToNextEvent(player: Player, leagues: List<League>): String {
        val log = StringBuilder()
        var weeksPassed = 0
        var foundEvent = false

        while (!foundEvent && weeksPassed < 52) {

            // 1. Process Basic Weekly Logic
            processWeeklyMaintenance(player)

            // 2. Check for Match
            val currentTeam = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }

            if (ScheduleEngine.isMatchScheduled(currentDate)) {
                // Find opponent BEFORE simulation advances (so we sync with LeagueEngine logic)
                val opponent = if (currentTeam != null) {
                    val league = leagues.find { it.id == currentTeam.leagueId }
                    if (league != null) LeagueEngine.getScheduledOpponent(league, currentTeam) else null
                } else null

                // Simulate REST of the world (advances matchdays for all leagues)
                LeagueEngine.simulateWeek(leagues, currentTeam?.id)

                // Play User Match
                if (currentTeam != null && opponent != null) {
                    log.append("--- ${currentDate} ---\n")
                    val matchLog = simulateMatchDay(player, currentTeam, opponent)
                    log.append(matchLog)
                    foundEvent = true
                }
            } else {
                // No match, check for Random Event
                 val randomEvent = EventEngine.generateWeeklyEvent(player)
                 if (randomEvent != null) {
                    log.append("--- ${currentDate} ---\n")
                    log.append("❗ EVENT: ${randomEvent.title}\n")
                    log.append("${randomEvent.description}\n")
                    foundEvent = true
                 }
            }

            // 3. End of Season
            if (ScheduleEngine.isEndOfSeason(currentDate)) {
                player.age++
                log.append("--- ${currentDate} ---\n")
                log.append("🎂 Birthday! You are now ${player.age}.\n")

                if (player.age >= player.retirementAge) {
                    log.append("🛑 RETIREMENT: You have reached the end of your career at ${player.age}.\n")
                    foundEvent = true
                }
            }

            // Ensure bounds after every week processing
            sanitizePlayerStats(player)

            if (!foundEvent) {
                currentDate.nextWeek()
            }
            weeksPassed++
        }

        if (foundEvent) {
             currentDate.nextWeek()
        }

        if (weeksPassed >= 52 && !foundEvent) log.append("⚠️ Advanced 1 year without major events.\n")

        return log.toString()
    }

    private fun sanitizePlayerStats(player: Player) {
        player.stamina = player.stamina.coerceIn(0.0, 100.0)
        player.morale = player.morale.coerceIn(0.0, 100.0)
        player.form = player.form.coerceIn(0.0, 100.0)
        // Ensure overall rating stays reasonable (though it might exceed 100 slightly in some games, typically 99 max)
        player.overallRating = player.overallRating.coerceIn(1.0, 99.9)
    }

    private fun processWeeklyMaintenance(player: Player) {
        player.stamina = (player.stamina + 5.0).coerceAtMost(100.0)
    }

    private fun simulateMatchDay(player: Player, currentTeam: Team, opponent: Team): String {
        val sb = StringBuilder()

        // Squad Selection
        val squad = SquadEngine.selectMatchSquad(currentTeam)
        val isSelected = squad.starters.any { it.id == player.id } || squad.substitutes.any { it.id == player.id }

        // Match Simulation
        val result = MatchEngine.simulateMatch(currentTeam, opponent)

        // Stats Update
        val playerPerf = result.playerRatings[player.id] ?: 6.0
        val playerGoals = result.events.count { it.playerId == player.id && it.type == com.championstar.soccer.domain.models.EventType.GOAL }

        LeagueEngine.updateTeamStats(currentTeam, opponent, result)

        if (isSelected) {
            // Player was involved
            player.form = (player.form * 0.8 + playerPerf * 10.0 * 0.2).coerceIn(0.0, 100.0)
            player.stamina = (player.stamina - 15.0).coerceAtLeast(0.0)

            // Check Achievements
            val unlocked = AchievementEngine.checkAchievements(player, result)

            sb.append("⚽ Match vs ${opponent.name}: ${currentTeam.name} ${result.homeScore}-${result.awayScore} ${opponent.name}\n")
            sb.append("   Rating: ${String.format("%.1f", playerPerf)}, Goals: $playerGoals\n")

            if (unlocked.isNotEmpty()) {
                unlocked.forEach { sb.append("   $it\n") }
            }
        } else {
            sb.append("❌ Not selected vs ${opponent.name}. Training instead.\n")
            sb.append("   Result: ${currentTeam.name} ${result.homeScore}-${result.awayScore} ${opponent.name}\n")
        }

        return sb.toString()
    }
}
