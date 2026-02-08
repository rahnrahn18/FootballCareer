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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        var worldLeagues = WorldGenerator.generateWorld()
        setContent {
            ChampionstarTheme {
                RootNavigation(
                    initialLeagues = worldLeagues,
                    onWorldRegenerated = { worldLeagues = it }
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

    var player by remember { mutableStateOf<Player?>(null) }
    var leagues by remember { mutableStateOf(initialLeagues) }
    var hasSave by remember { mutableStateOf(GameStorage.hasSaveGame(context)) }

    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") {
            MainMenuScreen(
                hasSaveGame = hasSave,
                onNewGame = {
                    GameStorage.deleteSave(context)
                    val newWorld = WorldGenerator.generateWorld()
                    onWorldRegenerated(newWorld)
                    leagues = newWorld
                    navController.navigate("character_creation")
                },
                onLoadGame = {
                    val savedState = GameStorage.loadGame(context)
                    if (savedState != null) {
                        player = savedState.player
                        leagues = savedState.leagues
                        navController.navigate("main_game")
                    }
                }
            )
        }
        composable("character_creation") {
            CharacterCreationScreen(
                onCharacterCreated = { newPlayer ->
                    player = newPlayer
                    val trialOffers = CareerEngine.generateTrialOffers(newPlayer, leagues)
                    if (trialOffers.isNotEmpty()) {
                        val (team, contract) = trialOffers.first()
                        newPlayer.contract = contract
                        val targetTeam = leagues.flatMap { it.teams }.find { it.id == team.id }
                        targetTeam?.players?.add(newPlayer)
                    } else {
                        val fallback = leagues.last().teams.first()
                        fallback.players.add(newPlayer)
                    }
                    GameStorage.saveGame(context, newPlayer, leagues)
                    hasSave = true
                    navController.navigate("main_game") { popUpTo("main_menu") { inclusive = true } }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("main_game") {
            if (player != null) {
                MainGameScreen(
                    player = player!!,
                    leagues = leagues,
                    onSave = { GameStorage.saveGame(context, player!!, leagues) },
                    onExit = { navController.navigate("main_menu") { popUpTo("main_game") { inclusive = true } } }
                )
            } else {
                LaunchedEffect(Unit) { navController.navigate("main_menu") }
            }
        }
    }
}

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
                    onSave()
                },
                onEventCompleted = { currentEvent = null },
                onNavigateToLeague = { navController.navigate("league") },
                onNavigateToShop = { navController.navigate("shop") },
                onNavigateToBusiness = { navController.navigate("business") },
                onSaveAndExit = { onSave(); onExit() }
            )
        }
        composable("league") {
            FullscreenScreenWithBack(navController) {
                val team = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }
                LeagueScreen(leagues, team?.leagueId)
            }
        }
        composable("shop") {
            FullscreenScreenWithBack(navController) {
                ShopScreen(player, ShopDatabase.items, { ShopEngine.buyItem(player, it) })
            }
        }
        composable("business") {
            FullscreenScreenWithBack(navController) {
                BusinessScreen(player, { /* TODO logic */ })
            }
        }
        // New Screens
        composable("club") {
            FullscreenScreenWithBack(navController) {
                val team = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }
                ClubScreen(team)
            }
        }
        composable("transfers") { FullscreenScreenWithBack(navController) { TransferScreen() } }
        composable("sponsors") { FullscreenScreenWithBack(navController) { SponsorScreen() } }
        composable("ranking") { FullscreenScreenWithBack(navController) { RankingScreen() } }
        composable("achievements") { FullscreenScreenWithBack(navController) { AchievementScreen(player) } }
        composable("match") { FullscreenScreenWithBack(navController) { MatchScreen() } }
    }
}

@Composable
fun FullscreenScreenWithBack(navController: androidx.navigation.NavController, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        FloatingActionButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(Icons.Filled.ArrowBack, "Back")
        }
    }
}
