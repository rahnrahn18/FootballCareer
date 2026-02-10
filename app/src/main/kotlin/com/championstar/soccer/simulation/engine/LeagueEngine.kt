package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.*
import java.util.Collections

/**
 * Advanced League Engine.
 * Simulates a living world of football leagues, handling match scheduling,
 * table updates, reputation dynamics, and global news generation.
 */
object LeagueEngine {

    // --- PUBLIC API ---

    /**
     * Simulates a full match week for all leagues in the game world.
     * Generates match results, updates tables, and produces news headlines.
     */
    fun simulateMatchWeek(leagues: List<League>, userTeamId: String? = null): List<String> {
        val weeklyNews = mutableListOf<String>()

        leagues.forEach { league ->
            val matchResults = simulateLeagueFixtures(league, userTeamId)

            matchResults.forEach { result ->
                updateLeagueTable(result.homeTeam, result.awayTeam, result)

                // Generate News for Big Matches or Upsets
                val news = generateMatchNews(result)
                if (news != null) weeklyNews.add(news)
            }

            // Update Dynamic Reputation based on standings
            updateTeamReputations(league)
        }

        return weeklyNews
    }

    /**
     * Finds the scheduled opponent for a specific team in the current match week.
     */
    fun getScheduledOpponent(league: League, team: Team): Team? {
        val fixtures = generateFixtures(league.teams, league.currentMatchday)
        val match = fixtures.find { it.first.id == team.id || it.second.id == team.id }
        return if (match?.first?.id == team.id) match.second else match?.first
    }

    // --- INTERNAL SIMULATION LOGIC ---

    private fun simulateLeagueFixtures(league: League, userTeamId: String?): List<MatchResult> {
        val results = mutableListOf<MatchResult>()
        val fixtures = generateFixtures(league.teams, league.currentMatchday)

        fixtures.forEach { (home, away) ->
            // Skip if user's match (handled interactively via TimeEngine/UI)
            if (userTeamId != null && (home.id == userTeamId || away.id == userTeamId)) {
                return@forEach
            }

            // Simulate Match (Using simplified Background Logic to save performance, or full engine?)
            // For "Deep Simulation", let's use a robust statistical model.
            // Full MatchEngine.simulateMinute loop for 500 matches is too slow.
            // We use a statistical approximation that respects the deep logic factors (Stamina, Rating, Form).

            val result = simulateBackgroundMatch(home, away)
            results.add(result)
        }

        league.currentMatchday++
        return results
    }

    /**
     * Deep Statistical Simulation for Background Matches.
     * Considers: Team Rating, Form, Home Advantage, Random Variance.
     */
    private fun simulateBackgroundMatch(home: Team, away: Team): MatchResult {
        val homeRating = calculateEffectiveRating(home, isHome = true)
        val awayRating = calculateEffectiveRating(away, isHome = false)

        val diff = homeRating - awayRating

        // Expected Goals (xG)
        val homeXG = (1.5 + (diff / 10.0)).coerceAtLeast(0.0)
        val awayXG = (1.0 - (diff / 10.0)).coerceAtLeast(0.0)

        // Poisson distribution for actual goals
        val homeScore = GameMath.poisson(homeXG)
        val awayScore = GameMath.poisson(awayXG)

        return MatchResult(
            matchId = "BG_${System.currentTimeMillis()}",
            homeTeam = home,
            awayTeam = away,
            homeScore = homeScore,
            awayScore = awayScore,
            isFinished = true
        )
    }

    private fun calculateEffectiveRating(team: Team, isHome: Boolean): Double {
        val base = team.players.map { it.overallRating }.average()

        // Form Modifier (Last 5 matches)
        val recentForm = team.leagueStats.form.count { it == MatchOutcome.WIN } * 1.5 +
                         team.leagueStats.form.count { it == MatchOutcome.DRAW } * 0.5
        // Max form bonus = 5 wins * 1.5 = 7.5

        // Home Advantage
        val homeBonus = if (isHome) 5.0 else 0.0

        return base + recentForm + homeBonus + GameMath.gaussian(0.0, 2.0) // Random day variance
    }

    private fun updateLeagueTable(home: Team, away: Team, result: MatchResult) {
        // Update Played
        home.leagueStats.played++
        away.leagueStats.played++

        // Update Goals
        home.leagueStats.goalsFor += result.homeScore
        home.leagueStats.goalsAgainst += result.awayScore
        away.leagueStats.goalsFor += result.awayScore
        away.leagueStats.goalsAgainst += result.homeScore

        // Update Points & Form
        if (result.homeScore > result.awayScore) {
            home.leagueStats.won++
            home.leagueStats.points += 3
            updateForm(home, MatchOutcome.WIN)

            away.leagueStats.lost++
            updateForm(away, MatchOutcome.LOSS)
        } else if (result.awayScore > result.homeScore) {
            away.leagueStats.won++
            away.leagueStats.points += 3
            updateForm(away, MatchOutcome.WIN)

            home.leagueStats.lost++
            updateForm(home, MatchOutcome.LOSS)
        } else {
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
        if (team.leagueStats.form.size > 5) team.leagueStats.form.removeAt(5)
    }

    private fun updateTeamReputations(league: League) {
        // Teams at top gain rep, teams at bottom lose rep
        val sorted = league.teams.sortedByDescending { it.leagueStats.points }
        val count = sorted.size

        sorted.forEachIndexed { index, team ->
            val percentile = 1.0 - (index.toDouble() / count)
            if (percentile > 0.8) {
                team.reputation = (team.reputation + 0.1).coerceAtMost(100.0) // Top 20% gain
            } else if (percentile < 0.2) {
                team.reputation = (team.reputation - 0.1).coerceAtLeast(0.0) // Bottom 20% lose
            }
        }
    }

    // --- NEWS GENERATION ---

    private fun generateMatchNews(result: MatchResult): String? {
        // 1. Upset? (Weak beats Strong)
        val homeRep = result.homeTeam.reputation
        val awayRep = result.awayTeam.reputation

        if (homeRep < awayRep - 15 && result.homeScore > result.awayScore) {
            return "UPSET! ${result.homeTeam.name} stuns ${result.awayTeam.name} with a ${result.homeScore}-${result.awayScore} victory!"
        }
        if (awayRep < homeRep - 15 && result.awayScore > result.homeScore) {
             return "SHOCK! ${result.awayTeam.name} defeats giants ${result.homeTeam.name} at home!"
        }

        // 2. Thrashing? (Difference >= 4 goals)
        if (result.homeScore - result.awayScore >= 4) {
            return "DEMOLITION! ${result.homeTeam.name} destroys ${result.awayTeam.name} ${result.homeScore}-${result.awayScore}."
        }

        // 3. High Scoring Draw?
        if (result.homeScore == result.awayScore && result.homeScore >= 3) {
            return "THRILLER! ${result.homeTeam.name} and ${result.awayTeam.name} share the points in a ${result.homeScore}-${result.awayScore} classic."
        }

        return null
    }

    // --- SCHEDULING HELPERS ---

    private fun generateFixtures(teams: MutableList<Team>, matchday: Int): List<Pair<Team, Team>> {
        // Simple Round Robin Rotation
        // In a real expanded engine, we'd pre-generate the schedule at season start.
        // For dynamic/prototype, we rotate on the fly.

        val rotatedTeams = teams.toMutableList()
        val numTeams = teams.size
        val numRounds = numTeams - 1

        // If odd number of teams, add a dummy? (Assuming even for now as WorldGen makes even)

        val rotation = (matchday - 1) % numRounds

        // Rotate all except first team
        if (rotation > 0) {
            val toRotate = rotatedTeams.subList(1, rotatedTeams.size)
            Collections.rotate(toRotate, -rotation) // Negative for standard circle method direction
        }

        val fixtures = mutableListOf<Pair<Team, Team>>()
        val half = numTeams / 2

        for (i in 0 until half) {
            val home = rotatedTeams[i]
            val away = rotatedTeams[numTeams - 1 - i]

            // Flip home/away for second half of season
            // Not implemented in simple rotation, but good enough for now.
            fixtures.add(home to away)
        }
        return fixtures
    }
}
