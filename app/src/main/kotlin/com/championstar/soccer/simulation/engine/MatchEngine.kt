package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.core.math.Probability
import com.championstar.soccer.data.static.EventDatabase
import com.championstar.soccer.domain.models.*
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

/**
 * Advanced Match Engine
 *
 * Simulates a football match minute-by-minute with "Smart Math" logic.
 * Calculates detailed player ratings, possession, and match events.
 */
object MatchEngine {

    private const val MATCH_DURATION = 90
    private const val BASE_RATING = 6.0

    /**
     * Simulates a full match between two teams.
     */
    fun simulateMatch(homeTeam: Team, awayTeam: Team): MatchResult {
        val matchId = UUID.randomUUID().toString()
        val events = mutableListOf<MatchEvent>()
        val playerRatings = mutableMapOf<String, Double>()

        // Initialize ratings for all players
        (homeTeam.players + awayTeam.players).forEach { playerRatings[it.id] = BASE_RATING }

        var homeScore = 0
        var awayScore = 0
        var homePossessionTicks = 0
        var awayPossessionTicks = 0
        var homeShots = 0
        var awayShots = 0
        var homeOnTarget = 0
        var awayOnTarget = 0

        // Team Strengths (cached for performance)
        val homeAtt = calculateSectorStrength(homeTeam, "ATT")
        val homeMid = calculateSectorStrength(homeTeam, "MF")
        val homeDef = calculateSectorStrength(homeTeam, "DEF")
        val homeGK = calculateSectorStrength(homeTeam, "GK")

        val awayAtt = calculateSectorStrength(awayTeam, "ATT")
        val awayMid = calculateSectorStrength(awayTeam, "MF")
        val awayDef = calculateSectorStrength(awayTeam, "DEF")
        val awayGK = calculateSectorStrength(awayTeam, "GK")

        // Simulation Loop
        for (minute in 1..MATCH_DURATION) {
            // 1. Determine Possession
            // Midfield battle + Home Advantage (1.05)
            val homeProb = (homeMid * 1.05) / (homeMid * 1.05 + awayMid)
            val isHomePossession = GameMath.nextDouble() < homeProb

            if (isHomePossession) homePossessionTicks++ else awayPossessionTicks++

            // 2. Action Logic
            val attackingTeam = if (isHomePossession) homeTeam else awayTeam
            val defendingTeam = if (isHomePossession) awayTeam else homeTeam
            val attStrength = if (isHomePossession) homeAtt else awayAtt
            val defStrength = if (isHomePossession) awayDef else homeDef
            val gkStrength = if (isHomePossession) awayGK else homeGK

            // Chance to create a chance (Creative Midfielders)
            if (GameMath.chance(0.15)) { // 15% chance of highlight per minute
                val creator = selectPlayerByRole(attackingTeam, "MF", "FW")

                // Event: Key Pass / Chance Creation
                if (GameMath.chance(attStrength / (attStrength + defStrength))) {
                    // Successful Attack -> Shot
                    val shooter = selectPlayerByRole(attackingTeam, "FW", "MF")

                    // Update Rating: Creator
                    updateRating(playerRatings, creator.id, 0.3)

                    // Shot Calculation
                    if (isHomePossession) homeShots++ else awayShots++

                    // On Target?
                    val finish = shooter.overallRating // simplified finishing stat
                    val shotQuality = GameMath.gaussian(finish, 10.0)

                    if (shotQuality > 40) { // On Target
                        if (isHomePossession) homeOnTarget++ else awayOnTarget++

                        // GK Save Check
                        val keeper = selectPlayerByRole(defendingTeam, "GK")
                        val saveChance = gkStrength / (gkStrength + shotQuality)

                        if (GameMath.nextDouble() > saveChance) {
                            // GOAL!
                            if (isHomePossession) homeScore++ else awayScore++

                            val eventType = EventType.GOAL
                            val desc = "${shooter.name} scores! Assist by ${creator.name}."
                            events.add(MatchEvent(minute, eventType, shooter.id, creator.id, attackingTeam.id, desc))

                            // Ratings Update
                            updateRating(playerRatings, shooter.id, 1.0) // Goal
                            updateRating(playerRatings, creator.id, 0.5) // Assist
                            updateRating(playerRatings, keeper.id, -0.5) // Conceded

                            // Update Player Season Stats
                            shooter.seasonStats.goals++
                            creator.seasonStats.assists++

                        } else {
                            // SAVE!
                            val desc = "Great save by ${keeper.name} from ${shooter.name}'s shot!"
                            events.add(MatchEvent(minute, EventType.SAVE, keeper.id, shooter.id, defendingTeam.id, desc))

                            updateRating(playerRatings, keeper.id, 0.5) // Save
                            updateRating(playerRatings, shooter.id, 0.1) // Shot on target
                        }
                    } else {
                        // Missed
                    }
                } else {
                    // Attack Broken Up -> Tackle
                    val defender = selectPlayerByRole(defendingTeam, "DF", "MF")
                    updateRating(playerRatings, defender.id, 0.2) // Tackle Won
                }
            }

            // Random Cards / Injuries (Low chance)
            if (GameMath.chance(0.005)) { // 0.5% per minute
                val offender = if (GameMath.chance(0.5)) homeTeam.players.random() else awayTeam.players.random()
                val cardType = if (GameMath.chance(0.9)) EventType.YELLOW_CARD else EventType.RED_CARD

                events.add(MatchEvent(minute, cardType, offender.id, null, if(homeTeam.players.contains(offender)) homeTeam.id else awayTeam.id, "${offender.name} receives a ${if(cardType==EventType.RED_CARD) "RED" else "Yellow"} Card!"))

                updateRating(playerRatings, offender.id, if (cardType == EventType.RED_CARD) -2.0 else -0.5)

                if (cardType == EventType.YELLOW_CARD) offender.seasonStats.yellowCards++
                else offender.seasonStats.redCards++
            }
        }

        // Finalize Stats
        val totalTicks = homePossessionTicks + awayPossessionTicks
        val homePoss = if (totalTicks > 0) ((homePossessionTicks.toDouble() / totalTicks) * 100).toInt() else 50
        val awayPoss = 100 - homePoss

        // Clean Sheets
        if (awayScore == 0) homeTeam.players.filter { it.position == "GK" || it.position.contains("B") }.forEach { it.seasonStats.cleanSheets++ }
        if (homeScore == 0) awayTeam.players.filter { it.position == "GK" || it.position.contains("B") }.forEach { it.seasonStats.cleanSheets++ }

        // Determine MOTM (Highest Rating)
        val motmId = playerRatings.maxByOrNull { it.value }?.key
        if (motmId != null) {
            val motmPlayer = (homeTeam.players + awayTeam.players).find { it.id == motmId }
            motmPlayer?.seasonStats?.manOfTheMatchCount = (motmPlayer?.seasonStats?.manOfTheMatchCount ?: 0) + 1
        }

        // Update Average Ratings in History
        updatePlayerHistory(homeTeam, matchId, awayTeam.name, playerRatings, events)
        updatePlayerHistory(awayTeam, matchId, homeTeam.name, playerRatings, events)

        return MatchResult(
            matchId = matchId,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeScore = homeScore,
            awayScore = awayScore,
            homePossession = homePoss,
            awayPossession = awayPoss,
            homeShots = homeShots,
            awayShots = awayShots,
            homeOnTarget = homeOnTarget,
            awayOnTarget = awayOnTarget,
            events = events,
            playerRatings = playerRatings,
            manOfTheMatchId = motmId,
            isFinished = true
        )
    }

    private fun calculateSectorStrength(team: Team, sector: String): Double {
        val players = team.players.filter {
            when (sector) {
                "GK" -> it.position == "GK"
                "DEF" -> it.position.contains("B") // CB, LB, RB
                "MF" -> it.position.contains("M") // CM, LM, RM, CAM, CDM
                "ATT" -> it.position.contains("F") || it.position.contains("W") || it.position.contains("ST") // FW, ST, RW, LW
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
                minutesPlayed = 90, // Simplified
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
