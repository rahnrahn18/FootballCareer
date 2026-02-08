package com.championstar.soccer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.championstar.soccer.data.static.ShopDatabase
import com.championstar.soccer.domain.models.Contract
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.CareerEngine
import com.championstar.soccer.simulation.engine.ShopEngine
import com.championstar.soccer.simulation.engine.TimeEngine
import com.championstar.soccer.simulation.engine.WorldGenerator
import com.championstar.soccer.ui.screens.DashboardScreen
import com.championstar.soccer.ui.screens.LeagueScreen
import com.championstar.soccer.ui.screens.ShopScreen
import com.championstar.soccer.ui.theme.ChampionstarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize World
        // Note: Running on main thread for demo prototype. In production, use ViewModel/Coroutines.
        val leagues = WorldGenerator.generateWorld()

        // 2. Start Career
        val myPlayer = CareerEngine.startCareer("User Hero", "ST", "Europe")
        myPlayer.stars = 50
        myPlayer.glory = 20

        // 3. Initial Team Join
        val trialOffers = CareerEngine.generateTrialOffers(myPlayer, leagues)
        if (trialOffers.isNotEmpty()) {
            val (team, contract) = trialOffers.first()
            myPlayer.contract = contract
            team.players.add(myPlayer)
        }

        setContent {
            ChampionstarTheme {
                MainApp(leagues, myPlayer)
            }
        }
    }
}

@Composable
fun MainApp(leagues: List<League>, player: Player) {
    val navController = rememberNavController()

    // State holders
    var currentDate by remember { mutableStateOf(TimeEngine.currentDate.toString()) }
    var triggerRecomposition by remember { mutableStateOf(0) } // Forces refresh on object mutation

    // Dummy state usage to force recomposition when trigger changes
    val refresh = triggerRecomposition

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = false, // Simplified
                    onClick = { navController.navigate("dashboard") },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("league") },
                    icon = { Text("🏆") },
                    label = { Text("League") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("shop") },
                    icon = { Text("🛒") },
                    label = { Text("Shop") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    player = player,
                    currentDate = currentDate,
                    onSimulate = {
                        // Core Logic Call
                        TimeEngine.jumpToNextEvent(player, leagues)
                        // Update UI
                        currentDate = TimeEngine.currentDate.toString()
                        triggerRecomposition++
                    }
                )
            }
            composable("league") {
                // Find player's league
                val team = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }
                val currentLeague = if (team != null) leagues.find { it.id == team.leagueId } else leagues.first()

                // Pass single league object or ID. Screen takes list + ID.
                LeagueScreen(leagues, currentLeague?.id)
            }
            composable("shop") {
                ShopScreen(
                    player = player,
                    items = ShopDatabase.items,
                    onBuy = { itemId ->
                        val result = ShopEngine.buyItem(player, itemId)
                        // Trigger update
                        triggerRecomposition++
                    }
                )
            }
        }
    }
}
