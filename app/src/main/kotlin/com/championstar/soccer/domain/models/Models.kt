package com.championstar.soccer.domain.models

import com.championstar.soccer.data.static.TraitDatabase

data class Player(
    val id: String,
    val name: String,
    var age: Int,
    var position: String, // GK, DF, MF, FW, ST, etc.
    var overallRating: Double,
    var potential: Double,
    var reputation: Double = 1.0,
    var stamina: Double = 100.0,
    var morale: Double = 100.0,
    val traits: List<TraitDatabase.Trait> = emptyList(),
    // Stats
    var goals: Int = 0,
    var assists: Int = 0,
    var appearances: Int = 0,
    var form: Double = 50.0 // Recent performance (0-100)
)

data class Team(
    val id: String,
    val name: String,
    var tactics: String = "4-4-2",
    val players: MutableList<Player> = mutableListOf(),
    var budget: Long = 1000000,
    var reputation: Double = 50.0,
    var leagueId: String = ""
)

data class League(
    val id: String,
    val name: String,
    val tier: Int = 1,
    val teams: MutableList<Team> = mutableListOf(),
    var currentMatchday: Int = 1
)

data class MatchSquad(
    val starters: List<Player>, // 11 players
    val substitutes: List<Player> // 6 players
)

data class MatchResult(
    val homeTeam: Team,
    val awayTeam: Team,
    var homeScore: Int = 0,
    var awayScore: Int = 0,
    val events: List<String> = emptyList(),
    val isFinished: Boolean = false,
    val homeSquad: MatchSquad? = null,
    val awaySquad: MatchSquad? = null
)
