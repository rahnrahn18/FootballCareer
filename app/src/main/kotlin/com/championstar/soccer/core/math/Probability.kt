package com.championstar.soccer.core.math

import kotlin.math.exp
import kotlin.math.pow
import kotlin.random.Random

/**
 * Probability
 *
 * Provides specialized probability distributions and random event generators
 * for simulating complex game scenarios (e.g., match scores, player events).
 */
object Probability {
    private val random = Random.Default

    /**
     * Calculates the probability of k events occurring in an interval, given lambda.
     * Uses the Poisson distribution formula: P(k; λ) = (e^-λ * λ^k) / k!
     *
     * @param k The number of occurrences (e.g., goals scored)
     * @param lambda The average rate of occurrence (e.g., goals per match)
     * @return The probability (0.0 to 1.0)
     */
    fun poisson(k: Int, lambda: Double): Double {
        if (lambda <= 0) return if (k == 0) 1.0 else 0.0
        val p = (exp(-lambda) * lambda.pow(k)) / factorial(k)
        return p
    }

    /**
     * Generates a random number of occurrences based on a Poisson distribution.
     * Useful for determining the number of goals in a match or injuries in a season.
     *
     * @param lambda The average rate of occurrence per interval.
     */
    fun nextPoisson(lambda: Double): Int {
        var l = exp(-lambda)
        var k = 0
        var p = 1.0
        do {
            k++
            p *= random.nextDouble()
        } while (p > l)
        return k - 1
    }

    /**
     * Factorial function for Poisson calculation.
     * Optimizes for small numbers common in football scores (0-10).
     */
    private fun factorial(n: Int): Long {
        if (n < 0) return 0
        if (n <= 1) return 1
        var result: Long = 1
        for (i in 2..n) {
            result *= i
        }
        return result
    }

    /**
     * Selects an item from a list based on weighted probabilities.
     *
     * @param items List of items to select from.
     * @param weights List of weights corresponding to each item (must sum > 0).
     * @return The selected item, or null if lists are empty/mismatched.
     */
    fun <T> weightedRandom(items: List<T>, weights: List<Double>): T? {
        if (items.size != weights.size || items.isEmpty()) return null

        val totalWeight = weights.sum()
        var randomValue = random.nextDouble() * totalWeight

        for (i in items.indices) {
            randomValue -= weights[i]
            if (randomValue <= 0) {
                return items[i]
            }
        }
        return items.last()
    }

    /**
     * Determines if an event occurs based on a base probability and modifiers.
     *
     * @param baseChance The inherent probability (0.0 to 1.0).
     * @param modifiers List of multiplicative modifiers (e.g., high skill = 1.2x).
     */
    fun checkEvent(baseChance: Double, modifiers: List<Double> = emptyList()): Boolean {
        var chance = baseChance
        modifiers.forEach { chance *= it }
        return random.nextDouble() < chance.coerceIn(0.0, 1.0)
    }
}
