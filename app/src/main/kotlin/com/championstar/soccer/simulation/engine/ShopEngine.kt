package com.championstar.soccer.simulation.engine

import com.championstar.soccer.data.static.ShopDatabase
import com.championstar.soccer.domain.models.Currency
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.ShopItem

object ShopEngine {

    /**
     * Attempts to purchase an item.
     * Returns a success message or failure reason.
     */
    fun buyItem(player: Player, itemId: String): String {
        val item = ShopDatabase.items.find { it.id == itemId } ?: return "Item not found."

        // Check Affordability
        val canAfford = when (item.currency) {
            Currency.STAR -> player.stars >= item.cost
            Currency.GLORY -> player.glory >= item.cost
        }

        if (!canAfford) {
            return "Not enough ${item.currency}!"
        }

        // Specific Item Logic Checks
        if (item.name.contains("Age Extension") && player.retirementAge >= 40) {
            return "Cannot extend career beyond 40."
        }
        if (item.id == "G_01" && player.age <= 20) {
            return "You are already young enough!"
        }

        // Deduct Cost
        when (item.currency) {
            Currency.STAR -> player.stars -= item.cost
            Currency.GLORY -> player.glory -= item.cost
        }

        // Apply Effect
        val message = item.effect(player)
        return "Bought ${item.name}! $message"
    }
}
