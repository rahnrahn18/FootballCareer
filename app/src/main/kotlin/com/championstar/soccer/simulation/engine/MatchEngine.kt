package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.Localization
import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.*
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Massive Match Engine Simulation Logic.
 * Handles every aspect of the match simulation including tactics, stamina, injuries, VAR, and detailed events.
 */
object MatchEngine {

    // --- CONSTANTS ---
    private const val BASE_RATING = 6.0
    private const val HOME_ADVANTAGE = 1.10 // 10% boost to home team stats
    private const val STAMINA_DRAIN_PER_MIN = 0.6 // Base drain
    private const val STAMINA_RECOVERY_HT = 15.0 // Half-time recovery
    private const val INJURY_RISK_BASE = 0.0005 // 0.05% per minute
    private const val INJURY_RISK_TIRED = 0.005 // 0.5% per minute if tired

    // --- TACTICS & STRATEGIES ---
    enum class Tactic {
        PARK_THE_BUS, // Ultra Defensive
        DEFENSIVE,    // Counter Attack
        BALANCED,     // Standard
        ATTACKING,    // Possession
        ALL_OUT_ATTACK // Gung-ho
    }

    enum class Aggression {
        CAUTIOUS, // No cards, less tackles
        NORMAL,
        AGGRESSIVE // Hard tackles, more cards, more injuries
    }

    // --- STATE ---
    data class InteractiveMatchState(
        val matchId: String = UUID.randomUUID().toString(),
        val homeTeam: Team,
        val awayTeam: Team,

        // Squads (Separated for logic)
        val homeLineup: MutableList<Player> = mutableListOf(),
        val homeBench: MutableList<Player> = mutableListOf(),
        val awayLineup: MutableList<Player> = mutableListOf(),
        val awayBench: MutableList<Player> = mutableListOf(),

        // Time
        var minute: Int = 0,
        var addedTimeFirstHalf: Int = 0,
        var addedTimeSecondHalf: Int = 0,
        var isSecondHalf: Boolean = false,
        var isFinished: Boolean = false,

        // Score
        var homeScore: Int = 0,
        var awayScore: Int = 0,

        // Stats
        var homePossessionTicks: Int = 0,
        var awayPossessionTicks: Int = 0,
        var homeShots: Int = 0,
        var awayShots: Int = 0,
        var homeOnTarget: Int = 0,
        var awayOnTarget: Int = 0,
        var homeCorners: Int = 0,
        var awayCorners: Int = 0,
        var homeFouls: Int = 0,
        var awayFouls: Int = 0,
        var homeYellows: Int = 0,
        var awayYellows: Int = 0,
        var homeReds: Int = 0,
        var awayReds: Int = 0,
        var homeSubsUsed: Int = 0,
        var awaySubsUsed: Int = 0,

        // Dynamic State
        var homeTactic: Tactic = Tactic.BALANCED,
        var awayTactic: Tactic = Tactic.BALANCED,
        var homeAggression: Aggression = Aggression.NORMAL,
        var awayAggression: Aggression = Aggression.NORMAL,

        // Lists
        val events: MutableList<MatchEvent> = mutableListOf(),
        val playerRatings: MutableMap<String, Double> = mutableMapOf(), // ID -> Rating
        val playerStamina: MutableMap<String, Double> = mutableMapOf(), // ID -> Current Stamina
        val injuredPlayers: MutableList<String> = mutableListOf(), // IDs of injured players
        val sentOffPlayers: MutableList<String> = mutableListOf(), // IDs of red carded players

        // Context
        var lastPossessorId: String? = null,
        var momentum: Double = 0.0 // -1.0 (Away) to 1.0 (Home)
    )

    // --- INITIALIZATION ---

    fun createInteractiveMatch(homeTeam: Team, awayTeam: Team): InteractiveMatchState {
        val state = InteractiveMatchState(homeTeam = homeTeam, awayTeam = awayTeam)

        // Initialize Squads
        val homeSquad = SquadEngine.selectMatchSquad(homeTeam)
        val awaySquad = SquadEngine.selectMatchSquad(awayTeam)

        state.homeLineup.addAll(homeSquad.starters)
        state.homeBench.addAll(homeSquad.substitutes)
        state.awayLineup.addAll(awaySquad.starters)
        state.awayBench.addAll(awaySquad.substitutes)

        // Initialize Ratings & Stamina (For everyone in squad)
        (state.homeLineup + state.homeBench + state.awayLineup + state.awayBench).forEach { player ->
            state.playerRatings[player.id] = BASE_RATING
            state.playerStamina[player.id] = player.stamina // Start with current stamina
        }

        // Set Initial Tactics based on Team Strength/Reputation
        // If Home is much stronger -> Attack
        val homeStr = calculateTeamStrength(state.homeLineup)
        val awayStr = calculateTeamStrength(state.awayLineup)

        state.homeTactic = if (homeStr > awayStr * 1.1) Tactic.ATTACKING else Tactic.BALANCED
        state.awayTactic = if (awayStr > homeStr * 1.1) Tactic.DEFENSIVE else Tactic.BALANCED

        // Calculate Added Time (Pre-calc for simplicity, or dynamic)
        state.addedTimeFirstHalf = GameMath.nextInt(1, 4)
        state.addedTimeSecondHalf = GameMath.nextInt(3, 8)

        return state
    }

    // --- SIMULATION LOOP (PER MINUTE) ---

    fun simulateMinute(state: InteractiveMatchState): List<MatchEvent> {
        if (state.isFinished) return emptyList()

        val eventsThisMinute = mutableListOf<MatchEvent>()

        // Handle Half Time
        if (state.minute == 45 + state.addedTimeFirstHalf && !state.isSecondHalf) {
            state.isSecondHalf = true
            state.minute = 45
            // Recovery
            state.playerStamina.keys.forEach { id ->
                state.playerStamina[id] = (state.playerStamina[id]!! + STAMINA_RECOVERY_HT).coerceAtMost(100.0)
            }
            eventsThisMinute.add(createEvent(state.minute, EventType.PASS_KEY, "", null, "", MatchCommentary.getHalfTimeCommentary()))
            return eventsThisMinute
        }

        // Check Full Time
        if (state.minute >= 90 + state.addedTimeSecondHalf) {
            state.isFinished = true
            finalizeInteractiveMatch(state)
            return emptyList()
        }

        state.minute++

        // 1. Update Stamina & Check Injuries
        updatePhysicalCondition(state, eventsThisMinute)

        // 2. AI Tactical Shifts
        updateTacticsAI(state)

        // 3. Possession Calculation
        val homeMid = calculateSectorStrength(state.homeLineup, "MF", state) * getTacticPossessionMod(state.homeTactic)
        val awayMid = calculateSectorStrength(state.awayLineup, "MF", state) * getTacticPossessionMod(state.awayTactic)

        // Home Advantage & Momentum
        val momentumFactor = 1.0 + (state.momentum * 0.2) // +/- 20%
        val homeProb = ((homeMid * HOME_ADVANTAGE * momentumFactor) / (homeMid * HOME_ADVANTAGE * momentumFactor + awayMid)).coerceIn(0.2, 0.8)

        val isHomePossession = GameMath.nextDouble() < homeProb
        if (isHomePossession) state.homePossessionTicks++ else state.awayPossessionTicks++

        val attackingLineup = if (isHomePossession) state.homeLineup else state.awayLineup
        val defendingLineup = if (isHomePossession) state.awayLineup else state.homeLineup
        val attackingTeam = if (isHomePossession) state.homeTeam else state.awayTeam
        val defendingTeam = if (isHomePossession) state.awayTeam else state.homeTeam

        val atkState = if (isHomePossession) state.homeTactic else state.awayTactic
        val defState = if (isHomePossession) state.awayTactic else state.homeTactic

        // 4. Action Simulation
        // Flow: Midfield -> Attack -> Chance -> Goal

        // A. Build Up
        val buildUpSuccess = GameMath.chance(0.6 + (getTacticAttackingMod(atkState) * 0.1))
        if (!buildUpSuccess) {
            // Turnover in midfield
            // Chance of foul?
            if (GameMath.chance(0.05)) {
                handleFoul(state, defendingTeam, defendingLineup, attackingTeam, attackingLineup, eventsThisMinute, "Midfield")
            }
            return eventsThisMinute
        }

        // B. Final Third Entry
        val defenseStr = calculateSectorStrength(defendingLineup, "DF", state) * getTacticDefensiveMod(defState)
        val attackStr = calculateSectorStrength(attackingLineup, "FW", state) * getTacticAttackingMod(atkState)

        val chanceRatio = attackStr / (attackStr + defenseStr)
        if (GameMath.nextDouble() > chanceRatio) {
             // Defense clears
             if (GameMath.chance(0.1)) {
                 // Corner Kick?
                 if (isHomePossession) state.homeCorners++ else state.awayCorners++
                 eventsThisMinute.add(createEvent(state.minute, EventType.CORNER, "", null, attackingTeam.id, MatchCommentary.getCornerCommentary(attackingTeam.name)))
                 // Corner routine... (Simplified: 10% goal chance)
                 if (GameMath.chance(0.1)) {
                     handleGoalChance(state, attackingTeam, attackingLineup, defendingTeam, defendingLineup, eventsThisMinute, isHeader = true)
                 }
             }
             return eventsThisMinute
        }

        // C. Goal Scoring Opportunity
        // Who shoots?
        val shooter = selectPlayerByRole(attackingLineup, "FW", "MF")
        state.lastPossessorId = shooter.id

        if (isHomePossession) state.homeShots++ else state.awayShots++

        // Shot Quality vs GK
        val gk = selectPlayerByRole(defendingLineup, "GK")
        val shotQuality = (shooter.overallRating * GameMath.nextDouble(0.8, 1.2))
        val saveQuality = (gk.overallRating * GameMath.nextDouble(0.8, 1.2))

        if (shotQuality > saveQuality * 0.8) {
             // On Target
             if (isHomePossession) state.homeOnTarget++ else state.awayOnTarget++

             if (shotQuality > saveQuality) {
                 // GOAL!
                 handleGoal(state, attackingTeam, defendingTeam, attackingLineup, shooter, eventsThisMinute)
             } else {
                 // SAVE!
                 val isSpectacular = shotQuality > saveQuality * 0.95
                 updateRating(state.playerRatings, gk.id, if (isSpectacular) 0.8 else 0.5)
                 eventsThisMinute.add(createEvent(state.minute, EventType.SAVE, gk.id, shooter.id, defendingTeam.id,
                     MatchCommentary.getSaveCommentary(gk.name, shooter.name, isSpectacular)))
             }
        } else {
            // Miss
            val isClose = shotQuality > saveQuality * 0.6
            eventsThisMinute.add(createEvent(state.minute, EventType.PASS_KEY, shooter.id, null, attackingTeam.id,
                MatchCommentary.getMissCommentary(shooter.name, false, isClose)))
        }

        return eventsThisMinute
    }

    // --- SUBSTITUTION ---

    fun performSubstitution(state: InteractiveMatchState, team: Team, outPlayerId: String, inPlayerId: String): Boolean {
        val isHome = team.id == state.homeTeam.id
        val lineup = if (isHome) state.homeLineup else state.awayLineup
        val bench = if (isHome) state.homeBench else state.awayBench
        val usedSubs = if (isHome) state.homeSubsUsed else state.awaySubsUsed

        if (usedSubs >= 5) return false // Rule: 5 Subs

        val outPlayer = lineup.find { it.id == outPlayerId }
        val inPlayer = bench.find { it.id == inPlayerId }

        if (outPlayer != null && inPlayer != null) {
            lineup.remove(outPlayer)
            lineup.add(inPlayer)
            bench.remove(inPlayer)
            // bench.add(outPlayer) // Actually, subbed out players stay on bench or go to "Subbed Out" list?
            // Typically they can't come back in.
            // For simplicity, remove from bench so they can't be selected again.

            if (isHome) state.homeSubsUsed++ else state.awaySubsUsed++

            state.events.add(createEvent(state.minute, EventType.SUBSTITUTION, inPlayer.id, outPlayer.id, team.id,
                "Substitution: ${inPlayer.name} IN, ${outPlayer.name} OUT"))
            return true
        }
        return false
    }

    // --- EVENT HANDLERS ---

    private fun handleGoal(state: InteractiveMatchState, scoringTeam: Team, concedingTeam: Team, scoringLineup: List<Player>, scorer: Player, events: MutableList<MatchEvent>) {
        // VAR Check (10% chance)
        if (GameMath.chance(0.1)) {
            events.add(createEvent(state.minute, EventType.VAR_CHECK, "", null, "", MatchCommentary.getVarCheckCommentary()))
            if (GameMath.chance(0.3)) { // 30% chance of being disallowed
                events.add(createEvent(state.minute, EventType.OFFSIDE, scorer.id, null, scoringTeam.id, MatchCommentary.getVarDecisionCommentary(false)))
                return
            }
             events.add(createEvent(state.minute, EventType.VAR_CHECK, "", null, "", MatchCommentary.getVarDecisionCommentary(true)))
        }

        if (scoringTeam == state.homeTeam) state.homeScore++ else state.awayScore++

        // Assist?
        val assister = if (GameMath.chance(0.7)) selectPlayerByRole(scoringLineup, "MF", "FW") else null
        if (assister != null && assister.id == scorer.id) { /* No self assist */ }

        val desc = MatchCommentary.getGoalCommentary(
            scorer.name,
            assister?.name,
            state.minute,
            state.homeScore,
            state.awayScore,
            scoringTeam == state.homeTeam
        )

        events.add(createEvent(state.minute, EventType.GOAL, scorer.id, assister?.id, scoringTeam.id, desc))

        updateRating(state.playerRatings, scorer.id, 1.5)
        assister?.let { updateRating(state.playerRatings, it.id, 0.8) }

        // Momentum Swing
        state.momentum = if (scoringTeam == state.homeTeam) 1.0 else -1.0

        // Add injury time?
        if (state.isSecondHalf) state.addedTimeSecondHalf++ else state.addedTimeFirstHalf++
    }

    private fun handleFoul(state: InteractiveMatchState, foulingTeam: Team, foulingLineup: List<Player>, fouledTeam: Team, fouledLineup: List<Player>, events: MutableList<MatchEvent>, location: String) {
        val fouler = selectPlayerByRole(foulingLineup, "DF", "MF")
        val fouled = selectPlayerByRole(fouledLineup, "MF", "FW")

        if (foulingTeam == state.homeTeam) state.homeFouls++ else state.awayFouls++

        // Card?
        // Aggression factor
        val agg = if (foulingTeam == state.homeTeam) state.homeAggression else state.awayAggression
        val cardChance = when(agg) {
            Aggression.CAUTIOUS -> 0.05
            Aggression.NORMAL -> 0.15
            Aggression.AGGRESSIVE -> 0.30
        }

        if (GameMath.chance(cardChance)) {
            // Yellow or Red?
            if (GameMath.chance(0.1)) { // 10% Red
                handleRedCard(state, foulingTeam, fouler, events)
            } else {
                handleYellowCard(state, foulingTeam, fouler, events)
            }
        } else {
            // Just a foul
            events.add(createEvent(state.minute, EventType.FOUL, fouler.id, fouled.id, foulingTeam.id,
                MatchCommentary.getFoulCommentary(fouler.name, fouled.name, location)))
        }
    }

    private fun handleYellowCard(state: InteractiveMatchState, team: Team, player: Player, events: MutableList<MatchEvent>) {
        if (team == state.homeTeam) state.homeYellows++ else state.awayYellows++

        events.add(createEvent(state.minute, EventType.YELLOW_CARD, player.id, null, team.id, MatchCommentary.getCardCommentary(player.name, false)))
        updateRating(state.playerRatings, player.id, -0.5)
    }

    private fun handleRedCard(state: InteractiveMatchState, team: Team, player: Player, events: MutableList<MatchEvent>) {
        if (state.sentOffPlayers.contains(player.id)) return

        if (team == state.homeTeam) state.homeReds++ else state.awayReds++
        state.sentOffPlayers.add(player.id)

        events.add(createEvent(state.minute, EventType.RED_CARD, player.id, null, team.id, MatchCommentary.getCardCommentary(player.name, true)))
        updateRating(state.playerRatings, player.id, -2.0)
    }

    private fun handleGoalChance(state: InteractiveMatchState, att: Team, attLineup: List<Player>, def: Team, defLineup: List<Player>, events: MutableList<MatchEvent>, isHeader: Boolean = false) {
        val scorer = selectPlayerByRole(attLineup, "DF", "FW") // Defenders come up for corners
        if (GameMath.chance(0.15)) { // Low conversion on corners
            handleGoal(state, att, def, attLineup, scorer, events)
        } else {
             val isClose = GameMath.chance(0.4)
             events.add(createEvent(state.minute, EventType.PASS_KEY, scorer.id, null, att.id, MatchCommentary.getMissCommentary(scorer.name, true, isClose)))
        }
    }

    // --- HELPERS ---

    private fun updatePhysicalCondition(state: InteractiveMatchState, events: MutableList<MatchEvent>) {
        // Only iterate ACTIVE lineups
        val allPlayers = state.homeLineup + state.awayLineup
        allPlayers.forEach { player ->
            if (state.sentOffPlayers.contains(player.id)) return@forEach
            // Drain
            var drain = STAMINA_DRAIN_PER_MIN
            val agg = if (state.homeLineup.contains(player)) state.homeAggression else state.awayAggression
            if (agg == Aggression.AGGRESSIVE) drain *= 1.2
            if (agg == Aggression.CAUTIOUS) drain *= 0.8

            val currentStamina = state.playerStamina[player.id] ?: 100.0
            val newStamina = (currentStamina - drain).coerceAtLeast(0.0)
            state.playerStamina[player.id] = newStamina

            // Injury Check
            val risk = if (newStamina < 30.0) INJURY_RISK_TIRED else INJURY_RISK_BASE
            if (!state.injuredPlayers.contains(player.id) && GameMath.chance(risk)) {
                state.injuredPlayers.add(player.id)
                events.add(createEvent(state.minute, EventType.INJURY, player.id, null,
                    if (state.homeLineup.contains(player)) state.homeTeam.id else state.awayTeam.id,
                    MatchCommentary.getInjuryCommentary(player.name)))
                if (state.isSecondHalf) state.addedTimeSecondHalf += 2 else state.addedTimeFirstHalf += 2
                // Force sub? Or just play with 10 men if no subs?
                // For now, they stay on pitch but useless/drain logic might continue.
                // In real implementation, auto-sub or user prompt.
            }
        }
    }

    private fun updateTacticsAI(state: InteractiveMatchState) {
        // Late game (75+) Logic
        if (state.minute > 75) {
            val gd = state.homeScore - state.awayScore
            if (gd < 0) { // Home losing
                state.homeTactic = Tactic.ALL_OUT_ATTACK
                state.homeAggression = Aggression.AGGRESSIVE
                state.awayTactic = Tactic.PARK_THE_BUS
            } else if (gd > 0) { // Home winning
                state.homeTactic = Tactic.DEFENSIVE
                state.awayTactic = Tactic.ALL_OUT_ATTACK
                state.awayAggression = Aggression.AGGRESSIVE
            }
        }
    }

    private fun calculateTeamStrength(lineup: List<Player>): Double {
        return if (lineup.isNotEmpty()) lineup.map { it.overallRating }.average() else 0.0
    }

    private fun calculateSectorStrength(lineup: List<Player>, sector: String, state: InteractiveMatchState): Double {
        // Only active players (not red carded)
        val players = lineup.filter {
            !state.sentOffPlayers.contains(it.id) && !state.injuredPlayers.contains(it.id)
        }.filter {
            when (sector) {
                "GK" -> it.position == "GK"
                "DEF" -> it.position.contains("B")
                "MF" -> it.position.contains("M")
                "FW" -> it.position.contains("F") || it.position.contains("W") || it.position.contains("ST")
                else -> false
            }
        }
        if (players.isEmpty()) return 30.0 // Penalty for empty sector (red cards)
        return players.map { it.overallRating * (state.playerStamina[it.id] ?: 100.0) / 100.0 }.average()
    }

    // ... Decision Logic ... (Same)
    data class DecisionContext(
        val title: String,
        val description: String,
        val options: List<DecisionOption>
    )

    data class DecisionOption(
        val text: String,
        val outcomeDescription: String,
        val successChance: Double,
        val rewardRating: Double,
        val riskRating: Double
    )

    fun generateDecision(player: Player, positionGroup: String): DecisionContext {
        // Expanded logic could go here
         return when (positionGroup) {
            "FW", "ATT" -> DecisionContext(
                Localization.get(Localization.DECISION_TITLE_GOAL),
                Localization.get(Localization.DECISION_DESC_GOAL),
                listOf(
                    DecisionOption(Localization.get(Localization.DECISION_OPT_CHIP), "Beautiful chip!", 0.4, 1.5, -0.5),
                    DecisionOption(Localization.get(Localization.DECISION_OPT_POWER), "Smashed it!", 0.6, 1.0, -0.2),
                    DecisionOption(Localization.get(Localization.DECISION_OPT_PASS), "Unselfish.", 0.8, 0.5, -0.1)
                )
            )
            else -> DecisionContext("Opportunity", "Make a choice", listOf(DecisionOption("Try", "Ok", 0.5, 0.5, 0.0)))
        }
    }

    fun processDecision(state: InteractiveMatchState, player: Player, option: DecisionOption): Boolean {
        val isSuccess = GameMath.nextDouble() < option.successChance
        if (isSuccess) {
            updateRating(state.playerRatings, player.id, option.rewardRating)
            if (option.text.contains("Shot") || option.text.contains("Power") || option.text.contains("Chip")) {
                 val team = if (state.homeLineup.any { it.id == player.id }) state.homeTeam else state.awayTeam
                 if (team == state.homeTeam) state.homeScore++ else state.awayScore++
                 state.events.add(createEvent(state.minute, EventType.GOAL, player.id, null, team.id, "${player.name} scores (User)!"))
            }
        } else {
            updateRating(state.playerRatings, player.id, option.riskRating)
        }
        return isSuccess
    }

    // --- UTILS ---

    private fun getTacticPossessionMod(tactic: Tactic): Double {
        return when(tactic) {
            Tactic.PARK_THE_BUS -> 0.6
            Tactic.DEFENSIVE -> 0.8
            Tactic.BALANCED -> 1.0
            Tactic.ATTACKING -> 1.2
            Tactic.ALL_OUT_ATTACK -> 1.3
        }
    }

    private fun getTacticAttackingMod(tactic: Tactic): Double = when(tactic) {
        Tactic.PARK_THE_BUS -> 0.5
        Tactic.ALL_OUT_ATTACK -> 1.5
        else -> 1.0
    }

    private fun getTacticDefensiveMod(tactic: Tactic): Double = when(tactic) {
        Tactic.PARK_THE_BUS -> 1.5
        Tactic.ALL_OUT_ATTACK -> 0.5
        else -> 1.0
    }

    private fun selectPlayerByRole(lineup: List<Player>, primaryRole: String, secondaryRole: String? = null): Player {
        val candidates = lineup.filter {
            val pRole = if (primaryRole == "FW") "F" else if (primaryRole == "DF") "B" else primaryRole
            val sRole = if (secondaryRole == "FW") "F" else if (secondaryRole == "DF") "B" else secondaryRole
            it.position.contains(pRole) || (sRole != null && it.position.contains(sRole))
        }
        return if (candidates.isNotEmpty()) candidates.random() else lineup.random()
    }

    private fun updateRating(ratings: MutableMap<String, Double>, playerId: String, change: Double) {
        val current = ratings[playerId] ?: BASE_RATING
        ratings[playerId] = (current + change).coerceIn(1.0, 10.0)
    }

    private fun createEvent(minute: Int, type: EventType, pId: String, pId2: String?, tId: String, desc: String): MatchEvent {
        return MatchEvent(minute, type, pId, pId2, tId, desc)
    }

    fun finalizeInteractiveMatch(state: InteractiveMatchState): MatchResult {
        // Update Stats
        updatePlayerHistory(state.homeTeam, state.matchId, state.awayTeam.name, state.playerRatings, state.events)
        updatePlayerHistory(state.awayTeam, state.matchId, state.homeTeam.name, state.playerRatings, state.events)

        // Return Result
        return MatchResult(
            matchId = state.matchId,
            homeTeam = state.homeTeam,
            awayTeam = state.awayTeam,
            homeScore = state.homeScore,
            awayScore = state.awayScore,
            homePossession = if (state.homePossessionTicks + state.awayPossessionTicks > 0)
                             (state.homePossessionTicks.toDouble() / (state.homePossessionTicks + state.awayPossessionTicks) * 100).toInt()
                             else 50,
            awayPossession = 100 - ((state.homePossessionTicks.toDouble() / (state.homePossessionTicks + state.awayPossessionTicks) * 100).toInt()),
            homeShots = state.homeShots,
            awayShots = state.awayShots,
            homeOnTarget = state.homeOnTarget,
            awayOnTarget = state.awayOnTarget,
            events = state.events,
            playerRatings = state.playerRatings,
            isFinished = true,
            homeSquad = MatchSquad(state.homeLineup, state.homeBench),
            awaySquad = MatchSquad(state.awayLineup, state.awayBench)
        )
    }

    private fun updatePlayerHistory(team: Team, matchId: String, opponentName: String, ratings: Map<String, Double>, events: List<MatchEvent>) {
        team.players.forEach { player ->
            val rating = ratings[player.id] ?: BASE_RATING

            val goals = events.count { it.playerId == player.id && it.type == EventType.GOAL }
            val assists = events.count { it.secondaryPlayerId == player.id && it.type == EventType.GOAL }

            val historyItem = MatchPerformance(
                matchId = matchId,
                opponentName = opponentName,
                rating = rating,
                goals = goals,
                assists = assists,
                minutesPlayed = 90, // Simplified
                events = events.filter { it.playerId == player.id || it.secondaryPlayerId == player.id }.map { it.description }
            )
            player.matchHistory.add(0, historyItem)

            // Season stats
            player.seasonStats.goals += goals
            player.seasonStats.assists += assists
            player.seasonStats.appearances++
            val oldSum = player.seasonStats.averageRating * player.seasonStats.ratingCount
            player.seasonStats.ratingCount++
            player.seasonStats.averageRating = (oldSum + rating) / player.seasonStats.ratingCount
        }
    }
}
