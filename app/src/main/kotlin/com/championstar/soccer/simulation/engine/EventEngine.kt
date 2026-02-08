package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.domain.models.Player

data class RandomEvent(
    val title: String,
    val description: String,
    val choices: List<EventChoice>
)

data class EventChoice(
    val text: String,
    val consequence: (Player) -> String
)

object EventEngine {

    /**
     * Generates a weekly random event based on player status.
     * Tells the "Zero to Hero" story through dilemmas.
     */
    fun generateWeeklyEvent(player: Player): RandomEvent? {
        // 20% chance of event per week
        if (!GameMath.chance(0.2)) return null

        val events = mutableListOf<RandomEvent>()

        // --- Low Reputation / Early Career Events ---
        if (player.reputation < 2.0) {
            events.add(RandomEvent(
                "Extra Training Opportunity",
                "The coach is offering extra sessions after practice. It's grueling work.",
                listOf(
                    EventChoice("Attend (Cost: 10 Stamina)") { p ->
                        p.stamina = (p.stamina - 10.0).coerceAtLeast(0.0)
                        p.overallRating += 0.2
                        "Coach is impressed. Skill +0.2, Stamina -10."
                    },
                    EventChoice("Skip and Rest") { p ->
                        p.stamina = (p.stamina + 5.0).coerceAtMost(100.0)
                        "You feel rested. Stamina +5."
                    }
                )
            ))

            events.add(RandomEvent(
                "Teammate Conflict",
                "A senior player criticizes your positioning in training.",
                listOf(
                    EventChoice("Apologize and Listen") { p ->
                        p.morale = (p.morale + 2.0).coerceAtMost(100.0)
                        "He appreciates your humility. Morale +2."
                    },
                    EventChoice("Argue Back") { p ->
                        p.morale = (p.morale - 5.0).coerceAtLeast(0.0)
                        p.reputation += 0.1 // Showing spine?
                        "The locker room is tense. Morale -5, Rep +0.1."
                    }
                )
            ))
        }

        // --- High Form Events ---
        if (player.form > 70.0) {
            events.add(RandomEvent(
                "Local Interview",
                "A local newspaper wants to interview the rising star.",
                listOf(
                    EventChoice("Accept (Cost: $50 for suit)") { p ->
                        // p.money -= 50
                        p.reputation += 0.5
                        p.morale = (p.morale + 2.0).coerceAtMost(100.0)
                        "Great exposure! Reputation +0.5."
                    },
                    EventChoice("Decline (Focus on game)") { p ->
                        p.form = (p.form + 2.0).coerceAtMost(100.0)
                        "Focus remains sharp. Form +2."
                    }
                )
            ))
        }

        // --- Agent / Transfer Events ---
        if (player.agent != null && player.contract != null && player.contract!!.yearsRemaining <= 1) {
             events.add(RandomEvent(
                "Contract Concerns",
                "Your agent suggests pushing for a renewal.",
                listOf(
                    EventChoice("Demand New Deal") { p ->
                        p.morale = (p.morale - 5.0).coerceAtLeast(0.0)
                        "Club is annoyed but listening. Morale -5."
                    },
                    EventChoice("Wait until end of season") { p ->
                        p.morale = (p.morale + 2.0).coerceAtMost(100.0)
                        "Focus on football. Morale +2."
                    }
                )
            ))
        }

        return events.randomOrNull()
    }
}
