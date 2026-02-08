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
     * Replaces the old "processWeek" loop.
     */
    fun jumpToNextEvent(player: Player, leagues: List<League>): String {
        val log = StringBuilder()
        var weeksPassed = 0
        var foundEvent = false

        // Loop until we find something interesting or 52 weeks pass (safety)
        while (!foundEvent && weeksPassed < 52) {

            // 1. Process Basic Weekly Logic (Stamina, etc.)
            processWeeklyMaintenance(player)

            // 2. Check for Match (Using ScheduleEngine)
            val currentTeam = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }

            if (currentTeam != null && ScheduleEngine.isMatchScheduled(currentDate)) {
                // Match Day!
                log.append("--- ${currentDate} ---\n")
                // Cannot call simulateMatchDay here directly because 'currentTeam' might be null in compiler eyes despite check
                val matchLog = simulateMatchDay(player, currentTeam, leagues)
                log.append(matchLog)
                foundEvent = true // Stop skipping
            } else {
                // No match, check for Random Event
                 val randomEvent = EventEngine.generateWeeklyEvent(player)
                 if (randomEvent != null) {
                    log.append("--- ${currentDate} ---\n")
                    log.append("❗ EVENT: ${randomEvent.title}\n")
                    log.append("${randomEvent.description}\n")
                    // In a real app, we'd pause here and return the event object to UI.
                    foundEvent = true
                 }
            }

            // 3. End of Season / Birthday Check
            if (ScheduleEngine.isEndOfSeason(currentDate)) {
                player.age++
                log.append("--- ${currentDate} ---\n")
                log.append("🎂 Birthday! You are now ${player.age}.\n")

                if (player.age >= player.retirementAge) {
                    log.append("🛑 RETIREMENT: You have reached the end of your career at ${player.age}.\n")
                    foundEvent = true
                }
            }

            // Always advance time at end of loop if not stopped
            if (!foundEvent) {
                currentDate.nextWeek()
            }
            weeksPassed++
        }

        // If we found an event, we advance past it so next call is fresh
        if (foundEvent) {
             currentDate.nextWeek()
        }

        if (weeksPassed >= 52 && !foundEvent) log.append("⚠️ Advanced 1 year without major events.\n")

        return log.toString()
    }

    private fun processWeeklyMaintenance(player: Player) {
        // Stamina Recovery
        player.stamina = (player.stamina + 5.0).coerceAtMost(100.0)
    }

    private fun simulateMatchDay(player: Player, currentTeam: Team, leagues: List<League>): String {
        val sb = StringBuilder()
        val league = leagues.find { it.id == currentTeam.leagueId }
        val opponent = league?.teams?.filter { it.id != currentTeam.id }?.random()

        if (opponent != null) {
            // Squad Selection
            val squad = SquadEngine.selectMatchSquad(currentTeam)
            val isSelected = squad.starters.any { it.id == player.id } || squad.substitutes.any { it.id == player.id }

            if (isSelected) {
                val result = MatchEngine.simulateMatch(currentTeam, opponent)
                val (rating, goals) = calculatePlayerPerformance(player, result, opponent)

                // Update Stats
                player.appearances++
                player.goals += goals
                player.form = (player.form * 0.8 + rating * 10.0 * 0.2).coerceIn(0.0, 100.0)
                player.stamina -= 15.0

                // Check Achievements
                val unlocked = AchievementEngine.checkAchievements(player, result)

                sb.append("⚽ Match vs ${opponent.name}: ${currentTeam.name} ${result.homeScore}-${result.awayScore} ${opponent.name}\n")
                sb.append("   Rating: ${String.format("%.1f", rating)}, Goals: $goals\n")

                if (unlocked.isNotEmpty()) {
                    unlocked.forEach { sb.append("   $it\n") }
                }
            } else {
                sb.append("❌ Not selected vs ${opponent.name}. Training instead.\n")
            }
        }
        return sb.toString()
    }

    private fun calculatePlayerPerformance(player: Player, matchResult: MatchResult, opponent: Team): Pair<Double, Int> {
        var rating = 6.0
        if (matchResult.homeScore > matchResult.awayScore) rating += 1.0
        else if (matchResult.homeScore < matchResult.awayScore) rating -= 0.5

        val formFactor = (player.form - 50.0) / 50.0
        rating += (random.nextDouble() * 2.0 - 0.5) + formFactor

        var goals = 0
        if (random.nextDouble() < 0.15 + (formFactor * 0.1)) {
            goals = 1
            rating += 1.5
        }
        return rating.coerceIn(3.0, 10.0) to goals
    }
}
