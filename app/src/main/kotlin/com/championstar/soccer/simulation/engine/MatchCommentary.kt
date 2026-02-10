package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.Language
import com.championstar.soccer.core.Localization
import com.championstar.soccer.core.math.GameMath

/**
 * Deep Match Commentary System.
 * Generates context-aware, varied commentary for match events in multiple languages.
 */
object MatchCommentary {

    fun getGoalCommentary(scorerName: String, assistName: String?, minute: Int, homeScore: Int, awayScore: Int, isHomeGoal: Boolean): String {
        val isLate = minute > 85
        val diff = if (isHomeGoal) homeScore - awayScore else awayScore - homeScore
        val isEqualizer = diff == 0
        val isLead = diff == 1
        val isThrashing = diff >= 3

        return when (Localization.currentLanguage) {
            Language.EN -> generateEnGoal(scorerName, assistName, isLate, isEqualizer, isLead, isThrashing)
            Language.ID -> generateIdGoal(scorerName, assistName, isLate, isEqualizer, isLead, isThrashing)
        }
    }

    fun getMissCommentary(shooterName: String, isHeader: Boolean, isClose: Boolean): String {
        return when (Localization.currentLanguage) {
            Language.EN -> generateEnMiss(shooterName, isHeader, isClose)
            Language.ID -> generateIdMiss(shooterName, isHeader, isClose)
        }
    }

    fun getSaveCommentary(keeperName: String, shooterName: String, isSpectacular: Boolean): String {
        return when (Localization.currentLanguage) {
            Language.EN -> generateEnSave(keeperName, shooterName, isSpectacular)
            Language.ID -> generateIdSave(keeperName, shooterName, isSpectacular)
        }
    }

    fun getFoulCommentary(foulerName: String, fouledName: String, location: String): String {
        return when (Localization.currentLanguage) {
            Language.EN -> generateEnFoul(foulerName, fouledName, location)
            Language.ID -> generateIdFoul(foulerName, fouledName, location)
        }
    }

    fun getCardCommentary(playerName: String, isRed: Boolean): String {
        return when (Localization.currentLanguage) {
            Language.EN -> if (isRed) "RED CARD! $playerName is sent off!" else "Yellow card for $playerName."
            Language.ID -> if (isRed) "KARTU MERAH! $playerName diusir keluar!" else "Kartu kuning untuk $playerName."
        }
    }

    fun getInjuryCommentary(playerName: String): String {
        return when (Localization.currentLanguage) {
            Language.EN -> "$playerName looks injured. He might need to come off."
            Language.ID -> "$playerName terlihat cedera. Dia mungkin harus keluar."
        }
    }

    fun getCornerCommentary(teamName: String): String {
        return when (Localization.currentLanguage) {
            Language.EN -> "Corner kick for $teamName."
            Language.ID -> "Tendangan sudut untuk $teamName."
        }
    }

    fun getVarCheckCommentary(): String {
        return when (Localization.currentLanguage) {
            Language.EN -> "VAR Check in progress..."
            Language.ID -> "Pemeriksaan VAR sedang berlangsung..."
        }
    }

    fun getVarDecisionCommentary(confirmed: Boolean): String {
        return when (Localization.currentLanguage) {
            Language.EN -> if (confirmed) "Goal Confirmed!" else "Goal disallowed! Offside."
            Language.ID -> if (confirmed) "Gol Disahkan!" else "Gol dianulir! Offside."
        }
    }

    fun getHalfTimeCommentary(): String {
        return when (Localization.currentLanguage) {
            Language.EN -> "HALF TIME"
            Language.ID -> "BABAK PERTAMA SELESAI"
        }
    }

    // --- ENGLISH GENERATORS ---

    private fun generateEnGoal(scorer: String, assist: String?, isLate: Boolean, isEqualizer: Boolean, isLead: Boolean, isThrashing: Boolean): String {
        val templates = mutableListOf<String>()

        if (isLate) {
            templates.add("UNBELIEVABLE! $scorer scores right at the death!")
            templates.add("Surely that's the winner from $scorer!")
            if (isEqualizer) templates.add("They've snatched a draw! $scorer equalizes in the dying moments!")
        } else if (isThrashing) {
            templates.add("It's a rout! $scorer adds another one!")
            templates.add("They are unstoppable today. $scorer scores.")
        } else if (isEqualizer) {
            templates.add("GOAL! $scorer levels the game!")
            templates.add("All square! $scorer finds the back of the net!")
        } else if (isLead) {
            templates.add("GOAL! $scorer puts them in front!")
            templates.add("Breakthrough! $scorer scores!")
        } else {
            templates.add("GOAL for $scorer!")
            templates.add("$scorer finishes nicely.")
            templates.add("It's in! $scorer scores.")
        }

        if (assist != null) {
            templates.add("$scorer scores after a great pass from $assist.")
            templates.add("Lovely assist by $assist, finished by $scorer.")
        }

        return templates.random()
    }

    private fun generateEnMiss(shooter: String, isHeader: Boolean, isClose: Boolean): String {
        val templates = mutableListOf<String>()
        if (isHeader) {
            templates.add("$shooter heads it just over!")
            templates.add("Header from $shooter goes wide.")
        } else {
            if (isClose) {
                templates.add("So close! $shooter hits the post!")
                templates.add("Inches wide from $shooter!")
            } else {
                templates.add("$shooter shoots wide.")
                templates.add("Poor effort from $shooter.")
            }
        }
        return templates.random()
    }

    private fun generateEnSave(keeper: String, shooter: String, isSpectacular: Boolean): String {
        return if (isSpectacular) {
             "WHAT A SAVE! $keeper denies $shooter!"
        } else {
             "Good save by $keeper."
        }
    }

    private fun generateEnFoul(fouler: String, fouled: String, location: String): String {
        val templates = listOf(
            "Foul by $fouler on $fouled.",
            "$fouler trips $fouled.",
            "Referee blows for a foul against $fouler.",
            "Clumsy challenge by $fouler."
        )
        return templates.random()
    }

    // --- INDONESIAN GENERATORS ---

    private fun generateIdGoal(scorer: String, assist: String?, isLate: Boolean, isEqualizer: Boolean, isLead: Boolean, isThrashing: Boolean): String {
        val templates = mutableListOf<String>()

        if (isLate) {
            templates.add("LUAR BIASA! $scorer mencetak gol di menit akhir!")
            templates.add("Pasti ini gol kemenangan dari $scorer!")
            if (isEqualizer) templates.add("Penyelamat! $scorer menyamakan kedudukan di saat-saat terakhir!")
        } else if (isThrashing) {
            templates.add("Pesta gol! $scorer menambah satu lagi!")
            templates.add("Mereka tak terbendung. $scorer mencetak gol.")
        } else if (isEqualizer) {
            templates.add("GOL! $scorer menyamakan kedudukan!")
            templates.add("Skor imbang! $scorer berhasil membobol gawang!")
        } else if (isLead) {
            templates.add("GOL! $scorer membawa timnya unggul!")
            templates.add("Terobosan! $scorer mencetak gol!")
        } else {
            templates.add("GOL untuk $scorer!")
            templates.add("$scorer menyelesaikannya dengan baik.")
            templates.add("Masuk! $scorer mencetak gol.")
        }

        if (assist != null) {
            templates.add("$scorer mencetak gol setelah umpan hebat dari $assist.")
            templates.add("Assist cantik oleh $assist, diselesaikan oleh $scorer.")
        }

        return templates.random()
    }

    private fun generateIdMiss(shooter: String, isHeader: Boolean, isClose: Boolean): String {
        val templates = mutableListOf<String>()
        if (isHeader) {
            templates.add("Sundulan $shooter melambung!")
            templates.add("Sundulan dari $shooter melebar.")
        } else {
            if (isClose) {
                templates.add("Sangat dekat! $shooter mengenai tiang!")
                templates.add("Hanya beberapa inci dari $shooter!")
            } else {
                templates.add("Tembakan $shooter melebar.")
                templates.add("Usaha yang buruk dari $shooter.")
            }
        }
        return templates.random()
    }

    private fun generateIdSave(keeper: String, shooter: String, isSpectacular: Boolean): String {
        return if (isSpectacular) {
             "PENYELAMATAN GEMILANG! $keeper menahan tembakan $shooter!"
        } else {
             "Penyelamatan bagus oleh $keeper."
        }
    }

    private fun generateIdFoul(fouler: String, fouled: String, location: String): String {
        val templates = listOf(
            "Pelanggaran oleh $fouler terhadap $fouled.",
            "$fouler menjegal $fouled.",
            "Wasit meniup peluit, pelanggaran $fouler.",
            "Tantangan ceroboh dari $fouler."
        )
        return templates.random()
    }
}
