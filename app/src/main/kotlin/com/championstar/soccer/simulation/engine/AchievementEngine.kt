package com.championstar.soccer.simulation.engine

import com.championstar.soccer.data.static.AchievementDatabase
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.MatchResult

object AchievementEngine {

    /**
     * Checks if any achievements are unlocked based on player stats and recent match result.
     * Returns a list of newly unlocked achievement messages.
     */
    fun checkAchievements(player: Player, recentMatch: MatchResult? = null): List<String> {
        val newUnlocks = mutableListOf<String>()

        AchievementDatabase.achievements.forEach { achievement ->
            // Skip if already unlocked
            if (!player.unlockedAchievements.contains(achievement.id)) {

                val isMet = when (achievement.id) {
                    "ACH_01" -> player.goals >= 1
                    "ACH_02" -> recentMatch != null && calculateMatchGoals(player, recentMatch) >= 3
                    "ACH_03" -> player.goals >= 20 // Ideally seasonal goals, but using career for simplicity now
                    "ACH_04" -> player.goals >= 100
                    "ACH_05" -> player.goals >= 500
                    "ACH_06" -> player.appearances >= 1
                    "ACH_07" -> player.appearances >= 10 // Simplified check
                    "ACH_08" -> player.appearances >= 50
                    "ACH_09" -> player.appearances >= 200

                    "ACH_10" -> player.contract != null
                    "ACH_11" -> player.contract != null && player.contract!!.salary > 500 // Proxy for Tier 3
                    "ACH_12" -> player.contract != null && player.contract!!.salary > 5000 // Proxy for Tier 1
                    "ACH_13" -> player.contract != null && player.contract!!.salary > 100000

                    "ACH_15" -> player.form > 90.0 // Proxy for high rating
                    "ACH_16" -> player.form >= 99.0
                    "ACH_17" -> player.assists >= 3 // Career assists for now

                    "ACH_22" -> player.agent != null && player.agent!!.level >= 10
                    "ACH_23" -> player.age >= 34

                    else -> false
                }

                if (isMet) {
                    player.unlockedAchievements.add(achievement.id)
                    player.stars += achievement.rewardStars
                    newUnlocks.add("🏆 Achievement Unlocked: ${achievement.title} (+${achievement.rewardStars} Stars)")
                }
            }
        }

        return newUnlocks
    }

    // Helper (This logic was inside MatchEngine/TimeEngine, simplified here)
    private fun calculateMatchGoals(player: Player, match: MatchResult): Int {
        // In a real system, MatchResult would store scorer IDs.
        // Since we simulate stats directly onto player object in TimeEngine,
        // we can't easily retroactively check "goals in THIS match" without passing it.
        // For the sake of this check, we assume the caller handles this context or we track "lastMatchGoals".
        // Placeholder:
        return 0
    }
}
