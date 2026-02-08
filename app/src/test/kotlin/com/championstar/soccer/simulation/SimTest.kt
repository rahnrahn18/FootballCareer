package com.championstar.soccer.simulation

import com.championstar.soccer.simulation.engine.*
import com.championstar.soccer.domain.models.*
import org.junit.Test
import org.junit.Assert.*

class SimTest {

    @Test
    fun testSimulation() {
        println("Starting Simulation...")

        // 1. Generate World
        val leagues = WorldGenerator.generateWorld()
        assertTrue("Leagues should be generated", leagues.isNotEmpty())

        val userLeague = leagues.find { it.tier == 4 } ?: leagues.first()
        val userTeam = userLeague.teams.first()

        // 2. Create User Player
        val player = CareerEngine.startCareer("Test User", "FW", "Europe")
        userTeam.players.add(player)

        // 3. Simulate 10 Weeks
        for (i in 1..10) {
            println("\n--- Week $i ---")
            TimeEngine.jumpToNextEvent(player, leagues)

            // Check League Stats
            val played = userTeam.leagueStats.played
            println("User Team Played: $played (Points: ${userTeam.leagueStats.points})")

            // Check Player Stats
            println("User Form: ${player.form}")
            println("User Goals: ${player.seasonStats.goals}")
            println("User Rating Avg: ${player.seasonStats.averageRating}")
        }

        // 4. Verify Stats
        assertTrue("User team should have played matches", userTeam.leagueStats.played > 0)
        assertTrue("User should have a rating history", player.seasonStats.ratingCount > 0 || player.matchHistory.isNotEmpty())

        // 5. Print Table
        println("\n--- League Table ---")
        userLeague.teams.sortedByDescending { it.leagueStats.points }.forEachIndexed { index, team ->
            println("${index+1}. ${team.name} - P:${team.leagueStats.played} Pts:${team.leagueStats.points} GD:${team.leagueStats.goalDifference}")
        }
    }
}
