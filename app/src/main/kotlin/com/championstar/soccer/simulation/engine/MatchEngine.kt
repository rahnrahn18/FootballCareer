package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.*
import java.util.UUID

/**
 * Advanced Match Engine & Interactive System
 */
object MatchEngine {

    private const val MATCH_DURATION = 90
    private const val BASE_RATING = 6.0

    // --- INTERACTIVE MODE SUPPORT ---

    data class InteractiveMatchState(
        val matchId: String = UUID.randomUUID().toString(),
        val homeTeam: Team,
        val awayTeam: Team,
        var minute: Int = 0,
        var homeScore: Int = 0,
        var awayScore: Int = 0,
        var homePossessionTicks: Int = 0,
        var awayPossessionTicks: Int = 0,
        val events: MutableList<MatchEvent> = mutableListOf(),
        val playerRatings: MutableMap<String, Double> = mutableMapOf(),
        var isFinished: Boolean = false
    )

    fun createInteractiveMatch(homeTeam: Team, awayTeam: Team): InteractiveMatchState {
        val state = InteractiveMatchState(homeTeam = homeTeam, awayTeam = awayTeam)
        (homeTeam.players + awayTeam.players).forEach { state.playerRatings[it.id] = BASE_RATING }
        return state
    }

    /**
     * Simulates 1 minute of the match.
     * Returns a list of events that happened in this minute.
     */
    fun simulateMinute(state: InteractiveMatchState): List<MatchEvent> {
        if (state.minute >= 90) {
            state.isFinished = true
            return emptyList()
        }
        state.minute++

        val newEvents = mutableListOf<MatchEvent>()

        // 1. Team Strengths
        val homeMid = calculateSectorStrength(state.homeTeam, "MF")
        val awayMid = calculateSectorStrength(state.awayTeam, "MF")

        // 2. Possession
        val homeProb = (homeMid * 1.05) / (homeMid * 1.05 + awayMid)
        val isHomePossession = GameMath.nextDouble() < homeProb
        if (isHomePossession) state.homePossessionTicks++ else state.awayPossessionTicks++

        // 3. Action Logic (Simplified for Interactive Loop)
        // 15% chance of highlight
        if (GameMath.chance(0.15)) {
            val attackingTeam = if (isHomePossession) state.homeTeam else state.awayTeam
            val defendingTeam = if (isHomePossession) state.awayTeam else state.homeTeam

            // Goal Chance
            if (GameMath.chance(0.10)) { // 10% of highlights result in goal chance
                 val scorer = selectPlayerByRole(attackingTeam, "FW", "MF")
                 // Simple Goal/Miss
                 if (GameMath.chance(0.4)) { // 40% conversion rate
                     if (isHomePossession) state.homeScore++ else state.awayScore++
                     val event = MatchEvent(state.minute, EventType.GOAL, scorer.id, null, attackingTeam.id, "${scorer.name} scores!")
                     newEvents.add(event)
                     state.events.add(event)
                     updateRating(state.playerRatings, scorer.id, 1.0)
                 } else {
                     // Miss
                 }
            }
        }

        return newEvents
    }

    data class DecisionContext(
        val title: String,
        val description: String,
        val options: List<DecisionOption>
    )

    data class DecisionOption(
        val text: String,
        val outcomeDescription: String,
        val successChance: Double,
        val rewardRating: Double,
        val riskRating: Double
    )

    fun generateDecision(player: Player, positionGroup: String): DecisionContext {
        return when (positionGroup) {
            "FW", "ATT" -> DecisionContext(
                "Goal Scoring Opportunity!",
                "You are through on goal. The keeper is rushing out.",
                listOf(
                    DecisionOption("Chip Shot", "Beautiful chip over the keeper!", 0.4, 1.5, -0.5),
                    DecisionOption("Power Shot", "Smashed into the corner!", 0.6, 1.0, -0.2),
                    DecisionOption("Pass", "Unselfish play to teammate.", 0.8, 0.5, -0.1)
                )
            )
            "MF", "MID" -> DecisionContext(
                "Midfield Battle",
                "You have space in the middle. Runners are ahead.",
                listOf(
                    DecisionOption("Through Ball", "Splits the defense!", 0.5, 0.8, -0.2),
                    DecisionOption("Long Shot", "Screamer from 30 yards!", 0.2, 2.0, -0.4),
                    DecisionOption("Dribble", "Take on the defender.", 0.6, 0.5, -0.3)
                )
            )
            "DF", "DEF" -> DecisionContext(
                "Defensive Duty",
                "The opponent is dribbling at you.",
                listOf(
                    DecisionOption("Slide Tackle", "Crunching tackle!", 0.5, 1.0, -1.0), // High risk card
                    DecisionOption("Jockey", "Forced them back.", 0.8, 0.3, 0.0),
                    DecisionOption("Interception", "Read the pass perfectly.", 0.6, 0.5, -0.2)
                )
            )
            else -> DecisionContext( // GK
                "Save The Shot",
                "Striker is shooting!",
                listOf(
                    DecisionOption("Dive Left", " fingertip save!", 0.33, 2.0, -1.0),
                    DecisionOption("Stay Center", "Blocked with chest!", 0.33, 1.0, -1.0),
                    DecisionOption("Dive Right", "Caught the ball!", 0.33, 2.0, -1.0)
                )
            )
        }
    }

    fun processDecision(state: InteractiveMatchState, player: Player, option: DecisionOption): Boolean {
        // Return true if success
        val isSuccess = GameMath.nextDouble() < option.successChance
        if (isSuccess) {
            updateRating(state.playerRatings, player.id, option.rewardRating)
            // If goal related?
            if (option.text.contains("Shot") || option.text.contains("Chip")) {
                // Goal!
                val team = if (state.homeTeam.players.any { it.id == player.id }) state.homeTeam else state.awayTeam
                if (team == state.homeTeam) state.homeScore++ else state.awayScore++

                val event = MatchEvent(state.minute, EventType.GOAL, player.id, null, team.id, "${player.name} scores a ${option.text}!")
                state.events.add(event)
                player.seasonStats.goals++
            }
        } else {
            updateRating(state.playerRatings, player.id, option.riskRating)
        }
        return isSuccess
    }

    fun finalizeInteractiveMatch(state: InteractiveMatchState): MatchResult {
        // Calculate possession
        val totalTicks = state.homePossessionTicks + state.awayPossessionTicks
        val homePoss = if (totalTicks > 0) ((state.homePossessionTicks.toDouble() / totalTicks) * 100).toInt() else 50

        // Update history
        updatePlayerHistory(state.homeTeam, state.matchId, state.awayTeam.name, state.playerRatings, state.events)
        updatePlayerHistory(state.awayTeam, state.matchId, state.homeTeam.name, state.playerRatings, state.events)

        return MatchResult(
            matchId = state.matchId,
            homeTeam = state.homeTeam,
            awayTeam = state.awayTeam,
            homeScore = state.homeScore,
            awayScore = state.awayScore,
            homePossession = homePoss,
            awayPossession = 100 - homePoss,
            events = state.events,
            playerRatings = state.playerRatings,
            isFinished = true
        )
    }

    // --- EXISTING BACKGROUND SIMULATION ---

    /**
     * Simulates a full match between two teams (Instant).
     */
    fun simulateMatch(homeTeam: Team, awayTeam: Team): MatchResult {
        // ... (Keep existing logic or redirect to interactive loop logic for consistency, but for now keep separate to avoid breaking changes if not needed)
        // Ideally we refactor simulateMatch to use simulateMinute in a loop.
        val state = createInteractiveMatch(homeTeam, awayTeam)
        for (i in 1..90) {
            val minuteEvents = simulateMinute(state)
            // No user interaction in background sim
        }
        return finalizeInteractiveMatch(state)
    }

    // ... (Keep existing helper methods like calculateSectorStrength, etc.)

    private fun calculateSectorStrength(team: Team, sector: String): Double {
        val players = team.players.filter {
            when (sector) {
                "GK" -> it.position == "GK"
                "DEF" -> it.position.contains("B")
                "MF" -> it.position.contains("M")
                "ATT" -> it.position.contains("F") || it.position.contains("W") || it.position.contains("ST")
                else -> false
            }
        }
        if (players.isEmpty()) return 50.0
        return players.map { it.overallRating }.average()
    }

    private fun selectPlayerByRole(team: Team, primaryRole: String, secondaryRole: String? = null): Player {
        val candidates = team.players.filter {
            val pRole = if (primaryRole == "FW") "F" else if (primaryRole == "DF") "B" else primaryRole
            val sRole = if (secondaryRole == "FW") "F" else if (secondaryRole == "DF") "B" else secondaryRole

            it.position.contains(pRole) || (sRole != null && it.position.contains(sRole))
        }
        return if (candidates.isNotEmpty()) candidates.random() else team.players.random()
    }

    private fun updateRating(ratings: MutableMap<String, Double>, playerId: String, change: Double) {
        val current = ratings[playerId] ?: BASE_RATING
        ratings[playerId] = (current + change).coerceIn(3.0, 10.0)
    }

    private fun updatePlayerHistory(team: Team, matchId: String, opponentName: String, ratings: Map<String, Double>, events: List<MatchEvent>) {
        team.players.forEach { player ->
            val rating = ratings[player.id] ?: BASE_RATING

            val goals = events.count { it.playerId == player.id && it.type == EventType.GOAL }
            val assists = events.count { it.secondaryPlayerId == player.id && it.type == EventType.GOAL }

            val historyItem = MatchPerformance(
                matchId = matchId,
                opponentName = opponentName,
                rating = rating,
                goals = goals,
                assists = assists,
                minutesPlayed = 90,
                events = events.filter { it.playerId == player.id || it.secondaryPlayerId == player.id }.map { it.description }
            )
            player.matchHistory.add(0, historyItem)
            if (player.matchHistory.size > 50) player.matchHistory.removeLast()

            val oldSum = player.seasonStats.averageRating * player.seasonStats.ratingCount
            player.seasonStats.ratingCount++
            player.seasonStats.averageRating = (oldSum + rating) / player.seasonStats.ratingCount
        }
    }
}
