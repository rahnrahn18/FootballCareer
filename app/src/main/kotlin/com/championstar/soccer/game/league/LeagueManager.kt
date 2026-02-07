package com.championstar.soccer.game.league

import com.championstar.soccer.data.model.Fixture
import com.championstar.soccer.data.model.League
import com.championstar.soccer.data.model.LeagueStanding
import java.time.LocalDate

object LeagueManager {

    private val tables = mutableMapOf<Int, List<LeagueStanding>>()
    private val fixtures = mutableMapOf<Int, List<Fixture>>()

    fun initialize(leagues: List<League>) {
        tables.clear()
        fixtures.clear()
        val seasonStartDate = LocalDate.of(2025, 8, 10)

        leagues.forEach { league ->
            val standings = league.clubs.map { club ->
                LeagueStanding(clubId = club.id.toString(), clubName = club.name)
            }
            tables[league.id] = standings
            fixtures[league.id] = FixtureGenerator.generateFixtures(league.clubs, seasonStartDate)
        }
    }

    fun getFixturesForClub(clubId: Int): List<Fixture> {
        val allFixtures = fixtures.values.flatten()
        return allFixtures.filter { it.homeClubId == clubId || it.awayClubId == clubId }
    }

    fun getTableForLeague(leagueId: Int): List<LeagueStanding> {
        // Mengurutkan berdasarkan Poin (Pts), lalu Selisih Gol (GD), lalu Gol Dicetak (GF)
        return tables[leagueId]?.sortedWith(
            compareByDescending<LeagueStanding> { it.pts }
                .thenByDescending { it.goalsFor - it.goalsAgainst }
                .thenByDescending { it.goalsFor }
        ) ?: emptyList()
    }

    fun recordMatch(leagueId: Int, homeClubId: Int, awayClubId: Int, homeGoals: Int, awayGoals: Int) {
        // 1. Ambil data dan buat salinan yang bisa diubah
        val leagueTable = tables[leagueId]?.toMutableList() ?: return

        fun updateStandings(clubId: Int, goalsFor: Int, goalsAgainst: Int) {
            val index = leagueTable.indexOfFirst { it.clubId == clubId.toString() }
            if (index != -1) {
                val standing = leagueTable[index]
                standing.played++
                standing.goalsFor += goalsFor
                standing.goalsAgainst += goalsAgainst
                when {
                    goalsFor > goalsAgainst -> {
                        standing.won++
                        standing.pts += 3
                    }
                    goalsFor == goalsAgainst -> {
                        standing.drawn++
                        standing.pts += 1
                    }
                    else -> {
                        standing.lost++
                    }
                }
                leagueTable[index] = standing
            }
        }

        // 2. Update statistik untuk kedua klub pada salinan
        updateStandings(homeClubId, homeGoals, awayGoals)
        updateStandings(awayClubId, awayGoals, homeGoals)

        // 3. *** INI ADALAH PERBAIKANNYA ***
        // Simpan kembali salinan yang sudah diperbarui ke data utama.
        tables[leagueId] = leagueTable
    }
}