package com.championstar.soccer

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.championstar.soccer.data.local.GameStorage
import com.championstar.soccer.data.static.ShopDatabase
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.*
import com.championstar.soccer.ui.screens.*
import com.championstar.soccer.ui.theme.ChampionstarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 2: Enforce Landscape Orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // World generation is heavy, ideally keep it in memory or load lazily.
        // For this architecture, we generate it once per app session or load from disk.
        var worldLeagues = WorldGenerator.generateWorld()

        setContent {
            ChampionstarTheme {
                RootNavigation(
                    initialLeagues = worldLeagues,
                    onWorldRegenerated = { worldLeagues = it } // Callback if New Game regenerates
                )
            }
        }
    }
}

@Composable
fun RootNavigation(
    initialLeagues: List<League>,
    onWorldRegenerated: (List<League>) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Global Game State (Lifted up)
    var player by remember { mutableStateOf<Player?>(null) }
    var leagues by remember { mutableStateOf(initialLeagues) }
    var hasSave by remember { mutableStateOf(GameStorage.hasSaveGame(context)) }

    NavHost(navController = navController, startDestination = "main_menu") {

        // 1. MAIN MENU
        composable("main_menu") {
            MainMenuScreen(
                hasSaveGame = hasSave,
                onNewGame = {
                    // Clear old data if any
                    GameStorage.deleteSave(context)
                    // Regenerate world to ensure fresh start
                    val newWorld = WorldGenerator.generateWorld()
                    onWorldRegenerated(newWorld)
                    leagues = newWorld
                    // Navigate to Creator
                    navController.navigate("character_creation")
                },
                onLoadGame = {
                    val savedState = GameStorage.loadGame(context)
                    if (savedState != null) {
                        player = savedState.player
                        // Ideally we load the world state too.
                        // For prototype simplicity, we use the static generated world
                        // but injecting the saved player back into their team is complex without full serialization.
                        // We will assume the `GameStorage` saves EVERYTHING (Player + Leagues).
                        // Since `GameStorage` DOES save leagues, we use them!
                        leagues = savedState.leagues
                        // Note: TimeEngine needs to be updated with saved date string
                        // TimeEngine.currentDate = ... (Parsing logic needed, skipping for prototype safety)

                        navController.navigate("main_game")
                    }
                }
            )
        }

        // 2. CHARACTER CREATION
        composable("character_creation") {
            CharacterCreationScreen(
                onCharacterCreated = { newPlayer ->
                    player = newPlayer

                    // Logic to find a team for the new player (Trial offer logic)
                    val trialOffers = CareerEngine.generateTrialOffers(newPlayer, leagues)
                    if (trialOffers.isNotEmpty()) {
                        val (team, contract) = trialOffers.first()
                        newPlayer.contract = contract
                        // Find the team instance in the `leagues` list and add player
                        val targetTeam = leagues.flatMap { it.teams }.find { it.id == team.id }
                        targetTeam?.players?.add(newPlayer)
                    } else {
                        // Fallback: Force join a Tier 4 team
                        val fallback = leagues.last().teams.first()
                        fallback.players.add(newPlayer)
                    }

                    // Save immediately
                    GameStorage.saveGame(context, newPlayer, leagues)
                    hasSave = true

                    navController.navigate("main_game") {
                        popUpTo("main_menu") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 3. MAIN GAME LOOP
        composable("main_game") {
            if (player != null) {
                MainGameScreen(
                    player = player!!,
                    leagues = leagues,
                    onSave = {
                        GameStorage.saveGame(context, player!!, leagues)
                    },
                    onExit = {
                        navController.navigate("main_menu") {
                            popUpTo("main_game") { inclusive = true }
                        }
                    }
                )
            } else {
                // Error state, go back
                LaunchedEffect(Unit) { navController.navigate("main_menu") }
            }
        }
    }
}

/**
 * Wrapper for the in-game UI (Dashboard/League/Shop navigation).
 * Replaces the old `MainApp` composable.
 */
@Composable
fun MainGameScreen(
    player: Player,
    leagues: List<League>,
    onSave: () -> Unit,
    onExit: () -> Unit
) {
    val navController = rememberNavController()
    var currentDate by remember { mutableStateOf(TimeEngine.currentDate.toString()) }
    var currentEvent by remember { mutableStateOf<GameTurnEvent?>(null) }

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("dashboard") {
            DashboardScreen(
                player = player,
                currentDate = currentDate,
                currentEvent = currentEvent,
                onAdvanceTime = {
                    val event = TimeEngine.advanceTime(player, leagues)
                    currentEvent = event
                    currentDate = TimeEngine.currentDate.toString()
                    onSave() // Auto-save on turn advance
                },
                onEventCompleted = {
                    currentEvent = null // Dismiss event, ready for next turn
                },
                onNavigateToLeague = { navController.navigate("league") },
                onNavigateToShop = { navController.navigate("shop") },
                onNavigateToBusiness = { navController.navigate("business") }, // NEW
                onSaveAndExit = {
                    onSave()
                    onExit()
                }
            )
        }
        composable("league") {
            Box(modifier = Modifier.fillMaxSize()) {
                val team = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }
                LeagueScreen(leagues, team?.leagueId)

                // Back Button Overlay
                FloatingActionButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            }
        }
        composable("shop") {
            Box(modifier = Modifier.fillMaxSize()) {
                ShopScreen(
                    player = player,
                    items = ShopDatabase.items,
                    onBuy = { itemId ->
                        ShopEngine.buyItem(player, itemId)
                    }
                )

                // Back Button Overlay
                FloatingActionButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            }
        }
        composable("business") {
            Box(modifier = Modifier.fillMaxSize()) {
                BusinessScreen(
                    player = player,
                    onBuy = { businessId ->
                        // Placeholder Logic for Business Buying
                        // Ideally call BusinessEngine.buyBusiness(player, businessId)
                    }
                )

                FloatingActionButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            }
        }
    }
}
