package com.championstar.soccer

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.simulation.engine.*
import com.championstar.soccer.ui.screens.*
import com.championstar.soccer.ui.theme.ChampionstarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Removed World Generation from Main Thread here.
        // It will be handled in the UI via Coroutines.

        setContent {
            ChampionstarTheme {
                RootNavigation()
            }
        }
    }
}

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var player by remember { mutableStateOf<Player?>(null) }
    var leagues by remember { mutableStateOf<List<League>>(emptyList()) }
    var hasSave by remember { mutableStateOf(GameStorage.hasSaveGame(context)) }
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFFFFD700))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Simulating World... Please Wait", color = Color.White)
            }
        }
        return
    }

    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") {
            MainMenuScreen(
                hasSaveGame = hasSave,
                onNewGame = {
                    scope.launch {
                        isLoading = true
                        GameStorage.deleteSave(context)
                        // Generate World on IO Thread
                        val newWorld = withContext(Dispatchers.IO) {
                            WorldGenerator.generateWorld()
                        }
                        leagues = newWorld
                        isLoading = false
                        navController.navigate("character_creation")
                    }
                },
                onLoadGame = {
                    scope.launch {
                        isLoading = true
                        // Load Game on IO Thread
                        val savedState = withContext(Dispatchers.IO) {
                            GameStorage.loadGame(context)
                        }
                        if (savedState != null) {
                            player = savedState.player
                            leagues = savedState.leagues
                            isLoading = false
                            navController.navigate("main_game")
                        } else {
                            isLoading = false
                            // Handle load error
                        }
                    }
                }
            )
        }
        composable("character_creation") {
            CharacterCreationScreen(
                onCharacterCreated = { newPlayer ->
                    scope.launch {
                        isLoading = true
                        player = newPlayer

                        // Process trial logic
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

                        // Save on IO Thread
                        withContext(Dispatchers.IO) {
                            GameStorage.saveGame(context, newPlayer, leagues)
                        }
                        hasSave = true
                        isLoading = false

                        navController.navigate("main_game") { popUpTo("main_menu") { inclusive = true } }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("main_game") {
            if (player != null) {
                MainGameScreen(
                    player = player!!,
                    leagues = leagues,
                    onSave = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                GameStorage.saveGame(context, player!!, leagues)
                            }
                        }
                    },
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
                onNavigateToTraining = { navController.navigate("training") },
                onSaveAndExit = { onSave(); onExit() }
            )
        }
        composable("training") {
            FullscreenScreenWithBack(navController) {
                TrainingScreen(player) { success ->
                    navController.popBackStack()
                }
            }
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
        composable("match") {
             val team = leagues.flatMap { it.teams }.find { t -> t.players.any { it.id == player.id } }
             val opponent = leagues.flatMap { it.teams }.filter { it.id != team?.id }.randomOrNull()

             if (team != null && opponent != null) {
                FullscreenScreenWithBack(navController) {
                    MatchScreen(
                        player = player,
                        team = team,
                        opponent = opponent,
                        onMatchEnd = { h, a ->
                            navController.popBackStack()
                        }
                    )
                }
             } else {
                 LaunchedEffect(Unit) { navController.popBackStack() }
             }
        }
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
