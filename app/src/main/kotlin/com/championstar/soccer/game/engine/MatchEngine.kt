package com.championstar.soccer.game.engine

import com.championstar.soccer.data.entities.PlayerEntity
import kotlin.random.Random

data class MatchResult(
    val scorePlayerTeam: Int,
    val scoreOpponent: Int,
    val commentary: List<String>,
    val playerRating: Float,
    val goals: Int,
    val assists: Int
)

class MatchEngine {

    fun simulateMatch(player: PlayerEntity, opponentStrength: Int): MatchResult {
        val commentary = mutableListOf<String>()
        var scorePlayerTeam = 0
        var scoreOpponent = 0
        var goals = 0
        var assists = 0
        var performanceScore = 6.0f

        commentary.add("Match Start! ${player.teamName} vs Opponent (Strength: $opponentStrength)")

        // Simple simulation logic based on 90 mins (simulated in chunks)
        for (minute in 1..90 step 10) {
            val eventRoll = Random.nextInt(100)

            // Influence of player skills
            val speed = player.skills["Speed"] ?: 50
            val shooting = player.skills["Shooting"] ?: 50
            val passing = player.skills["Passing"] ?: 50

            // Determine possession/action based on overall rating vs opponent
            val teamStrength = 70 + (player.overallRating / 10) // Basic team strength calculation

            if (eventRoll < (teamStrength - opponentStrength + 50)) {
                // Player's team attacking
                if (Random.nextInt(100) < 30) {
                    // Player involved
                    if (Random.nextInt(100) < (shooting + speed) / 2) {
                        goals++
                        scorePlayerTeam++
                        performanceScore += 1.0f
                        commentary.add("$minute': GOAL!! ${player.name} scores with a brilliant finish!")
                    } else if (Random.nextInt(100) < passing) {
                        assists++
                        scorePlayerTeam++
                        performanceScore += 0.5f
                        commentary.add("$minute': GOAL! Great assist by ${player.name} to a teammate.")
                    } else {
                        commentary.add("$minute': ${player.name} shoots but misses narrowly.")
                    }
                } else {
                    // Teammate scores/misses
                    if (Random.nextBoolean()) {
                        scorePlayerTeam++
                        commentary.add("$minute': GOAL for ${player.teamName}!")
                    } else {
                        commentary.add("$minute': ${player.teamName} attacks but fails to convert.")
                    }
                }
            } else {
                // Opponent attacking
                if (Random.nextInt(100) < 40) {
                    scoreOpponent++
                    performanceScore -= 0.1f
                    commentary.add("$minute': Goal for Opponent. Defense was caught napping.")
                } else {
                    commentary.add("$minute': Opponent pushes forward but is stopped.")
                }
            }
        }

        commentary.add("Full Time: ${player.teamName} $scorePlayerTeam - $scoreOpponent Opponent")

        return MatchResult(
            scorePlayerTeam,
            scoreOpponent,
            commentary,
            performanceScore.coerceIn(1.0f, 10.0f),
            goals,
            assists
        )
    }
}