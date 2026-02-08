package com.championstar.soccer.data.static

import com.championstar.soccer.domain.models.Achievement

object AchievementDatabase {

    // --- Core Achievements (Generates Stars) ---
    val achievements = listOf(
        // GOALS
        Achievement("ACH_01", "First Strike", "Score your first career goal.", 1),
        Achievement("ACH_02", "Hat-trick Hero", "Score 3 goals in a single match.", 3),
        Achievement("ACH_03", "Golden Boot", "Score 20 goals in a single season.", 5),
        Achievement("ACH_04", "Century Club", "Score 100 career goals.", 10),
        Achievement("ACH_05", "Legendary Scorer", "Score 500 career goals.", 20),

        // APPEARANCES
        Achievement("ACH_06", "Debut", "Play your first professional match.", 1),
        Achievement("ACH_07", "Regular Starter", "Play 10 consecutive matches.", 2),
        Achievement("ACH_08", "Iron Man", "Play 50 matches in a season.", 5),
        Achievement("ACH_09", "Club Icon", "Make 200 appearances for one club.", 10),

        // CAREER & TRANSFER
        Achievement("ACH_10", "First Contract", "Sign your first professional contract.", 1),
        Achievement("ACH_11", "Moving Up", "Sign for a Tier 3 club.", 2),
        Achievement("ACH_12", "Big Leagues", "Sign for a Tier 1 club.", 5),
        Achievement("ACH_13", "Galactico", "Sign a contract worth over $100k/week.", 10),
        Achievement("ACH_14", "Loyalty", "Stay at one club for 5 seasons.", 5),

        // PERFORMANCE
        Achievement("ACH_15", "Man of the Match", "Earn a match rating of 9.0 or higher.", 2),
        Achievement("ACH_16", "Perfect 10", "Earn a match rating of 10.0.", 5),
        Achievement("ACH_17", "Assist King", "Provide 3 assists in a single match.", 3),
        Achievement("ACH_18", "Clean Sheet Master", "Keep 5 clean sheets in a row (GK/DF).", 3),

        // WEALTH & LIFESTYLE
        Achievement("ACH_19", "High Roller", "Accumulate $1,000,000 in career earnings.", 5),
        Achievement("ACH_20", "Business Tycoon", "Own 5 profitable businesses.", 5),

        // MISC
        Achievement("ACH_21", "National Hero", "Win a major trophy.", 10),
        Achievement("ACH_22", "Agent's Best Friend", "Upgrade your agent to Level 10.", 5),
        Achievement("ACH_23", "Veteran", "Play until age 34.", 5),
        Achievement("ACH_24", "Survivor", "Recover from a major injury.", 2),
        Achievement("ACH_25", "Captain", "Be named team captain.", 3)
    )

    fun getAchievementById(id: String) = achievements.find { it.id == id }
}
