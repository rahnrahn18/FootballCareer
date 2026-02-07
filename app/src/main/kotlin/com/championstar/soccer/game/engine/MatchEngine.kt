package com.championstar.soccer.game.engine

import com.championstar.soccer.data.model.*
import com.championstar.soccer.game.league.LeagueManager
import kotlin.random.Random

object MatchEngine {

    private var currentMinute = 0
    private var playerScore = 0
    private var opponentScore = 0
    private lateinit var player: Player
    private lateinit var opponent: Club
    private var leagueId: Int = -1

    val commentaryLog = mutableListOf<MatchCommentary>()

    fun startMatch(player: Player, opponent: Club, leagueId: Int) {
        this.player = player
        this.opponent = opponent
        this.leagueId = leagueId
        currentMinute = 0
        playerScore = 0
        opponentScore = 0
        commentaryLog.clear()
        commentaryLog.add(MatchCommentary(0, "Kick-off! The match between ${player.club.name} and ${opponent.name} has begun."))
    }

    fun simulateMinute(): KeyMoment? {
        currentMinute++
        if (currentMinute > 90) {
            endMatch()
            return null
        }

        // FOUNDATION: Opponent scoring logic
        val opponentAttackChance = opponent.reputation / 1500.0 // e.g., 85 rep = ~5.6% chance per minute
        if (Random.nextDouble() < opponentAttackChance) {
            val playerClubDefense = (player.club.reputation + player.attributes.technical.tackling) / 2.0
            val opponentAttack = opponent.reputation * (1 + Random.nextDouble(-0.2, 0.2)) // Add variability
            if (opponentAttack > playerClubDefense) {
                opponentScore++
                commentaryLog.add(MatchCommentary(currentMinute, "Goal for ${opponent.name}! They've broken through the defense."))
            }
        }

        // Chance for a key moment to occur (e.g., 10% each minute)
        if (Random.nextDouble() < 0.10) {
            return generateKeyMoment()
        }

        commentaryLog.add(MatchCommentary(currentMinute, "The ball is being passed around in the midfield."))
        return null
    }

    private fun generateKeyMoment(): KeyMoment {
        return KeyMoment(
            minute = currentMinute,
            description = "You've found space outside the box and have a clear sight of goal!",
            choices = listOf(
                PlayerChoice("Power shot to the corner", BuffType.FINISHING, BuffType.STAMINA),
                PlayerChoice("Finesse shot around the keeper", BuffType.DRIBBLING, BuffType.FINISHING),
                PlayerChoice("Look for a pass to a teammate", BuffType.TACKLING, BuffType.NONE)
            )
        )
    }

    fun resolvePlayerChoice(choice: PlayerChoice): Boolean {
        val primaryStat = getAttributeValue(choice.primaryAttribute)
        val secondaryStat = getAttributeValue(choice.secondaryAttribute)
        val successThreshold = (primaryStat + secondaryStat) / 2.0
        val diceRoll = Random.nextInt(1, 100)
        val success = diceRoll < successThreshold

        if (success) {
            playerScore++
            commentaryLog.add(MatchCommentary(currentMinute, "GOAL! A brilliant decision by ${player.profile.name} pays off!"))
            player.careerStats.goals++ // Track stats
        } else {
            commentaryLog.add(MatchCommentary(currentMinute, "Oh, what a miss! The chance goes begging."))
        }
        return success
    }

    private fun getAttributeValue(type: BuffType): Int {
        return when (type) {
            BuffType.FINISHING -> player.attributes.technical.finishing
            BuffType.SPEED -> player.attributes.physical.sprintSpeed
            BuffType.DRIBBLING -> player.attributes.technical.dribbling
            BuffType.STAMINA -> player.attributes.physical.stamina
            BuffType.TACKLING -> player.attributes.technical.tackling
            else -> 5
        }
    }

    fun getScore(): Pair<Int, Int> = Pair(playerScore, opponentScore)

    fun endMatch() {
        if (leagueId != -1) {
            LeagueManager.recordMatch(leagueId, player.club.id, opponent.id, playerScore, opponentScore)
        }
        player.careerStats.appearances++ // Track stats
    }
}
