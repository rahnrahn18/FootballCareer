package com.championstar.soccer.data.static

/**
 * TraitDatabase
 *
 * Defines personality traits that affect player behavior, growth, and match performance.
 * Traits add depth to the simulation by making players unique individuals.
 */
object TraitDatabase {

    enum class TraitType { MENTAL, PHYSICAL, SOCIAL, DEVELOPMENT }

    data class Trait(
        val id: String,
        val name: String,
        val description: String,
        val type: TraitType,
        val impact: (Double) -> Double // Generic modifier function
    )

    val traits = listOf(
        // --- MENTAL ---
        Trait("MEN_01", "Determined", "Never gives up, even when losing.", TraitType.MENTAL) { it * 1.1 },
        Trait("MEN_02", "Lazy", "Often skips training sessions.", TraitType.MENTAL) { it * 0.8 },
        Trait("MEN_03", "Professional", "Model citizen on and off the pitch.", TraitType.MENTAL) { it * 1.05 },
        Trait("MEN_04", "Volatile", "Prone to arguments and red cards.", TraitType.MENTAL) { it * 0.9 },
        Trait("MEN_05", "Leader", "Inspires teammates to perform better.", TraitType.MENTAL) { it * 1.15 },
        Trait("MEN_06", "Nervous", "Performances drop in big matches.", TraitType.MENTAL) { it * 0.85 },
        Trait("MEN_07", "Clutch", "Performs best in the final minutes.", TraitType.MENTAL) { it * 1.2 },

        // --- PHYSICAL ---
        Trait("PHY_01", "Injury Prone", "Likely to spend time on the physio table.", TraitType.PHYSICAL) { it * 0.7 },
        Trait("PHY_02", "Iron Man", "Rarely gets injured or tired.", TraitType.PHYSICAL) { it * 1.2 },
        Trait("PHY_03", "Speedster", "Relies heavily on pace.", TraitType.PHYSICAL) { it * 1.1 },
        Trait("PHY_04", "Tank", "Uses strength to dominate.", TraitType.PHYSICAL) { it * 1.1 },
        Trait("PHY_05", "Agile", "Can turn on a sixpence.", TraitType.PHYSICAL) { it * 1.05 },

        // --- SOCIAL ---
        Trait("SOC_01", "Popular", "Boosts shirt sales and fan support.", TraitType.SOCIAL) { it * 1.3 },
        Trait("SOC_02", "Locker Room Cancer", "Disrupts team harmony.", TraitType.SOCIAL) { it * 0.7 },
        Trait("SOC_03", "Media Darling", "Loved by the press, good for sponsors.", TraitType.SOCIAL) { it * 1.2 },
        Trait("SOC_04", "Controversial", "Frequently in the tabloids.", TraitType.SOCIAL) { it * 0.9 },

        // --- DEVELOPMENT ---
        Trait("DEV_01", "Late Bloomer", "Develops skills later in career.", TraitType.DEVELOPMENT) { it * 1.0 }, // Logic handled in engine
        Trait("DEV_02", "Wonderkid", "Rapid development at a young age.", TraitType.DEVELOPMENT) { it * 1.5 },
        Trait("DEV_03", "Peak Early", "Reaches potential fast but declines early.", TraitType.DEVELOPMENT) { it * 1.2 },
        Trait("DEV_04", "Consistent", "Maintain level for a long time.", TraitType.DEVELOPMENT) { it * 1.05 }
    )

    fun getTraitById(id: String) = traits.find { it.id == id }
    fun getRandomTraits(count: Int) = traits.shuffled().take(count)
}
