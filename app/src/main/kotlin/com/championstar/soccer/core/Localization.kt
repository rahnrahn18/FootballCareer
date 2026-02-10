package com.championstar.soccer.core

enum class Language(val code: String, val displayName: String) {
    EN("en", "English"),
    ID("id", "Bahasa Indonesia")
}

object Localization {
    var currentLanguage: Language = Language.EN
        private set

    fun setLanguage(lang: Language) {
        currentLanguage = lang
    }

    fun get(key: String, vararg args: Any): String {
        val map = when (currentLanguage) {
            Language.EN -> enStrings
            Language.ID -> idStrings
        }
        val template = map[key] ?: key
        return try {
            if (args.isNotEmpty()) String.format(template, *args) else template
        } catch (e: Exception) {
            template
        }
    }

    // --- KEYS ---
    const val MENU_NEW_GAME = "menu_new_game"
    const val MENU_CONTINUE = "menu_continue"
    const val MENU_CONTINUE_LOCKED = "menu_continue_locked"
    const val MENU_SETTINGS = "menu_settings"
    const val MENU_LANGUAGE = "menu_language"

    const val MATCH_DAY = "match_day"
    const val MATCH_VS = "match_vs"
    const val MATCH_STARTING_XI = "match_starting_xi"
    const val MATCH_BENCH = "match_bench"
    const val MATCH_BENCH_SIM = "match_bench_sim"
    const val MATCH_KICK_OFF = "match_kick_off"
    const val MATCH_FULL_TIME = "match_full_time"
    const val MATCH_CONTINUE = "match_continue"

    const val MATCH_EVENT_GOAL = "match_event_goal"

    // DECISION
    const val DECISION_TITLE_GOAL = "decision_title_goal"
    const val DECISION_DESC_GOAL = "decision_desc_goal"
    const val DECISION_OPT_CHIP = "decision_opt_chip"
    const val DECISION_OPT_POWER = "decision_opt_power"
    const val DECISION_OPT_PASS = "decision_opt_pass"

    // STATS
    const val STAT_POSSESSION = "stat_possession"
    const val STAT_SHOTS = "stat_shots"
    const val STAT_ON_TARGET = "stat_on_target"
    const val STAT_FOULS = "stat_fouls"
    const val STAT_CARDS = "stat_cards"

    private val enStrings = mapOf(
        MENU_NEW_GAME to "NEW CAREER",
        MENU_CONTINUE to "CONTINUE CAREER",
        MENU_CONTINUE_LOCKED to "CONTINUE CAREER (Locked)",
        MENU_SETTINGS to "SETTINGS",
        MENU_LANGUAGE to "Language: English",

        MATCH_DAY to "MATCH DAY",
        MATCH_VS to " VS ",
        MATCH_STARTING_XI to "You are in the STARTING XI!",
        MATCH_BENCH to "You are on the BENCH.",
        MATCH_BENCH_SIM to "Simulating match from bench...",
        MATCH_KICK_OFF to "KICK OFF",
        MATCH_FULL_TIME to "FULL TIME",
        MATCH_CONTINUE to "Continue",

        MATCH_EVENT_GOAL to "GOAL!",

        DECISION_TITLE_GOAL to "Goal Scoring Opportunity!",
        DECISION_DESC_GOAL to "You are through on goal. The keeper is rushing out.",
        DECISION_OPT_CHIP to "Chip Shot",
        DECISION_OPT_POWER to "Power Shot",
        DECISION_OPT_PASS to "Pass",

        STAT_POSSESSION to "Possession",
        STAT_SHOTS to "Shots",
        STAT_ON_TARGET to "On Target",
        STAT_FOULS to "Fouls",
        STAT_CARDS to "Cards"
    )

    private val idStrings = mapOf(
        MENU_NEW_GAME to "KARIR BARU",
        MENU_CONTINUE to "LANJUTKAN KARIR",
        MENU_CONTINUE_LOCKED to "LANJUTKAN KARIR (Terkunci)",
        MENU_SETTINGS to "PENGATURAN",
        MENU_LANGUAGE to "Bahasa: Indonesia",

        MATCH_DAY to "HARI PERTANDINGAN",
        MATCH_VS to " LAWAN ",
        MATCH_STARTING_XI to "Anda masuk STARTING XI!",
        MATCH_BENCH to "Anda di BANGKU CADANGAN.",
        MATCH_BENCH_SIM to "Menyimulasikan pertandingan dari bangku cadangan...",
        MATCH_KICK_OFF to "MULAI PERTANDINGAN",
        MATCH_FULL_TIME to "WAKTU HABIS",
        MATCH_CONTINUE to "Lanjutkan",

        MATCH_EVENT_GOAL to "GOL!",

        DECISION_TITLE_GOAL to "Peluang Mencetak Gol!",
        DECISION_DESC_GOAL to "Anda lolos dari pertahanan. Kiper maju menghadang.",
        DECISION_OPT_CHIP to "Tendangan Chip",
        DECISION_OPT_POWER to "Tendangan Keras",
        DECISION_OPT_PASS to "Oper Bola",

        STAT_POSSESSION to "Penguasaan Bola",
        STAT_SHOTS to "Tembakan",
        STAT_ON_TARGET to "Tepat Sasaran",
        STAT_FOULS to "Pelanggaran",
        STAT_CARDS to "Kartu"
    )
}
