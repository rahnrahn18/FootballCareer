package com.championstar.soccer.simulation.engine.generators

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.data.static.NameDatabase
import com.championstar.soccer.data.static.TraitDatabase
import com.championstar.soccer.domain.models.Player
import java.util.UUID
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

/**
 * Advanced Player Generator.
 * Creates unique, diverse, and realistic football players with detailed attributes and potential curves.
 */
object PlayerGenerator {

    // --- CONFIGURATION ---
    private const val MIN_AGE = 16
    private const val MAX_AGE = 36
    private const val PEAK_AGE_START = 26
    private const val PEAK_AGE_END = 29

    // --- GENERATION LOGIC ---

    fun generatePlayer(
        tier: Int,
        leagueRegion: String,
        forcedPosition: String? = null,
        isYouthAcademy: Boolean = false
    ): Player {
        // 1. Determine Position
        val position = forcedPosition ?: generateRandomPosition()

        // 2. Determine Age
        // Weighted towards prime age (24-29) generally, but youth academy is 16-19
        val age = if (isYouthAcademy) {
            GameMath.nextInt(16, 19)
        } else {
            generateWeightedAge()
        }

        // 3. Name Generation
        val nameRegion = regionToNameDbRegion(leagueRegion)
        val name = NameDatabase.generateNameForRegion(nameRegion)

        // 4. Rating & Potential Calculation
        val (currentRating, potential) = calculateRatingAndPotential(tier, age, position, isYouthAcademy)

        // 5. Traits
        val traits = generateTraits(position, age, potential)

        // 6. Stats (Stamina, Morale, Form)
        // Stamina correlates with age (younger = better recovery but maybe lower peak, older = lower)
        // Here simplified.
        val stamina = generateStamina(age, position)

        return Player(
            id = UUID.randomUUID().toString(),
            name = name,
            age = age,
            position = position,
            overallRating = currentRating,
            potential = potential,
            reputation = (currentRating / 100.0).pow(2) * 100, // Reputation grows exponentially with skill
            stamina = stamina,
            morale = GameMath.nextDouble(70.0, 100.0), // Start relatively happy
            form = 50.0, // Neutral form
            traits = traits
        )
    }

    private fun generateRandomPosition(): String {
        val roll = GameMath.nextDouble()
        return when {
            roll < 0.1 -> "GK" // 10%
            roll < 0.45 -> listOf("CB", "LB", "RB", "LWB", "RWB").random() // 35% Defenders
            roll < 0.80 -> listOf("CDM", "CM", "CAM", "LM", "RM").random() // 35% Midfielders
            else -> listOf("ST", "CF", "RW", "LW").random() // 20% Forwards
        }
    }

    private fun generateWeightedAge(): Int {
        // Bell curve around 26
        val mean = 26.0
        val stdDev = 4.0
        val age = (GameMath.gaussian(mean, stdDev)).toInt().coerceIn(MIN_AGE, MAX_AGE)
        return age
    }

    private fun calculateRatingAndPotential(tier: Int, age: Int, position: String, isYouth: Boolean): Pair<Double, Double> {
        // Base rating depends on Tier
        // Tier 1: 75-90, Tier 2: 65-78, Tier 3: 55-68, Tier 4: 45-60
        val baseMean = when(tier) {
            1 -> 80.0
            2 -> 72.0
            3 -> 62.0
            else -> 52.0
        }

        // Variance
        val rating = (GameMath.gaussian(baseMean, 5.0)).coerceIn(1.0, 99.0)

        // Potential logic
        // Young players have higher potential gap
        // Old players potential == current (or declined)

        val potentialGap = if (age < 22) {
            GameMath.nextInt(5, 25) // High growth potential
        } else if (age < 26) {
            GameMath.nextInt(2, 10) // Moderate growth
        } else if (age < 30) {
            GameMath.nextInt(0, 3) // Peak
        } else {
            0 // Decline phase
        }

        // Wonderkid chance (low tier might spawn a gem)
        val isWonderkid = age < 21 && GameMath.chance(0.02) // 2% chance
        val finalPotential = if (isWonderkid) {
            (rating + GameMath.nextInt(20, 35)).coerceAtMost(99.0)
        } else {
            (rating + potentialGap).coerceAtMost(99.0)
        }

        // Adjust current rating for age (Young players start lower than baseMean often)
        val ageFactor = if (age < 22) 0.85 + (age - 16) * 0.025 else 1.0
        val finalRating = (rating * ageFactor).coerceIn(1.0, finalPotential)

        return finalRating to finalPotential
    }

    private fun generateTraits(position: String, age: Int, potential: Double): List<TraitDatabase.Trait> {
        if (!GameMath.chance(0.3)) return emptyList() // 70% have no special traits initially

        val numTraits = if (potential > 85) GameMath.nextInt(1, 3) else 1
        val available = TraitDatabase.traits.toMutableList()

        // Filter senseless traits
        if (position == "GK") {
            available.removeAll { it.name == "Speedster" || it.name == "Tank" } // GKs usually not speedsters/tanks in trait context
        }

        if (age < 20) {
            available.removeAll { it.name == "Leader" || it.name == "Professional" } // Rare for kids
        } else {
             available.removeAll { it.name == "Wonderkid" } // Can't be wonderkid if old
        }

        return available.shuffled().take(numTraits)
    }

    private fun generateStamina(age: Int, position: String): Double {
        val base = when(position) {
            "GK" -> 60.0
            "CB" -> 75.0
            "CM", "CDM", "CAM", "LM", "RM", "LWB", "RWB" -> 85.0 // Midfielders run most
            else -> 75.0
        }

        // Age factor: Peaks at 24-28
        val ageMod = when {
            age < 20 -> 0.9
            age in 20..29 -> 1.0
            age in 30..33 -> 0.9
            else -> 0.75
        }

        val variance = GameMath.nextDouble(-5.0, 10.0)
        return (base * ageMod + variance).coerceIn(50.0, 99.0)
    }

    private fun regionToNameDbRegion(leagueRegion: String): String {
        return when(leagueRegion) {
            "England", "Spain", "Germany", "Italy", "France", "Portugal", "Netherlands", "Belgium", "Poland", "Sweden", "Norway", "Denmark", "Austria", "Switzerland", "Scotland", "Greece", "Russia", "Turkey" -> "Europe"
            "Brazil", "Argentina", "Colombia", "Chile", "Uruguay" -> "SouthAmerica"
            "Japan", "South Korea", "China", "Saudi Arabia", "India" -> "Asia"
            "USA", "Mexico", "Canada" -> "NorthAmerica"
            "Nigeria", "Ghana", "Senegal", "Egypt", "Morocco", "Cameroon" -> "Africa"
            else -> "Europe"
        }
    }
}
