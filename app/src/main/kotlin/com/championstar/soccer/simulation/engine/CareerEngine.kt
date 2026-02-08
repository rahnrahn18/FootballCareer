package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.EconomyMath
import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.Agent
import com.championstar.soccer.domain.models.Contract
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.domain.models.TransferPolicy
import java.util.UUID

object CareerEngine {

    /**
     * Creates a new "Free Agent" player to start the game.
     * Represents the user's avatar.
     */
    fun startCareer(name: String, position: String, region: String): Player {
        val player = Player(
            id = UUID.randomUUID().toString(),
            name = name,
            age = 17, // Young prospect
            position = position,
            overallRating = 55.0, // Low starting rating (Tier 4 level)
            potential = GameMath.gaussian(85.0, 5.0).coerceIn(75.0, 99.0), // High potential
            reputation = 0.5,
            morale = 100.0,
            form = 50.0,
            agent = AgentEngine.createStarterAgent("A_START") // Family friend
        )
        return player
    }

    /**
     * Generates trial offers for a free agent player.
     * Looks for Tier 4 or low Tier 3 clubs.
     */
    fun generateTrialOffers(player: Player, allLeagues: List<League>): List<Pair<Team, Contract>> {
        val offers = mutableListOf<Pair<Team, Contract>>()

        // Agent Level 1 finds Tier 4 clubs
        // We assume agent is set from startCareer
        val agent = player.agent ?: AgentEngine.createStarterAgent(player.id)
        val potentialTeams = AgentEngine.findOpportunities(player, agent, allLeagues)

        // Take up to 3 teams and try to generate offers
        potentialTeams.take(3).forEach { team ->
            // Trial Offer: Very short contract (1 year), low wage
            // "Trial" means club evaluates you. Here we simulate passing trial if desire > 40.

            val desire = TransferEngine.evaluatePlayerForClub(player, team)
            if (desire > 40.0) {
                val contract = TransferEngine.generateContractOffer(player, team)
                // Trial specific: Lower wage initially
                val trialContract = contract.copy(
                    salary = (contract.salary * 0.7).toLong(),
                    yearsRemaining = 1,
                    signingBonus = 0
                )
                offers.add(team to trialContract)
            }
        }

        return offers
    }

    /**
     * Processes weekly opportunities for an employed player.
     * - Transfer offers if playing well.
     * - Loan offers if not playing.
     */
    fun processWeeklyOpportunities(player: Player, allLeagues: List<League>): List<Pair<Team, Contract>> {
        val offers = mutableListOf<Pair<Team, Contract>>()
        val agent = player.agent ?: return emptyList()

        // 1. Check Form
        if (player.form > 80.0) {
            // Hot Streak! Higher tier clubs interested
            val interestedClubs = AgentEngine.findOpportunities(player, agent, allLeagues)

            interestedClubs.forEach { team ->
                val desire = TransferEngine.evaluatePlayerForClub(player, team)
                if (desire > 70.0) { // High bar for poaching active player
                    val baseContract = TransferEngine.generateContractOffer(player, team)
                    // Agent negotiates automatically to show "best" offer
                    val negotiated = AgentEngine.negotiateOffer(baseContract, agent)
                    offers.add(team to negotiated)
                }
            }
        }

        // 2. Check Contract Status (Expiring)
        if (player.contract != null && player.contract!!.yearsRemaining <= 1) {
            // Expiring soon -> Pre-contract offers logic would go here
            // Simplified: Just add chance for offers regardless of form if value > 0
            if (GameMath.chance(0.1)) { // 10% chance per week
                 val interestedClubs = AgentEngine.findOpportunities(player, agent, allLeagues)
                 val team = interestedClubs.firstOrNull()
                 if (team != null) {
                     val contract = TransferEngine.generateContractOffer(player, team)
                     offers.add(team to contract)
                 }
            }
        }

        return offers
    }
}
