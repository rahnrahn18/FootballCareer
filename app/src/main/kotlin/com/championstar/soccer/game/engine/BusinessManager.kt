package com.championstar.soccer.game.engine

import com.championstar.soccer.data.entities.BusinessEntity
import com.championstar.soccer.data.entities.PlayerEntity

class BusinessManager {

    fun calculateWeeklyIncome(businesses: List<BusinessEntity>): Long {
        return businesses.sumOf { if (it.level > 0) it.baseIncome * it.level else 0 }
    }

    fun buyBusiness(player: PlayerEntity, business: BusinessEntity): Pair<PlayerEntity, BusinessEntity>? {
        val cost = business.baseCost * (business.level + 1)
        if (player.money >= cost) {
            val newPlayer = player.copy(money = player.money - cost)
            val newBusiness = business.copy(level = business.level + 1)
            return Pair(newPlayer, newBusiness)
        }
        return null
    }
}