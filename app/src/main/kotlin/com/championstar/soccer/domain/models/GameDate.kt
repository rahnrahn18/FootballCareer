package com.championstar.soccer.domain.models

data class GameDate(
    var year: Int = 2024,
    var month: Int = 7, // 1 = January, 7 = July (Start of Season)
    var week: Int = 1 // 1-4
) {
    fun nextWeek() {
        week++
        if (week > 4) {
            week = 1
            month++
            if (month > 12) {
                month = 1
                year++
            }
        }
    }

    override fun toString(): String {
        val monthName = when(month) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
            7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
            else -> "???"
        }
        return "Week $week, $monthName $year"
    }

    fun isTransferWindow(): Boolean {
        // Summer: July-Aug, Winter: Jan
        return (month == 7 || month == 8 || month == 1)
    }

    fun isSeasonEnd(): Boolean {
        return month == 6 && week == 4
    }
}
