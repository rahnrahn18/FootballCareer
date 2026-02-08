package com.championstar.soccer.data.static

import com.championstar.soccer.domain.models.Currency
import com.championstar.soccer.domain.models.ShopItem

object ShopDatabase {

    val items = listOf(
        // --- STAR ITEMS (20) ---
        // Consumables (Energy/Morale)
        ShopItem("S_01", "Energy Drink", "Restores 20 Stamina instantly.", 1, Currency.STAR) { p -> p.stamina = (p.stamina + 20.0).coerceAtMost(100.0); "Refreshed!" },
        ShopItem("S_02", "Spa Day", "Restores 50 Stamina and +5 Morale.", 3, Currency.STAR) { p -> p.stamina = (p.stamina + 50.0).coerceAtMost(100.0); p.morale += 5.0; "Relaxing." },
        ShopItem("S_03", "Sports Psychologist", "Boosts Morale by 20.", 2, Currency.STAR) { p -> p.morale = (p.morale + 20.0).coerceAtMost(100.0); "Mentally sharp." },
        ShopItem("S_04", "Ice Bath", "Recover 10 Stamina before a match.", 1, Currency.STAR) { p -> p.stamina += 10.0; "Cold but effective." },
        ShopItem("S_05", "Vacation Ticket", "Max out Morale instantly.", 5, Currency.STAR) { p -> p.morale = 100.0; "Ready to go!" },

        // Training (Skill Boosts - Small)
        ShopItem("S_06", "Personal Trainer (1 Week)", "+1.0 to Strength.", 5, Currency.STAR) { p -> p.overallRating += 0.1; "Feeling stronger." }, // Simplified skill map
        ShopItem("S_07", "Skills Coach (1 Week)", "+1.0 to Dribbling.", 5, Currency.STAR) { p -> p.overallRating += 0.1; "Feet are quicker." },
        ShopItem("S_08", "Shooting Drills", "+1.0 to Finishing.", 5, Currency.STAR) { p -> p.overallRating += 0.1; "Target practice paid off." },
        ShopItem("S_09", "Yoga Sessions", "+1.0 to Agility.", 4, Currency.STAR) { p -> p.overallRating += 0.1; "More flexible." },
        ShopItem("S_10", "Tactical Analysis", "+1.0 to Positioning.", 4, Currency.STAR) { p -> p.overallRating += 0.1; "Reading the game better." },

        // Agent / Reputation
        ShopItem("S_11", "PR Campaign", "+5.0 Reputation boost.", 8, Currency.STAR) { p -> p.reputation += 5.0; "Fans are talking about you." },
        ShopItem("S_12", "Charity Event", "+2.0 Reputation boost.", 3, Currency.STAR) { p -> p.reputation += 2.0; "Good karma." },
        ShopItem("S_13", "Agent Network Boost", "Agent Network +0.5.", 10, Currency.STAR) { p -> p.agent?.networkReach = (p.agent?.networkReach ?: 1.0) + 0.5; "Agent connections expanded." },
        ShopItem("S_14", "Agent Negotiation Class", "Agent Negotiation +0.5.", 10, Currency.STAR) { p -> p.agent?.negotiationSkill = (p.agent?.negotiationSkill ?: 1.0) + 0.5; "Agent drives a harder bargain." },

        // Age Extension (Expensive)
        ShopItem("S_15", "Cryotherapy Chamber", "Extend career by 1 month.", 50, Currency.STAR) { p -> "Career extended slightly." }, // Logic handled in TimeEngine
        ShopItem("S_16", "Stem Cell Treatment", "Extend career by 6 months.", 200, Currency.STAR) { p -> p.retirementAge += 0; "Knees feel brand new. (Logic pending)" }, // Simplified
        ShopItem("S_17", "Bionic Enhancements", "Extend career by 1 year.", 500, Currency.STAR) { p -> p.retirementAge += 1; "Playing until ${p.retirementAge}!" },

        // Fluff
        ShopItem("S_18", "Golden Boots", "Cosmetic only. Look cool.", 15, Currency.STAR) { p -> "Shiny." },
        ShopItem("S_19", "Custom Celebration", "Unlock a new celebration.", 5, Currency.STAR) { p -> "Crowd loves it." },
        ShopItem("S_20", "Rename Agent", "Change your agent's name.", 2, Currency.STAR) { p -> "Agent identity changed." }, // UI needed

        // --- GLORY (PREMIUM) ITEMS (10) ---
        // Age & Potential
        ShopItem("G_01", "Fountain of Youth (Reset Age)", "Reset age to 17. Keep skills.", 50, Currency.GLORY) { p -> p.age = 17; p.retirementAge = 35; "Reborn! The world is yours again." },
        ShopItem("G_02", "Genetic Re-sequencing", "Max out Potential (99.0).", 20, Currency.GLORY) { p -> p.potential = 99.0; "Limitless potential." },
        ShopItem("G_03", "Career Extension Max", "Set retirement age to 40 immediately.", 15, Currency.GLORY) { p -> p.retirementAge = 40; "Five more years!" },

        // Career Control
        ShopItem("G_04", "Force Transfer", "Force a move to any club.", 10, Currency.GLORY) { p -> p.isListedForTransfer = true; "Transfer request submitted with force." },
        ShopItem("G_05", "Super Agent Contract", "Hire a Level 10 Agent instantly.", 15, Currency.GLORY) { p -> p.agent?.level = 10; p.agent?.negotiationSkill = 2.0; p.agent?.networkReach = 2.0; "Mino Raiola works for you now." },
        ShopItem("G_06", "Position Change Master", "Instantly master a new position.", 5, Currency.GLORY) { p -> "New position mastered." }, // Needs UI selection

        // Stat Maxing
        ShopItem("G_07", "Steroid Injection (Legal?)", "+5.0 to ALL Physical stats.", 10, Currency.GLORY) { p -> p.stamina = 100.0; p.overallRating += 2.0; "You feel unstoppable." },
        ShopItem("G_08", "Neural Link", "+5.0 to ALL Mental stats.", 10, Currency.GLORY) { p -> p.morale = 100.0; p.overallRating += 2.0; "Focus is absolute." },

        // Convenience
        ShopItem("G_09", "Infinite Energy (1 Season)", "Stamina never drops below 90.", 25, Currency.GLORY) { p -> "Energy is boundless." }, // Logic needed in TimeEngine
        ShopItem("G_10", "Buy The Club", "Become player-manager.", 100, Currency.GLORY) { p -> "You own the place now." }
    )

    fun getStarItems() = items.filter { it.currency == Currency.STAR }
    fun getGloryItems() = items.filter { it.currency == Currency.GLORY }
}
