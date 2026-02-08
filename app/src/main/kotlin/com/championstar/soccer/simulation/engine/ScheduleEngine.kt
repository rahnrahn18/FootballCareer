package com.championstar.soccer.simulation.engine

import com.championstar.soccer.domain.models.GameDate

object ScheduleEngine {

    /**
     * Determines if a match is scheduled for the current week.
     * Follows a realistic August -> May season.
     */
    fun isMatchScheduled(date: GameDate): Boolean {
        // Season: August (8) to May (5)
        val isSeason = date.month >= 8 || date.month <= 5

        if (!isSeason) return false // Pre-season / Off-season

        // International Breaks (Approximate)
        // Sep Week 2, Oct Week 2, Nov Week 2, Mar Week 3
        if (date.month == 9 && date.week == 2) return false
        if (date.month == 10 && date.week == 2) return false
        if (date.month == 11 && date.week == 2) return false
        if (date.month == 3 && date.week == 3) return false

        // Winter Break (Some leagues have it, but EPL plays through)
        // Let's assume a short break in late Dec/Jan week 1
        if (date.month == 1 && date.week == 1) return false

        return true
    }

    /**
     * Returns true if it's the end of the season (End of May/June).
     */
    fun isEndOfSeason(date: GameDate): Boolean {
        return date.month == 6 && date.week == 1
    }
}
