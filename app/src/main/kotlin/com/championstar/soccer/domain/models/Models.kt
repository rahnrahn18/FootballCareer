package com.championstar.soccer.domain.models

import com.championstar.soccer.data.static.TraitDatabase

// --- Currency Enums ---
enum class Currency {
    STAR,    // Earned from Achievements
    GLORY    // Premium Currency (replacing "Golden")
}

// --- Stats Enums ---
enum class MatchOutcome {
    WIN, DRAW, LOSS
}

// --- Achievement System ---
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val rewardStars: Int,
    var isUnlocked: Boolean = false
)

// --- Shop System ---
data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val currency: Currency,
    val isConsumable: Boolean = true, // Can be bought multiple times?
    val effect: (Player) -> String // Returns a message
)

// --- Stats Models ---

data class LeagueStats(
    var played: Int = 0,
    var won: Int = 0,
    var drawn: Int = 0,
    var lost: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0,
    var points: Int = 0,
    val form: MutableList<MatchOutcome> = mutableListOf() // Last 5 matches
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}

data class SeasonStats(
    var seasonYear: Int = 2024,
    var appearances: Int = 0,
    var goals: Int = 0,
    var assists: Int = 0,
    var yellowCards: Int = 0,
    var redCards: Int = 0,
    var cleanSheets: Int = 0,
    var manOfTheMatchCount: Int = 0,
    var averageRating: Double = 6.0,
    var ratingsSum: Double = 0.0, // Helper for calculation
    var ratingCount: Int = 0      // Helper for calculation
)

data class MatchPerformance(
    val matchId: String,
    val opponentName: String,
    val rating: Double,
    val goals: Int,
    val assists: Int,
    val minutesPlayed: Int,
    val events: List<String> // e.g., "Goal (12')", "Yellow Card (45')"
)

// --- Player (Updated) ---
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
    var form: Double = 50.0, // Recent performance (0-100)
    val traits: List<TraitDatabase.Trait> = emptyList(),

    // Stats (Updated Structure)
    var seasonStats: SeasonStats = SeasonStats(),
    val matchHistory: MutableList<MatchPerformance> = mutableListOf(),

    // Legacy Stats Accessors (for backward compatibility if needed, or remove)
    // For now, we rely on seasonStats.

    // Career & Transfer
    var contract: Contract? = null,
    var agent: Agent? = null,
    var marketValue: Long = 0,
    var isListedForLoan: Boolean = false,
    var isListedForTransfer: Boolean = false,

    // NEW: Currencies & Progression
    var stars: Int = 0,
    var glory: Int = 0,
    val unlockedAchievements: MutableList<String> = mutableListOf(), // IDs
    var retirementAge: Int = 35 // Default 35, can extend to 40
)

data class Contract(
    val salary: Long, // Weekly wage
    val yearsRemaining: Int,
    val releaseClause: Long = 0,
    val signingBonus: Long = 0,
    val goalBonus: Long = 0, // Bonus per goal
    val cleanSheetBonus: Long = 0, // Bonus per clean sheet (GK/DF)
    val appearanceBonus: Long = 0
)

data class Agent(
    val id: String,
    val name: String,
    var level: Int = 1, // 1 (Family) -> 10 (Super Agent)
    var negotiationSkill: Double = 1.0, // Multiplier for wage/bonuses
    var networkReach: Double = 1.0, // Multiplier for finding clubs (Tier 4 -> Tier 1)
    var scouting: Double = 1.0, // Ability to see hidden stats/potential of clubs
    val clients: MutableList<String> = mutableListOf() // IDs of other clients (for flavor)
)

data class Team(
    val id: String,
    val name: String,
    var tactics: String = "4-4-2",
    val players: MutableList<Player> = mutableListOf(),
    var budget: Long = 1000000,
    var reputation: Double = 50.0,
    var leagueId: String = "",

    // Detailed League Stats
    val leagueStats: LeagueStats = LeagueStats(),

    // Club Policy (AI Logic)
    val transferPolicy: TransferPolicy = TransferPolicy.BALANCED
)

enum class TransferPolicy {
    BALANCED,       // Mix of youth and experience
    GALACTICO,      // Buys high reputation stars
    YOUTH_DEVELOPMENT, // Buys high potential youngsters
    MONEYBALL,      // Buys undervalued players (high stats/low cost)
    RELEGATION_BATTLER // Buys experienced veterans for survival
}

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

data class MatchEvent(
    val minute: Int,
    val type: EventType,
    val playerId: String, // Who did it
    val secondaryPlayerId: String? = null, // Who assisted / was tackled
    val teamId: String,
    val description: String
)

enum class EventType {
    GOAL, ASSIST, YELLOW_CARD, RED_CARD, SUBSTITUTION, INJURY, SAVE, TACKLE, PASS_KEY, FOUL, OFFSIDE, VAR_CHECK, CORNER, FREE_KICK, PENALTY
}

data class MatchResult(
    val matchId: String,
    val homeTeam: Team,
    val awayTeam: Team,
    var homeScore: Int = 0,
    var awayScore: Int = 0,

    // Detailed Stats
    var homePossession: Int = 50,
    var awayPossession: Int = 50,
    var homeShots: Int = 0,
    var awayShots: Int = 0,
    var homeOnTarget: Int = 0,
    var awayOnTarget: Int = 0,

    val events: MutableList<MatchEvent> = mutableListOf(),
    val playerRatings: MutableMap<String, Double> = mutableMapOf(), // PlayerID -> Rating (e.g., 7.4)
    var manOfTheMatchId: String? = null,

    val isFinished: Boolean = false,
    val homeSquad: MatchSquad? = null,
    val awaySquad: MatchSquad? = null
)
