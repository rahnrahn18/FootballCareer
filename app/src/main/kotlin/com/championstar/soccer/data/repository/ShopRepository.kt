package com.championstar.soccer.data.repository

import com.championstar.soccer.data.model.BuffType
import com.championstar.soccer.data.model.ShopItem

object ShopRepository {

    private val allItems = listOf(
        ShopItem(
            id = "boot_001",
            name = "Kinetik Starter",
            description = "Sepatu standar untuk pemula. Memberikan sedikit peningkatan pada kecepatan.",
            price = 250.0,
            buffType = BuffType.SPEED,
            buffValue = 1,
            iconResourceName = "boot_kinetik_starter"
        ),
        ShopItem(
            id = "boot_002",
            name = "Apex Predator",
            description = "Dirancang untuk striker murni. Ujung sepatu yang diperkuat meningkatkan akurasi tendangan.",
            price = 1200.0,
            buffType = BuffType.FINISHING,
            buffValue = 3,
            iconResourceName = "boot_apex_predator"
        ),
        ShopItem(
            id = "boot_003",
            name = "Ghost Runner",
            description = "Sangat ringan, terasa seperti tidak memakai apa-apa. Sempurna untuk menggiring bola.",
            price = 1500.0,
            buffType = BuffType.DRIBBLING,
            buffValue = 3,
            iconResourceName = "boot_ghost_runner"
        )
    )

    // NAMA FUNGSI DIPERBAIKI
    fun getAllItems(): List<ShopItem> {
        return allItems
    }

    fun getItemById(id: String?): ShopItem? {
        if (id == null) return null
        return allItems.find { it.id == id }
    }
}