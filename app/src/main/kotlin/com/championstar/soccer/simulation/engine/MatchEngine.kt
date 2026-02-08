package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.core.math.Probability
import com.championstar.soccer.data.static.EventDatabase
import com.championstar.soccer.domain.models.MatchResult
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import kotlin.math.abs

/**
 * MatchEngine
 *
 * The core simulation logic for matches.
 * Uses probability distributions and team stats to generate realistic match outcomes
 * and minute-by-minute event logs.
 */
object MatchEngine {

    const val MATCH_MINUTES = 90

    // --- Simulation Logic ---

    fun simulateMatch(homeTeam: Team, awayTeam: Team): MatchResult {
        val events = mutableListOf<String>()
        var homeScore = 0
        var awayScore = 0

        // Calculate team strengths
        val homeStrength = calculateTeamStrength(homeTeam)
        val awayStrength = calculateTeamStrength(awayTeam)

        // Home advantage
        val homeAdvantage = 1.1
        val adjustedHomeStrength = homeStrength * homeAdvantage

        // Expected goals (xG) based on strength difference
        val strengthDiff = (adjustedHomeStrength - awayStrength) / 10.0
        val baseGoals = 1.3 // Average goals per team per match

        val homeXG = (baseGoals + strengthDiff).coerceAtLeast(0.1)
        val awayXG = (baseGoals - strengthDiff).coerceAtLeast(0.1)

        // Simulate goals using Poisson distribution
        val projectedHomeGoals = Probability.nextPoisson(homeXG)
        val projectedAwayGoals = Probability.nextPoisson(awayXG)

        // Generate events minute by minute
        for (minute in 1..MATCH_MINUTES) {
            // Check for goal
            // Chance per minute distributed across the match
            if (homeScore < projectedHomeGoals && GameMath.chance(1.0 / (MATCH_MINUTES / projectedHomeGoals.coerceAtLeast(1).toDouble()))) {
                homeScore++
                val scorer = selectScorer(homeTeam)
                val eventText = EventDatabase.getEvent(EventDatabase.Zone.ATTACK, EventDatabase.EventType.SHOT, EventDatabase.Outcome.GOAL)
                    .replace("{player}", scorer.name)
                events.add("$minute': GOAL! $eventText ($homeScore-$awayScore)")
                scorer.goals++
            } else if (awayScore < projectedAwayGoals && GameMath.chance(1.0 / (MATCH_MINUTES / projectedAwayGoals.coerceAtLeast(1).toDouble()))) {
                awayScore++
                val scorer = selectScorer(awayTeam)
                val eventText = EventDatabase.getEvent(EventDatabase.Zone.ATTACK, EventDatabase.EventType.SHOT, EventDatabase.Outcome.GOAL)
                    .replace("{player}", scorer.name)
                events.add("$minute': GOAL! $eventText ($homeScore-$awayScore)")
                scorer.goals++
            } else {
                // Non-goal event logic (fluff)
                if (GameMath.chance(0.05)) { // 5% chance of noteworthy event per minute
                    val team = if (GameMath.chance(0.5)) homeTeam else awayTeam
                    val player = team.players.randomOrNull() ?: continue

                    val possibleTypes = EventDatabase.EventType.entries.filter { it != EventDatabase.EventType.SHOT && it != EventDatabase.EventType.CARD }
                    if (possibleTypes.isEmpty()) continue

                    val eventType = possibleTypes.random()
                    val zone = EventDatabase.Zone.entries.random()
                    val outcome = if (GameMath.chance(0.7)) EventDatabase.Outcome.SUCCESS else EventDatabase.Outcome.FAILURE

                    var text = EventDatabase.getEvent(zone, eventType, outcome)
                    text = text.replace("{player}", player.name)

                    val otherPlayers = team.players.filter { it != player }
                    if (otherPlayers.isNotEmpty()) {
                         text = text.replace("{receiver}", otherPlayers.random().name)
                    } else {
                         text = text.replace("{receiver}", "teammate")
                    }

                    events.add("$minute': $text")
                }
            }
        }

        return MatchResult(homeTeam, awayTeam, homeScore, awayScore, events, true)
    }

    private fun calculateTeamStrength(team: Team): Double {
        if (team.players.isEmpty()) return 50.0 // Default average
        val totalRating = team.players.sumOf { it.overallRating }
        // Tactics modifier could go here
        return totalRating / team.players.size
    }

    private fun selectScorer(team: Team): Player {
        // Weighted random selection based on position/skill
        if (team.players.isEmpty()) return Player("0", "Unknown", 20, "FW", 50.0, 50.0)

        val weights = team.players.map { player ->
            when (player.position) {
                "FW", "ST" -> 5.0
                "RW", "LW", "CAM" -> 3.0
                "CM", "CDM" -> 1.0
                "CB", "LB", "RB" -> 0.5
                "GK" -> 0.1
                else -> 1.0
            } + (player.overallRating / 20.0) // Better players score more
        }

        return Probability.weightedRandom(team.players, weights) ?: team.players.random()
    }

    /**
     * Simulates a detailed match involving the User Player.
     * Higher detail than general simulation.
     */
    fun playUserMatch(player: Player, team: Team, opponent: Team): MatchResult {
        // Just use general logic for now, but ensure player is involved
        // Could force player events here
        val result = simulateMatch(team, opponent)

        // Post-process to ensure user gets mentioned if they played well
        // Logic handled in TimeEngine for now

        return result
    }
}
