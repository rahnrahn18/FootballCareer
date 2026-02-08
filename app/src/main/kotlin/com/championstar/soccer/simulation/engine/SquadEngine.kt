package com.championstar.soccer.simulation.engine

import com.championstar.soccer.domain.models.MatchSquad
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team

object SquadEngine {

    /**
     * Automatically selects the best 11 starters and 6 substitutes for a team.
     * Uses a simple formation logic (1 GK, 4 DF, 4 MF, 2 FW).
     */
    fun selectMatchSquad(team: Team): MatchSquad {
        // Filter available players (e.g., stamina > 50, not injured)
        val allPlayers = team.players.sortedByDescending { it.overallRating }

        val starters = mutableListOf<Player>()
        val substitutes = mutableListOf<Player>()

        // 1. Select Goalkeeper (Best available)
        val gk = allPlayers.firstOrNull { it.position == "GK" } ?: allPlayers.first()
        starters.add(gk)

        // 2. Select Defenders (4)
        val defenders = allPlayers.filter { !starters.contains(it) && isDefender(it.position) }.take(4)
        starters.addAll(defenders)

        // 3. Select Midfielders (4)
        val midfielders = allPlayers.filter { !starters.contains(it) && isMidfielder(it.position) }.take(4)
        starters.addAll(midfielders)

        // 4. Select Forwards (2)
        val forwards = allPlayers.filter { !starters.contains(it) && isForward(it.position) }.take(2)
        starters.addAll(forwards)

        // 5. Fill remaining spots if position groups were short (e.g., team has only 3 defenders)
        if (starters.size < 11) {
            val remainingNeeded = 11 - starters.size
            val others = allPlayers.filter { !starters.contains(it) }.take(remainingNeeded)
            starters.addAll(others)
        }

        // 6. Select Substitutes (6)
        // Ideally 1 GK, 2 DF, 2 MF, 1 FW
        val remaining = allPlayers.filter { !starters.contains(it) }

        // Try to get a backup GK
        val subGK = remaining.firstOrNull { it.position == "GK" }
        if (subGK != null) substitutes.add(subGK)

        // Fill the rest with best remaining players
        val remainingSlots = 6 - substitutes.size
        substitutes.addAll(remaining.filter { !substitutes.contains(it) }.take(remainingSlots))

        return MatchSquad(starters, substitutes)
    }

    private fun isDefender(pos: String) = pos == "CB" || pos == "LB" || pos == "RB" || pos == "RWB" || pos == "LWB"
    private fun isMidfielder(pos: String) = pos == "CM" || pos == "CDM" || pos == "CAM" || pos == "LM" || pos == "RM"
    private fun isForward(pos: String) = pos == "ST" || pos == "CF" || pos == "RW" || pos == "LW"
}
