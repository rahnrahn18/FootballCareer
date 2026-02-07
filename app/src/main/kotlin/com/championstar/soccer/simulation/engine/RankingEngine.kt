package com.championstar.soccer.simulation.engine

import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player

object RankingEngine {

    /**
     * Retrieves the top 100 players globally, sorted by overall rating.
     */
    fun getTop100Players(allLeagues: List<League>): List<Player> {
        return allLeagues
            .flatMap { it.teams }
            .flatMap { it.players }
            .sortedByDescending { it.overallRating }
            .take(100)
    }

    /**
     * Retrieves the top 100 players in a specific league.
     */
    fun getTopPlayersInLeague(league: League): List<Player> {
        return league.teams
            .flatMap { it.players }
            .sortedByDescending { it.overallRating }
            .take(100)
    }
}
