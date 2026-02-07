package com.championstar.soccer.game.engine

import com.championstar.soccer.data.entities.PlayerEntity
import com.championstar.soccer.data.entities.BusinessEntity
import com.championstar.soccer.data.entities.StoryEventEntity
import com.championstar.soccer.data.repository.PlayerRepository
import com.championstar.soccer.data.repository.BusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameEngine(
    private val playerRepo: PlayerRepository,
    private val businessRepo: BusinessRepository
) {
    private val matchEngine = MatchEngine()
    private val storyManager = StoryManager()
    private val businessManager = BusinessManager()

    private val _currentEvent = MutableStateFlow<StoryEventEntity?>(null)
    val currentEvent: StateFlow<StoryEventEntity?> = _currentEvent.asStateFlow()

    suspend fun advanceWeek() {
        val player = playerRepo.getPlayerSync() ?: return

        // 2. Calculate Business Income
        val businesses = businessRepo.getAllBusinessesSync()
        val income = businessManager.calculateWeeklyIncome(businesses)
        val newMoney = player.money + income

        // 3. Update Player Age/Stats
        var newWeek = player.week + 1
        var newYear = player.year
        if (newWeek > 52) {
            newWeek = 1
            newYear++
        }

        val updatedPlayer = player.copy(
            week = newWeek,
            year = newYear,
            money = newMoney
        )
        playerRepo.update(updatedPlayer)

        // 4. Trigger Story Event
        val event = storyManager.checkForEvents(updatedPlayer)
        if (event != null) {
            _currentEvent.value = event
        }
    }

    suspend fun resolveEvent(choiceId: String) {
        val event = _currentEvent.value ?: return
        val player = playerRepo.getPlayerSync() ?: return

        val listType = object : TypeToken<List<Choice>>() {}.type
        val choices: List<Choice> = Gson().fromJson(event.choicesJson, listType)
        val choice = choices.find { it.id == choiceId } ?: return

        val newPlayer = storyManager.applyConsequence(player, choice)
        playerRepo.update(newPlayer)
        _currentEvent.value = null
    }

    suspend fun playMatch(opponentStrength: Int): MatchResult? {
        val player = playerRepo.getPlayerSync() ?: return null
        val result = matchEngine.simulateMatch(player, opponentStrength)
        return result
    }

    suspend fun buyBusiness(business: BusinessEntity): Boolean {
        val player = playerRepo.getPlayerSync() ?: return false
        val result = businessManager.buyBusiness(player, business)
        if (result != null) {
            playerRepo.update(result.first)
            businessRepo.update(result.second)
            return true
        }
        return false
    }
}