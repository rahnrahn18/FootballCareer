package com.championstar.soccer.simulation.engine

import com.championstar.soccer.domain.models.*
import java.util.Collections

object LeagueEngine {

    /**
     * Simulates all matches for the current week across all leagues.
     * Skips the match involving the user player (handled separately by TimeEngine),
     * or returns it if we want to integrate.
     *
     * @param leagues List of all leagues to simulate.
     * @param userTeamId The ID of the team the user plays for (to skip or handle specially).
     */
    fun simulateWeek(leagues: List<League>, userTeamId: String? = null) {
        leagues.forEach { league ->
            simulateLeagueMatchDay(league, userTeamId)
        }
    }

    private fun simulateLeagueMatchDay(league: League, userTeamId: String?) {
        val teams = league.teams
        if (teams.size < 2) return

        // Round Robin Scheduling Logic
        val pairings = getPairings(teams, league.currentMatchday)

        pairings.forEach { (home, away) ->
            // Skip User Match if handled externally
            if (userTeamId != null && (home.id == userTeamId || away.id == userTeamId)) {
                return@forEach
            }

            val result = MatchEngine.simulateMatch(home, away)
            updateTeamStats(home, away, result)
        }

        league.currentMatchday++
    }

    /**
     * Helper to get the scheduled opponent for a specific team in the current matchday.
     */
    fun getScheduledOpponent(league: League, team: Team): Team? {
        val pairings = getPairings(league.teams, league.currentMatchday)
        val match = pairings.find { it.first.id == team.id || it.second.id == team.id }
        return if (match?.first?.id == team.id) match.second else match?.first
    }

    private fun getPairings(teams: MutableList<Team>, matchday: Int): List<Pair<Team, Team>> {
        val rotatedTeams = teams.toMutableList()
        val rotation = (matchday - 1) % (teams.size - 1)

        if (rotation > 0) {
            val subList = rotatedTeams.subList(1, rotatedTeams.size)
            Collections.rotate(subList, rotation)
        }

        val halfSize = teams.size / 2
        val pairings = mutableListOf<Pair<Team, Team>>()

        for (i in 0 until halfSize) {
            val home = rotatedTeams[i]
            val away = rotatedTeams[teams.size - 1 - i]
            pairings.add(home to away)
        }
        return pairings
    }

    fun updateTeamStats(home: Team, away: Team, result: MatchResult) {
        // 1. Update Home Stats
        home.leagueStats.played++
        home.leagueStats.goalsFor += result.homeScore
        home.leagueStats.goalsAgainst += result.awayScore

        // 2. Update Away Stats
        away.leagueStats.played++
        away.leagueStats.goalsFor += result.awayScore
        away.leagueStats.goalsAgainst += result.homeScore

        // 3. Result Logic
        if (result.homeScore > result.awayScore) {
            // Home Win
            home.leagueStats.won++
            home.leagueStats.points += 3
            updateForm(home, MatchOutcome.WIN)

            away.leagueStats.lost++
            updateForm(away, MatchOutcome.LOSS)
        } else if (result.homeScore < result.awayScore) {
            // Away Win
            away.leagueStats.won++
            away.leagueStats.points += 3
            updateForm(away, MatchOutcome.WIN)

            home.leagueStats.lost++
            updateForm(home, MatchOutcome.LOSS)
        } else {
            // Draw
            home.leagueStats.drawn++
            home.leagueStats.points += 1
            updateForm(home, MatchOutcome.DRAW)

            away.leagueStats.drawn++
            away.leagueStats.points += 1
            updateForm(away, MatchOutcome.DRAW)
        }
    }

    private fun updateForm(team: Team, outcome: MatchOutcome) {
        team.leagueStats.form.add(0, outcome)
        if (team.leagueStats.form.size > 5) {
            team.leagueStats.form.removeAt(5)
        }
    }
}
