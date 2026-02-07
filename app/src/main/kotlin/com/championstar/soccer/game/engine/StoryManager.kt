package com.championstar.soccer.game.engine

import com.championstar.soccer.data.entities.PlayerEntity
import com.championstar.soccer.data.entities.StoryEventEntity
import com.google.gson.Gson
import kotlin.random.Random

data class Choice(
    val id: String,
    val text: String,
    val consequence: Consequence
)

data class Consequence(
    val moneyChange: Long = 0,
    val moraleChange: Int = 0,
    val skillChange: String? = null,
    val skillChangeAmount: Int = 0
)

class StoryManager {
    private val gson = Gson()

    fun checkForEvents(player: PlayerEntity): StoryEventEntity? {
        val week = player.week
        val events = mutableListOf<StoryEventEntity>()

        // Check for triggers (e.g. specific week, low morale, high skill)
        if (week == 1) {
            events.add(createEvent(
                "Welcome to the Club",
                "You have just joined ${player.teamName}. The manager wants a word with you.",
                listOf(
                    Choice("TrainHard", "Promise to train hard", Consequence(moraleChange = 5, skillChange = "Stamina", skillChangeAmount = 2)),
                    Choice("Chill", "Take it easy", Consequence(moraleChange = -5))
                )
            ))
        }

        if (player.morale < 50 && Random.nextInt(100) < 30) {
            events.add(createEvent(
                "Feeling Down",
                "Your performance has been lacking lately. Fans are criticizing you.",
                listOf(
                    Choice("Ignore", "Ignore them", Consequence(moraleChange = -10)),
                    Choice("Apologize", "Apologize publicly", Consequence(moraleChange = 10, moneyChange = -100))
                )
            ))
        }

        if (player.money > 10000 && Random.nextInt(100) < 10) {
            events.add(createEvent(
                "Investment Opportunity",
                "A shady businessman offers you a deal.",
                listOf(
                    Choice("Invest", "Invest $5000", Consequence(moneyChange = -5000, moraleChange = 5)), // Could have random outcome later
                    Choice("Decline", "Decline", Consequence(moraleChange = 0))
                )
            ))
        }

        return if (events.isNotEmpty()) events.random() else null
    }

    private fun createEvent(title: String, description: String, choices: List<Choice>): StoryEventEntity {
        return StoryEventEntity(
            title = title,
            description = description,
            choicesJson = gson.toJson(choices)
        )
    }

    fun applyConsequence(player: PlayerEntity, choice: Choice): PlayerEntity {
        val consequence = choice.consequence
        var newMoney = player.money + consequence.moneyChange
        var newMorale = (player.morale + consequence.moraleChange).coerceIn(0, 100)

        val newSkills = player.skills.toMutableMap()
        if (consequence.skillChange != null) {
            val currentSkill = newSkills[consequence.skillChange] ?: 50
            newSkills[consequence.skillChange] = (currentSkill + consequence.skillChangeAmount).coerceIn(0, 99)
        }

        return player.copy(
            money = newMoney,
            morale = newMorale,
            skills = newSkills
        )
    }
}