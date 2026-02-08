package com.championstar.soccer.simulation.engine

import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.core.math.GameMath

object GrindEngine {

    /**
     * Applies daily/weekly costs and penalties to ensure the game is hard.
     */
    fun applyHardships(player: Player) {
        // 1. Skill Decay: If not training (which costs energy), skills drop.
        // We assume auto-decay happens weekly in TimeEngine, but here we can add "Injury Risk"

        if (player.stamina < 30.0) {
            // High injury risk
            if (GameMath.chance(0.1)) {
                player.stamina -= 20.0
                player.form -= 10.0
                player.morale -= 10.0
                // Log: "You pushed too hard and pulled a muscle."
            }
        }

        // 2. Financial Pressure
        // Agents take a cut of wages (10-20%)
        val agentFee = (player.contract?.salary ?: 0) * 0.15
        // player.money -= agentFee.toLong()

        // 3. Morale difficulty
        // Bad form spirals
        if (player.form < 40.0) {
            player.morale -= 1.0 // Depression spiral
        }
    }
}
