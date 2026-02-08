package com.championstar.soccer.simulation.engine

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
     * Advances the game by one week (one turn).
     * This is the core "Game Loop".
     * Returns a summary log of the week's events.
     */
    fun processWeek(player: Player, leagues: List<League>): String {
        val weekLog = StringBuilder()
        weekLog.append("--- ${currentDate} ---\n")

        // Find Player's Team (Optimization: Could pass this in)
        // Since contract is linked to player, we can find team if player has contract
        // But team object contains player... circular. Let's find team by player ID
        val currentTeam = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }

        // 1. Process Player State (The "Grind")
        // Energy Management: Without rest, energy drops.
        player.stamina = (player.stamina + 5.0).coerceAtMost(100.0) // Natural recovery

        // Skill Decay (if not training/playing)
        if (currentTeam == null) {
            // Free agent decay is faster
            player.overallRating = (player.overallRating - 0.05).coerceAtLeast(1.0)
            weekLog.append("⚠ Unemployed: Skill slightly declining due to lack of match fitness.\n")
        }

        // 2. Financials (Living Costs)
        val livingCost = calculateLivingCost(player)
        // Need a bank account model, for now just log it or deduct from imaginary wallet
        // player.money -= livingCost
        weekLog.append("💸 Living Expenses: $$livingCost deducted.\n")

        // 3. Match Simulation (if employed)
        if (currentTeam != null) {
            // Check if match scheduled
            // Simple logic: Play every week except off-season (June/July)
            // Season: Aug (8) to May (5)
            val isSeason = currentDate.month >= 8 || currentDate.month <= 5

            if (isSeason) {
                // Find opponent (Random for now within league)
                val league = leagues.find { it.id == currentTeam.leagueId }
                val opponent = league?.teams?.filter { it.id != currentTeam.id }?.random()

                if (opponent != null) {
                    // Pre-match selection check
                    val squad = SquadEngine.selectMatchSquad(currentTeam)
                    val isStarter = squad.starters.any { it.id == player.id }
                    val isSub = squad.substitutes.any { it.id == player.id }

                    if (isStarter || isSub) {
                        // Simulate match
                        val result = MatchEngine.simulateMatch(currentTeam, opponent)

                        // Calculate performance
                        val (rating, goals) = calculatePlayerPerformance(player, result, opponent)

                        // Update player stats
                        player.appearances++
                        player.goals += goals
                        // Form update: weighted moving average
                        player.form = (player.form * 0.8 + rating * 10.0 * 0.2).coerceIn(0.0, 100.0)
                        player.stamina -= 15.0 // Match fatigue

                        weekLog.append("⚽ Match vs ${opponent.name}: ${currentTeam.name} ${result.homeScore}-${result.awayScore} ${opponent.name}\n")
                        weekLog.append("   Performance: Rating ${String.format("%.1f", rating)}, Goals: $goals\n")

                        // Story snippets based on performance
                        if (rating > 8.0) weekLog.append("   ⭐ Man of the Match performance!\n")
                        if (rating < 5.0) weekLog.append("   📉 Disappointing display. Coach is not happy.\n")

                    } else {
                        weekLog.append("❌ Not selected for match vs ${opponent.name}. Coach says: 'Train harder!'\n")
                        player.morale -= 2.0 // Frustration
                    }
                }
            } else {
                weekLog.append("📅 Pre-season training. Fitness is key.\n")
                player.stamina = (player.stamina + 10.0).coerceAtMost(100.0) // Boost fitness in pre-season
            }
        }

        // 4. Advance Date
        currentDate.nextWeek()

        return weekLog.toString()
    }

    private fun calculateLivingCost(player: Player): Long {
        // Lifestyle creep: Higher reputation = higher expected spending to maintain image
        val baseCost = 500L
        val lifestyle = (player.reputation * 2000).toLong()
        return baseCost + lifestyle
    }

    private fun calculatePlayerPerformance(player: Player, matchResult: MatchResult, opponent: Team): Pair<Double, Int> {
        // Simple performance calc
        // Base rating 6.0
        // +1.0 for win, -0.5 for loss
        // +Skill difference vs opponent avg

        var rating = 6.0

        // Win bonus
        if (matchResult.homeScore > matchResult.awayScore) rating += 1.0
        else if (matchResult.homeScore < matchResult.awayScore) rating -= 0.5

        // Personal performance (Dice roll + skill)
        val formFactor = (player.form - 50.0) / 50.0 // -1.0 to +1.0
        val skillFactor = (player.overallRating - opponent.reputation) / 20.0 // +/- rating based on opponent strength

        rating += (random.nextDouble() * 2.0 - 0.5) + formFactor + skillFactor

        // Goals?
        var goals = 0
        // Strikers score more
        val goalChanceBase = when (player.position) {
            "ST", "CF" -> 0.25
            "RW", "LW", "CAM" -> 0.15
            "CM", "CDM" -> 0.05
            "DF", "GK" -> 0.01
            else -> 0.0
        }

        // If team scored, did I score?
        val teamGoals = matchResult.homeScore // Assuming home for now, logic needs update for Home/Away context
        // Simplified: Just roll independent chance based on match flow

        if (random.nextDouble() < goalChanceBase + (skillFactor * 0.1)) {
            goals = 1
            rating += 1.5
            // Brace chance
            if (random.nextDouble() < 0.1) {
                goals++
                rating += 1.0
            }
        }

        return rating.coerceIn(3.0, 10.0) to goals
    }
}
