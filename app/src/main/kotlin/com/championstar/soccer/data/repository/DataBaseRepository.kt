package com.championstar.soccer.data.repository

import android.content.Context
import com.championstar.soccer.data.model.Club
import com.championstar.soccer.data.model.ClubTier
import com.championstar.soccer.data.model.League
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import kotlin.random.Random

object DatabaseRepository {

    private var leagues: List<League> = emptyList()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        try {
            val jsonString = context.assets.open("gamedata.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<League>>() {}.type
            var loadedLeagues: List<League> = Gson().fromJson(jsonString, listType)

            // Foundation enhancement: Assign tiers and reputations programmatically
            leagues = loadedLeagues.map { league ->
                val (defaultTier, minRep, maxRep) = when (league.id) {
                    11, 2, 12, 9, 7 -> Triple(ClubTier.PROFESSIONAL, 75, 95) // Top 5 leagues
                    43 -> Triple(ClubTier.PROFESSIONAL, 85, 99) // International
                    else -> Triple(ClubTier.SEMI_PRO, 50, 74)
                }
                val updatedClubs = league.clubs.map { club ->
                    club.copy(
                        tier = defaultTier,
                        reputation = Random.nextInt(minRep, maxRep + 1)
                    )
                }
                league.copy(clubs = updatedClubs)
            }

            isInitialized = true
            println("Database loaded successfully. Found ${leagues.size} leagues.")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Error loading database: ${e.message}")
        }
    }

    fun getLeagues(): List<League> {
        return leagues
    }

    fun getClubById(clubId: Int): Club? {
        if (clubId == 0) return Club.unattached()
        return leagues.flatMap { it.clubs }.find { it.id == clubId }
    }
}