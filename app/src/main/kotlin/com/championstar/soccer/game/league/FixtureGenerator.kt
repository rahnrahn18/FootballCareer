package com.championstar.soccer.game.league

import com.championstar.soccer.data.model.Club
import com.championstar.soccer.data.model.Fixture
import java.time.LocalDate

object FixtureGenerator {

    fun generateFixtures(clubs: List<Club>, seasonStartDate: LocalDate): List<Fixture> {
        val fixtures = mutableListOf<Fixture>()
        if (clubs.size < 2) return fixtures

        val clubIds = clubs.map { it.id }.toMutableList()
        // Handle odd number of teams by adding a dummy "bye" team
        if (clubIds.size % 2 != 0) {
            clubIds.add(-1) // Bye team ID
        }

        val numRounds = clubIds.size - 1
        val halfSeasonRounds = numRounds
        var matchDate = seasonStartDate

        // Generate first half of the season
        for (round in 0 until halfSeasonRounds) {
            for (i in 0 until clubIds.size / 2) {
                val homeId = clubIds[i]
                val awayId = clubIds[clubIds.size - 1 - i]

                if (homeId != -1 && awayId != -1) { // Don't create a match for the bye team
                    fixtures.add(Fixture(matchDate, homeId, awayId))
                }
            }
            // Rotate teams for the next round, keeping the first team fixed
            val last = clubIds.removeAt(clubIds.size - 1)
            clubIds.add(1, last)
            matchDate = matchDate.plusWeeks(1)
        }

        // Generate second half of the season (return fixtures)
        val returnFixtures = fixtures.map {
            it.copy(
                homeClubId = it.awayClubId,
                awayClubId = it.homeClubId,
                date = it.date.plusWeeks(halfSeasonRounds.toLong())
            )
        }
        fixtures.addAll(returnFixtures)

        return fixtures.sortedBy { it.date }
    }
}