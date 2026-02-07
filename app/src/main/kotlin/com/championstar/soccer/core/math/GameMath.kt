package com.championstar.soccer.core.math

import kotlin.math.ln
import kotlin.math.pow
import kotlin.random.Random

/**
 * GameMath
 *
 * Central mathematical utility for the Championstar Soccer Simulator.
 * Provides consistent random number generation, scaling factors, and
 * core mathematical constants used throughout the simulation.
 */
object GameMath {
    private val random = Random(System.currentTimeMillis())

    // --- Core Constants ---
    const val MATCH_DURATION_MINUTES = 90
    const val SIMULATION_TICK_MS = 100L
    const val MAX_PLAYER_RATING = 100.0
    const val MIN_PLAYER_RATING = 1.0

    // --- Random Generation ---

    /**
     * Returns a random float between 0.0 and 1.0.
     */
    fun nextFloat(): Float = random.nextFloat()

    /**
     * Returns a random double between 0.0 and 1.0.
     */
    fun nextDouble(): Double = random.nextDouble()

    /**
     * Returns a random integer between min (inclusive) and max (exclusive).
     */
    fun nextInt(min: Int, max: Int): Int = random.nextInt(min, max)

    /**
     * Returns a boolean with a given probability of being true.
     * @param probability 0.0 to 1.0
     */
    fun chance(probability: Double): Boolean = nextDouble() < probability

    /**
     * Returns a value from a Gaussian (Normal) distribution.
     * @param mean The mean value (center of the bell curve)
     * @param stdDev The standard deviation (spread)
     */
    fun gaussian(mean: Double, stdDev: Double): Double {
        // Box-Muller transform
        var u = 0.0
        var v = 0.0
        while (u == 0.0) u = nextDouble()
        while (v == 0.0) v = nextDouble()

        val z = kotlin.math.sqrt(-2.0 * ln(u)) * kotlin.math.cos(2.0 * Math.PI * v)
        return mean + (z * stdDev)
    }

    /**
     * Clamps a value between min and max.
     */
    fun clamp(value: Double, min: Double, max: Double): Double {
        return value.coerceIn(min, max)
    }

    /**
     * Linear interpolation between a and b by t.
     */
    fun lerp(a: Double, b: Double, t: Double): Double {
        return a + t * (b - a)
    }
}
