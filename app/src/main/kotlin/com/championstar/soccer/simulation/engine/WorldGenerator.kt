package com.championstar.soccer.simulation.engine

import com.championstar.soccer.data.static.LeagueDatabase
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.simulation.engine.generators.ClubGenerator
import com.championstar.soccer.simulation.engine.generators.PlayerGenerator

object WorldGenerator {

    /**
     * Generates the entire game world with 30 leagues, ~550 teams, and ~12,000 players.
     * Uses advanced generators for Clubs and Players to ensure deep variety.
     */
    fun generateWorld(): List<League> {
        val leagues = mutableListOf<League>()

        LeagueDatabase.leagueTemplates.forEach { template ->
            val leagueTeams = mutableListOf<Team>()

            for (i in 1..template.teamCount) {
                // Initial Basic Name (will be enriched)
                val baseName = "${template.name} Club $i" // Placeholder
                val teamId = "${template.id}_T${i.toString().padStart(2, '0')}"

                val team = Team(
                    id = teamId,
                    name = baseName,
                    leagueId = template.id,
                    reputation = 50.0 // Will be recalculated based on players/tier
                )

                // Enrich Club Identity (Name, Colors, History)
                ClubGenerator.enrichTeam(team, template.region)

                // Fill Squad (25 Players) using Advanced Player Generator
                fillSquad(team, template.tier, template.region)

                // Recalculate Team Reputation based on Squad
                team.reputation = team.players.map { it.overallRating }.average()

                leagueTeams.add(team)
            }

            leagues.add(League(
                id = template.id,
                name = template.name,
                tier = template.tier,
                teams = leagueTeams
            ))
        }

        return leagues
    }

    private fun fillSquad(team: Team, tier: Int, region: String) {
        // Standard distribution: 3 GK, 8 DF, 8 MF, 6 FW = 25 Players
        // This ensures a balanced squad for the match engine.

        generatePlayersForPosition(team, "GK", 3, tier, region)
        generatePlayersForPosition(team, "DF", 8, tier, region)
        generatePlayersForPosition(team, "MF", 8, tier, region)
        generatePlayersForPosition(team, "FW", 6, tier, region)
    }

    private fun generatePlayersForPosition(
        team: Team,
        posGroup: String,
        count: Int,
        tier: Int,
        region: String
    ) {
        for (i in 0 until count) {
            val specificPos = when(posGroup) {
                "DF" -> listOf("CB", "LB", "RB").random()
                "MF" -> listOf("CDM", "CM", "CAM", "LM", "RM").random()
                "FW" -> listOf("ST", "CF", "RW", "LW").random()
                else -> "GK"
            }

            // Generate distinct player
            val player = PlayerGenerator.generatePlayer(
                tier = tier,
                leagueRegion = region,
                forcedPosition = specificPos,
                isYouthAcademy = false
            )

            team.players.add(player)
        }
    }
}
