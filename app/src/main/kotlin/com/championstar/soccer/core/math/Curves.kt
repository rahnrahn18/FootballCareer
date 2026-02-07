package com.championstar.soccer.core.math

import kotlin.math.E
import kotlin.math.ln
import kotlin.math.pow

/**
 * Curves
 *
 * Provides mathematical functions for non-linear player development and regression.
 * Models:
 * - Sigmoid (S-curve): Typical for skill acquisition (slow start -> rapid -> slow end).
 * - Logarithmic: Diminishing returns (e.g., training efficiency).
 * - Exponential Decay: Age-related decline.
 */
object Curves {

    /**
     * Sigmoid function for player development.
     * Maps an input (e.g., training hours/age) to a 0.0-1.0 progress value.
     *
     * @param x The input variable (e.g., age relative to peak).
     * @param steepness How quickly the curve rises (higher = faster development window).
     * @param midpoint The x value where progress is 0.5 (50%).
     */
    fun sigmoid(x: Double, steepness: Double = 1.0, midpoint: Double = 0.0): Double {
        return 1.0 / (1.0 + E.pow(-steepness * (x - midpoint)))
    }

    /**
     * Logarithmic growth for diminishing returns (e.g., skill practice).
     *
     * @param x Effort or time invested.
     * @param scale Scaling factor.
     */
    fun logarithmic(x: Double, scale: Double = 1.0): Double {
        if (x <= 0) return 0.0
        return scale * ln(x + 1.0)
    }

    /**
     * Exponential decay for aging players.
     *
     * @param age Current age beyond peak.
     * @param decayRate How fast skills drop per year (0.0 to 1.0).
     */
    fun exponentialDecay(initialValue: Double, age: Int, decayRate: Double): Double {
        return initialValue * (1.0 - decayRate).pow(age)
    }

    /**
     * Calculates the potential skill cap based on a bell curve distribution around a mean potential.
     *
     * @param meanPotential The average potential for a player type.
     * @param variance The standard deviation of potential.
     */
    fun calculatePotential(meanPotential: Double, variance: Double): Double {
        return GameMath.gaussian(meanPotential, variance).coerceIn(1.0, 100.0)
    }

    /**
     * Linearly interpolates between two values based on a progress percentage.
     * Useful for smooth transitions between skill levels.
     */
    fun lerp(start: Double, end: Double, progress: Double): Double {
        return start + (end - start) * progress.coerceIn(0.0, 1.0)
    }
}
