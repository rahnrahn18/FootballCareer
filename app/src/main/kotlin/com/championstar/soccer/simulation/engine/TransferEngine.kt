package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.EconomyMath
import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.Contract
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.domain.models.TransferPolicy
import kotlin.math.roundToLong

object TransferEngine {

    /**
     * Evaluates a player's suitability for a club.
     * Returns a "Desire Score" (0.0 to 100.0).
     */
    fun evaluatePlayerForClub(player: Player, club: Team): Double {
        var score = 50.0

        // 1. Position Need
        val existingPlayersInPos = club.players.filter { it.position == player.position }
        val depthScore = when (existingPlayersInPos.size) {
            0 -> 40.0 // Desperate need
            1 -> 25.0 // Strong need (backup/competition)
            2 -> 10.0 // Moderate need
            else -> -20.0 // Overcrowded
        }
        score += depthScore

        // 2. Skill Comparison (Is player better than current starter?)
        if (existingPlayersInPos.isNotEmpty()) {
            val bestPlayer = existingPlayersInPos.maxByOrNull { it.overallRating }!!
            val skillDiff = player.overallRating - bestPlayer.overallRating
            score += skillDiff * 2.0 // +10 score for +5 rating advantage
        } else {
            score += 20.0 // Automatic upgrade if position empty
        }

        // 3. Club Policy Modifiers
        when (club.transferPolicy) {
            TransferPolicy.GALACTICO -> {
                // Loves high reputation & rating
                if (player.reputation > 80.0) score += 20.0
                if (player.overallRating > 85.0) score += 15.0
            }
            TransferPolicy.YOUTH_DEVELOPMENT -> {
                // Loves young, high potential
                if (player.age < 23) score += 15.0
                if (player.potential - player.overallRating > 10.0) score += 20.0
            }
            TransferPolicy.MONEYBALL -> {
                // Loves undervalued players (high stats/low cost)
                // Simplified: High stats for age
                if (player.overallRating > 75.0 && player.marketValue < 5000000) score += 25.0
            }
            TransferPolicy.RELEGATION_BATTLER -> {
                // Loves experience
                if (player.age > 28) score += 10.0
                if (player.overallRating > 70.0) score += 10.0
            }
            else -> {} // Balanced
        }

        // 4. Budget Check (Binary Gate)
        // Can they afford wage?
        val estWage = EconomyMath.calculateWage(
            EconomyMath.calculateMarketValue(player.overallRating, player.age, player.potential, player.reputation),
            3
        )
        if (club.budget < estWage * 52) { // Need yearly budget
            score -= 100.0 // Cannot afford
        }

        return score.coerceIn(0.0, 100.0)
    }

    /**
     * Generates a contract offer from a club to a player.
     */
    fun generateContractOffer(player: Player, club: Team): Contract {
        val marketValue = EconomyMath.calculateMarketValue(player.overallRating, player.age, player.potential, player.reputation)

        // Base offer
        var wage = EconomyMath.calculateWage(marketValue, 3)
        var years = 3
        var signingBonus = (wage * 4) // 1 month wage as bonus

        // Adjust based on "Desire Score"
        val desire = evaluatePlayerForClub(player, club)
        if (desire > 80.0) {
            wage = (wage * 1.2).toLong() // 20% premium
            signingBonus = (signingBonus * 1.5).toLong()
            years = 4 // Lock down longer
        } else if (desire < 40.0) {
            wage = (wage * 0.8).toLong() // Lowball
            years = 2 // Short term risk
        }

        // Bonuses
        val goalBonus = if (player.position in listOf("ST", "FW", "CF")) (wage * 0.1).toLong() else 0
        val cleanSheetBonus = if (player.position in listOf("GK", "CB", "LB", "RB")) (wage * 0.05).toLong() else 0

        return Contract(
            salary = wage,
            yearsRemaining = years,
            releaseClause = (marketValue * 2.5).toLong(), // Standard 2.5x value
            signingBonus = signingBonus,
            goalBonus = goalBonus,
            cleanSheetBonus = cleanSheetBonus,
            appearanceBonus = (wage * 0.05).toLong()
        )
    }
}
