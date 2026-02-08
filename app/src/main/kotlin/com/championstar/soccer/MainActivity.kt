package com.championstar.soccer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.championstar.soccer.data.static.ShopDatabase
import com.championstar.soccer.simulation.engine.WorldGenerator
import com.championstar.soccer.simulation.engine.CareerEngine
import com.championstar.soccer.simulation.engine.TimeEngine
import com.championstar.soccer.simulation.engine.ShopEngine
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Currency

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Generate World
        val leagues = WorldGenerator.generateWorld()

        // 2. Start Career (Zero to Hero)
        val myPlayer = CareerEngine.startCareer("User Hero", "ST", "Europe")

        // Grant free currency for demo
        myPlayer.stars = 50
        myPlayer.glory = 20

        // 3. Join Team
        val trialOffers = CareerEngine.generateTrialOffers(myPlayer, leagues)
        if (trialOffers.isNotEmpty()) {
            val (team, contract) = trialOffers.first()
            myPlayer.contract = contract
            team.players.add(myPlayer)
        }

        setContent {
            GameLoopView(leagues, myPlayer)
        }
    }
}

@Composable
fun GameLoopView(leagues: List<League>, player: Player) {
    val gameLog = remember { mutableStateOf("Welcome! You have joined a club.") }
    val currentWeek = remember { mutableStateOf(TimeEngine.currentDate.toString()) }
    val playerStats = remember { mutableStateOf("") }

    fun updateStats() {
        playerStats.value = "OVR ${String.format("%.1f", player.overallRating)} | Sta ${String.format("%.1f", player.stamina)} | Age ${player.age}/${player.retirementAge}\n" +
                            "⭐ Stars: ${player.stars} | 🏆 Glory: ${player.glory}"
    }
    updateStats()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Date: ${currentWeek.value}")
        Text("Player: ${player.name} (${player.position})")
        Text(playerStats.value)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // --- TIME SKIP ---
            val log = TimeEngine.jumpToNextEvent(player, leagues)
            gameLog.value = log
            currentWeek.value = TimeEngine.currentDate.toString()
            updateStats()
        }) {
            Text("Simulate to Next Event")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // --- SHOP DEMO ---
            val result = ShopEngine.buyItem(player, "S_01") // Buy Energy Drink
            gameLog.value = "SHOP: $result"
            updateStats()
        }) {
            Text("Buy Energy Drink (1 Star)")
        }

        Button(onClick = {
             // --- PREMIUM SHOP DEMO ---
            val result = ShopEngine.buyItem(player, "G_01") // Reset Age
            gameLog.value = "PREMIUM SHOP: $result"
            updateStats()
        }) {
            Text("Reset Age (50 Glory) - Fail Check")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("--- Log ---")
        Text(gameLog.value)
    }
}
