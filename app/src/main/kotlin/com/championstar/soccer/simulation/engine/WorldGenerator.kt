package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.data.static.LeagueDatabase
import com.championstar.soccer.data.static.NameDatabase
import com.championstar.soccer.data.static.TraitDatabase
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import java.util.UUID

object WorldGenerator {

    /**
     * Generates the entire game world with 30 leagues, ~550 teams, and ~12,000 players.
     */
    fun generateWorld(): List<League> {
        val leagues = mutableListOf<League>()

        // Track unique team names globally or per league to avoid duplicates
        // For simplicity, we just ensure IDs are unique

        var teamIdCounter = 1

        LeagueDatabase.leagueTemplates.forEach { template ->
            val leagueTeams = mutableListOf<Team>()

            // Determine skill range based on tier
            // Tier 1: 75-95, Tier 2: 68-82, Tier 3: 60-72, Tier 4: 50-65
            val minSkill = when(template.tier) {
                1 -> 75.0
                2 -> 68.0
                3 -> 60.0
                else -> 50.0
            }
            val maxSkill = when(template.tier) {
                1 -> 95.0
                2 -> 82.0
                3 -> 72.0
                else -> 65.0
            }

            for (i in 1..template.teamCount) {
                // Generate Team Name
                // If a real city/name generator isn't fully expanded, we use template logic
                val teamName = if (i <= 5 && template.teamCount >= 5) {
                    "${template.name} Giants ${i}"
                } else {
                    "${template.name} Club ${i}"
                }

                // Ensure unique ID
                val teamId = "${template.id}_T${i.toString().padStart(2, '0')}"

                val team = Team(
                    id = teamId,
                    name = teamName,
                    leagueId = template.id,
                    reputation = (minSkill + maxSkill) / 2.0
                )

                // Fill Squad (25 Players)
                // Need to use team reference so we create the object first, then fill list
                // Kotlin allows this as `players` is a mutable list inside `Team`

                // Pass primitive doubles for min/max
                fillSquad(team, minSkill, maxSkill, template.region)
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

    private fun fillSquad(team: Team, minRating: Double, maxRating: Double, region: String) {
        // Standard distribution: 3 GK, 8 DF, 8 MF, 6 FW = 25 Players

        generatePlayersForPosition(team, "GK", 3, minRating, maxRating, region)
        generatePlayersForPosition(team, "DF", 8, minRating, maxRating, region)
        generatePlayersForPosition(team, "MF", 8, minRating, maxRating, region)
        generatePlayersForPosition(team, "FW", 6, minRating, maxRating, region)
    }

    private fun generatePlayersForPosition(
        team: Team,
        posGroup: String,
        count: Int,
        minR: Double,
        maxR: Double,
        region: String
    ) {
        for (i in 0 until count) {
            val age = GameMath.nextInt(16, 36)

            // Skill curve based on age (peak 27-29)
            // Young players (16-21) lower current, high potential
            // Old players (32+) high current (maybe), declining potential

            // Base rating from league tier
            val tierBase = GameMath.lerp(minR, maxR, GameMath.nextDouble())

            // Variance
            val variance = GameMath.gaussian(0.0, 3.0)
            val currentRating = (tierBase + variance).coerceIn(1.0, 99.0)

            // Potential logic
            val potential = if (age < 24) {
                (currentRating + GameMath.nextInt(5, 15)).coerceAtMost(99.0)
            } else {
                currentRating // Older players have reached potential
            }

            // Specific positions
            val specificPos = when(posGroup) {
                "DF" -> listOf("CB", "LB", "RB").random()
                "MF" -> listOf("CDM", "CM", "CAM", "LM", "RM").random()
                "FW" -> listOf("ST", "CF", "RW", "LW").random()
                else -> "GK"
            }

            val player = Player(
                id = UUID.randomUUID().toString(),
                name = NameDatabase.generateNameForRegion(regionToNameDbRegion(region)),
                age = age,
                position = specificPos,
                overallRating = currentRating,
                potential = potential,
                reputation = currentRating / 100.0,
                traits = if (GameMath.chance(0.2)) TraitDatabase.getRandomTraits(1) else emptyList()
            )

            team.players.add(player)
        }
    }

    private fun regionToNameDbRegion(leagueRegion: String): String {
        return when(leagueRegion) {
            "England", "Spain", "Germany", "Italy", "France", "Portugal", "Netherlands", "Belgium", "Poland", "Sweden", "Norway", "Denmark", "Austria", "Switzerland", "Scotland", "Greece", "Russia", "Turkey" -> "Europe"
            "Brazil", "Argentina" -> "SouthAmerica"
            "Japan", "South Korea", "China", "Saudi Arabia" -> "Asia"
            "USA", "Mexico" -> "NorthAmerica" // Mapped to Europe in NameDb for now or add NorthAmerica
            else -> "Europe"
        }
    }
}
