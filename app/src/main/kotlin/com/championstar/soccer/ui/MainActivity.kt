package com.championstar.soccer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.championstar.soccer.MyApplication
import com.championstar.soccer.data.entities.PlayerEntity
import com.championstar.soccer.game.engine.MatchResult
import com.championstar.soccer.ui.navigation.Screen
import com.championstar.soccer.ui.screens.*
import com.championstar.soccer.ui.theme.SoccerCareerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MyApplication
        val gameEngine = app.gameEngine
        val playerRepo = app.playerRepository
        val businessRepo = app.businessRepository

        setContent {
            SoccerCareerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()

                    val playerState by playerRepo.player.observeAsState()
                    val businessesState by businessRepo.allBusinesses.observeAsState(emptyList())

                    // Transient State
                    var matchResult by remember { mutableStateOf<MatchResult?>(null) }

                    NavHost(navController = navController, startDestination = Screen.MainMenu.route) {

                        composable(Screen.MainMenu.route) {
                            MainMenuScreen(
                                onNewGame = { navController.navigate(Screen.CharacterCreation.route) },
                                onLoadGame = {
                                    if (playerState != null) {
                                        navController.navigate(Screen.Dashboard.route)
                                    }
                                },
                                hasSave = playerState != null
                            )
                        }

                        composable(Screen.CharacterCreation.route) {
                            CharacterCreationScreen(
                                onCharacterCreated = { name, position, appearance ->
                                    scope.launch {
                                        val newPlayer = PlayerEntity(
                                            name = name,
                                            position = position,
                                            appearanceJson = appearance
                                        )
                                        // Clear old data if any (optional)
                                        // playerRepo.deleteAll() // If needed
                                        playerRepo.insert(newPlayer)
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(Screen.MainMenu.route) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable(Screen.Dashboard.route) {
                            val player = playerState
                            if (player != null) {
                                DashboardScreen(
                                    player = player,
                                    onNavigate = { route ->
                                        if (route == "match") {
                                            matchResult = null // Reset previous match
                                            navController.navigate(Screen.Match.route)
                                        } else if (route == "business") {
                                            navController.navigate(Screen.Business.route)
                                        } else {
                                            // Handle other routes or show toast
                                        }
                                    },
                                    onAdvanceWeek = {
                                        scope.launch {
                                            gameEngine.advanceWeek()
                                        }
                                    }
                                )
                            } else {
                                // Loading or Error
                                Text("Loading Player Data...")
                            }
                        }

                        composable(Screen.Match.route) {
                            val player = playerState
                            if (player != null) {
                                MatchScreen(
                                    player = player,
                                    matchResult = matchResult,
                                    onSimulate = {
                                        scope.launch {
                                            // Calculate opponent strength based on league logic (simplified here)
                                            val opponentStrength = 60 + (player.week / 2)
                                            matchResult = gameEngine.playMatch(opponentStrength)
                                        }
                                    },
                                    onFinish = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable(Screen.Business.route) {
                            val player = playerState
                            if (player != null) {
                                BusinessScreen(
                                    player = player,
                                    businesses = businessesState,
                                    onBuy = { business ->
                                        scope.launch {
                                            gameEngine.buyBusiness(business)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}