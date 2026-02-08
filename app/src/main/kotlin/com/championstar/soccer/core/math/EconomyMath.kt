package com.championstar.soccer.core.math

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * EconomyMath
 *
 * Provides financial calculations for the simulation, including:
 * - Market Value Valuation (based on skill, age, potential, reputation).
 * - Salary Caps and Dynamic Wage Growth.
 * - Inflation and Compound Interest.
 * - Business ROI and Risk Calculations.
 */
object EconomyMath {

    private const val BASE_INFLATION_RATE = 0.02 // 2% per year
    private const val SKILL_VALUE_EXPONENT = 2.5 // Values rise exponentially with skill
    private const val AGE_FACTOR_PEAK = 26.0

    /**
     * Calculates the market value of a player.
     *
     * @param overallRating Current skill level (1-100).
     * @param age Player age.
     * @param potential Potential skill cap (1-100).
     * @param reputation Global fame multiplier (1.0 = average).
     * @return Estimated market value in currency units.
     */
    fun calculateMarketValue(
        overallRating: Double,
        age: Int,
        potential: Double,
        reputation: Double
    ): Long {
        // Exponential value curve based on rating
        val baseValue = 1000.0 * (overallRating / 10.0).pow(SKILL_VALUE_EXPONENT)

        // Age factor: Young players with high potential are worth more
        // Peak age is ~26. Older players lose value, younger gain value based on potential.
        val ageFactor = if (age <= AGE_FACTOR_PEAK) {
            1.0 + (potential - overallRating) / 50.0 // Value potential heavily
        } else {
            (1.0 - (age - AGE_FACTOR_PEAK) * 0.1).coerceAtLeast(0.1) // Value drops post-peak
        }

        val totalValue = baseValue * ageFactor * reputation
        return totalValue.roundToInt().toLong().coerceAtLeast(1000)
    }

    /**
     * Calculates a fair weekly wage based on market value and role.
     *
     * @param marketValue The player's calculated market value.
     * @param contractLengthYears Duration of contract.
     */
    fun calculateWage(marketValue: Long, contractLengthYears: Int): Long {
        // Simple model: Wage is ~0.5% to 1% of value per week, adjusted for length
        val baseWage = marketValue * 0.008
        val lengthModifier = 1.0 + (contractLengthYears - 1) * 0.1 // Higher wage for longer commitment
        return (baseWage * lengthModifier).toLong().coerceAtLeast(100)
    }

    /**
     * Applies compound interest or inflation to a monetary value over time.
     *
     * @param principal Initial amount.
     * @param rate Annual rate (e.g., 0.05 for 5%).
     * @param years Time period.
     */
    fun compoundInterest(principal: Double, rate: Double, years: Int): Double {
        return principal * (1.0 + rate).pow(years)
    }

    /**
     * Calculates return on investment for a business venture, factoring in risk.
     *
     * @param investmentAmount Capital invested.
     * @param roiRate Expected base return rate (e.g., 0.10 for 10%).
     * @param riskFactor Probability of failure (0.0 to 1.0).
     */
    fun calculateBusinessReturn(
        investmentAmount: Long,
        roiRate: Double,
        riskFactor: Double
    ): Long {
        // Risk check: A simple dice roll against the risk factor
        if (GameMath.chance(riskFactor)) {
            // Failure: Lose a portion of investment (0.5 to 1.0 loss)
            val lossMultiplier = GameMath.nextDouble() * 0.5 + 0.5
            return -(investmentAmount * lossMultiplier).toLong()
        }

        // Success: Gain ROI with variance (+/- 20% of expected)
        val variance = GameMath.nextDouble() * 0.4 - 0.2 // -0.2 to +0.2
        val actualRoi = roiRate * (1.0 + variance)
        return (investmentAmount * actualRoi).toLong()
    }
}
