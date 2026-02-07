package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.EconomyMath
import com.championstar.soccer.data.static.BusinessDatabase
import com.championstar.soccer.domain.models.Team
import kotlin.math.roundToLong

/**
 * EconomyEngine
 *
 * Logic for managing team finances, business investments, and market fluctuations.
 */
object EconomyEngine {

    /**
     * Updates the financial state of a team or player manager.
     * Processes income, expenses, and business returns weekly.
     */
    fun processWeeklyFinances(team: Team, activeBusinesses: Map<String, Int> = emptyMap()) {
        // 1. Calculate Player Wages
        val totalWages = calculateWeeklyWageBill(team)
        team.budget -= totalWages

        // 2. Process Business Returns (Passive Income)
        var businessIncome: Long = 0
        activeBusinesses.forEach { (businessId, quantity) ->
            val business = BusinessDatabase.getBusinessById(businessId)
            if (business != null) {
                // ROI is annual, convert to weekly
                val weeklyRoi = business.roi / 52.0
                val investment = business.cost * quantity

                val returnAmount = EconomyMath.calculateBusinessReturn(investment, weeklyRoi, business.risk / 52.0)
                businessIncome += returnAmount
            }
        }
        team.budget += businessIncome

        // 3. Sponsorship / Ticket Sales (Simplified)
        val fanSupport = team.reputation * 1000 // Fans based on rep
        val ticketPrice = 25.0
        // Assume 50% home games average per week (simulated)
        val matchDayIncome = (fanSupport * ticketPrice * 0.5).roundToLong()

        team.budget += matchDayIncome
    }

    fun calculateWeeklyWageBill(team: Team): Long {
        return team.players.sumOf { player ->
            EconomyMath.calculateWage(
                EconomyMath.calculateMarketValue(player.overallRating, player.age, player.potential, player.reputation),
                3 // Assume 3 year contracts for now
            )
        }
    }

    /**
     * Checks if a team can afford a transfer or investment.
     */
    fun canAfford(team: Team, amount: Long): Boolean {
        return team.budget >= amount
    }

    /**
     * Transaction: Buy Business
     */
    fun buyBusiness(team: Team, businessId: String): Boolean {
        val business = BusinessDatabase.getBusinessById(businessId) ?: return false
        if (canAfford(team, business.cost)) {
            team.budget -= business.cost
            // Logic to add business to team's portfolio would go here
            return true
        }
        return false
    }
}
