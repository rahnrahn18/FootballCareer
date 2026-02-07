package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.Curves
import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.data.static.TraitDatabase
import com.championstar.soccer.domain.models.Player
import kotlin.math.roundToInt

/**
 * PlayerGrowthEngine
 *
 * Logic for player development, regression, and aging.
 * Uses mathematical curves to simulate realistic career trajectories.
 */
object PlayerGrowthEngine {

    /**
     * Updates player stats based on age, potential, and traits.
     * Should be called weekly or monthly in game time.
     */
    fun processWeeklyGrowth(player: Player) {
        // Growth logic
        if (player.age < 29) {
            // Development phase
            val potentialGap = player.potential - player.overallRating
            if (potentialGap > 0) {
                // Determine growth rate using Sigmoid curve (peak growth around 21-24)
                // Normalize age 16-30 to roughly -5 to +5 for sigmoid
                val ageFactor = (player.age - 22.0) / 3.0
                val growthSpeed = Curves.sigmoid(ageFactor, steepness = 0.8)

                // Base growth + random variation
                var growthAmount = (potentialGap * 0.05 * growthSpeed) + (GameMath.nextDouble() * 0.2)

                // Trait modifiers
                player.traits.forEach { trait ->
                    if (trait.type == TraitDatabase.TraitType.DEVELOPMENT) {
                        growthAmount = trait.impact(growthAmount)
                    }
                }

                player.overallRating = (player.overallRating + growthAmount).coerceAtMost(player.potential)
            }
        } else {
            // Decline phase
            val ageDiff = player.age - 29
            val decayRate = 0.05 // 5% per year approx
            // Exponential decay for older players
            val currentSkill = player.overallRating
            val newSkill = Curves.exponentialDecay(currentSkill, ageDiff, decayRate / 52.0) // Weekly decay

            player.overallRating = newSkill.coerceAtLeast(1.0)
        }

        // Stamina Recovery
        player.stamina = (player.stamina + 10.0).coerceAtMost(100.0)
    }

    /**
     * Checks for retirement or major career events.
     */
    fun checkCareerEvents(player: Player): String? {
        if (player.age > 34 && GameMath.chance(0.1)) {
            return "${player.name} is considering retirement at end of season."
        }
        return null
    }
}
