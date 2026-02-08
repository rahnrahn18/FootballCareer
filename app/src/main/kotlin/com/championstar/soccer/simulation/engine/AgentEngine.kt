package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.Agent
import com.championstar.soccer.domain.models.Contract
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import kotlin.math.roundToLong

object AgentEngine {

    /**
     * Generates a starter agent (Level 1: Family Member/Friend).
     */
    fun createStarterAgent(playerId: String): Agent {
        return Agent(
            id = "A_START_$playerId",
            name = "Family Friend",
            level = 1,
            negotiationSkill = 1.0, // Baseline: No bonus
            networkReach = 1.0, // Baseline: Local clubs only (Tier 4)
            scouting = 1.0, // Basic info
            clients = mutableListOf(playerId)
        )
    }

    /**
     * Upgrades an agent's stats based on successful deals or experience points.
     */
    fun upgradeAgent(agent: Agent) {
        if (agent.level < 10) {
            agent.level++
            // Scaling: 5% better negotiation per level
            agent.negotiationSkill = 1.0 + (agent.level * 0.05)
            // Network: Opens up higher tiers (Tier 4 -> Tier 1)
            agent.networkReach = 1.0 + (agent.level * 0.2) // Reach wider markets
            // Scouting: Better hidden attribute visibility
            agent.scouting = 1.0 + (agent.level * 0.1)
        }
    }

    /**
     * Negotiates a contract offer using the agent's skill.
     * Returns a new Contract with potentially improved terms.
     */
    fun negotiateOffer(offer: Contract, agent: Agent): Contract {
        // Negotiation Logic: Increase Wage & Bonuses based on skill vs "Club Patience"
        // For simplicity, we assume successful negotiation boosts value by (Skill - 1.0) * 100%
        // E.g., Level 5 Agent (1.25 skill) -> 25% boost

        val bonusMultiplier = agent.negotiationSkill

        // Random variance: Sometimes agents overplay their hand (small chance of failure not modeled here for simplicity)
        val variance = GameMath.gaussian(1.0, 0.1).coerceIn(0.8, 1.2) // +/- 20%

        val finalMultiplier = bonusMultiplier * variance

        return offer.copy(
            salary = (offer.salary * finalMultiplier).roundToLong(),
            signingBonus = (offer.signingBonus * finalMultiplier).roundToLong(),
            goalBonus = (offer.goalBonus * finalMultiplier).roundToLong(),
            cleanSheetBonus = (offer.cleanSheetBonus * finalMultiplier).roundToLong(),
            appearanceBonus = (offer.appearanceBonus * finalMultiplier).roundToLong()
        )
    }

    /**
     * Finds opportunities for a player based on agent's network.
     * Higher network = Better clubs found.
     */
    fun findOpportunities(player: Player, agent: Agent, allLeagues: List<com.championstar.soccer.domain.models.League>): List<Team> {
        // Filter leagues by agent's reach (Tier based)
        // Level 1: Tier 4
        // Level 3: Tier 3
        // Level 6: Tier 2
        // Level 9+: Tier 1

        val maxTierAccess = when {
            agent.level >= 9 -> 1
            agent.level >= 6 -> 2
            agent.level >= 3 -> 3
            else -> 4
        }

        val potentialClubs = allLeagues
            .filter { it.tier >= maxTierAccess } // Note: Tier 1 is "highest", so logic is inverted usually. Here Tier 1 < Tier 4 numerically.
            .flatMap { it.teams }
            .filter { team ->
                // Filter by need/fit
                // 1. Can they afford wage? (Budget check)
                // 2. Do they need the position? (Squad depth check)
                team.budget > 0 && TransferEngine.evaluatePlayerForClub(player, team) > 50.0
            }
            .shuffled()
            .take(3 + agent.level) // Higher level agents find more options

        return potentialClubs
    }
}
