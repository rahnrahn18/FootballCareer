package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.simulation.engine.MatchEngine
import com.championstar.soccer.simulation.engine.SquadEngine
import com.championstar.soccer.ui.components.ClubAssetImage
import kotlinx.coroutines.delay

enum class MatchState {
    PRE_MATCH,
    IN_PROGRESS,
    DECISION_TIME,
    FULL_TIME
}

@Composable
fun MatchScreen(
    player: Player,
    team: Team,
    opponent: Team,
    onMatchEnd: (Int, Int) -> Unit // home, away
) {
    var matchState by remember { mutableStateOf(MatchState.PRE_MATCH) }
    var minute by remember { mutableStateOf(0) }
    var homeScore by remember { mutableStateOf(0) }
    var awayScore by remember { mutableStateOf(0) }
    var log by remember { mutableStateOf(mutableListOf<String>()) }
    var currentDecision by remember { mutableStateOf<String?>(null) }
    var isStarter by remember { mutableStateOf(false) }

    // Simulation Loop
    LaunchedEffect(matchState) {
        if (matchState == MatchState.IN_PROGRESS) {
            while (minute < 90) {
                delay(200) // Speed of match
                minute += 2

                // Random Events
                if (Math.random() < 0.05) {
                    val event = if(Math.random() > 0.5) "${team.name} attacks!" else "${opponent.name} attacks!"
                    log.add(0, "Min $minute: $event")
                }

                // Decision Point (Only if player is playing)
                if (isStarter && Math.random() < 0.02 && currentDecision == null) {
                    currentDecision = "Pass or Shoot?"
                    matchState = MatchState.DECISION_TIME
                }
            }
            matchState = MatchState.FULL_TIME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        when (matchState) {
            MatchState.PRE_MATCH -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("MATCH DAY", style = MaterialTheme.typography.displayMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ClubAssetImage(modifier = Modifier.size(64.dp))
                        Text(" VS ", color = Color.Gray, style = MaterialTheme.typography.headlineLarge)
                        ClubAssetImage(modifier = Modifier.size(64.dp)) // Ideally distinct logos
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    val squad = SquadEngine.selectMatchSquad(team)
                    isStarter = squad.starters.any { it.id == player.id }

                    Text(
                        if (isStarter) "You are in the STARTING XI!" else "You are on the BENCH.",
                        color = if (isStarter) Color.Green else Color.Yellow,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { matchState = MatchState.IN_PROGRESS }) {
                        Text("KICK OFF")
                    }
                }
            }
            MatchState.IN_PROGRESS, MatchState.DECISION_TIME -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Scoreboard
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(team.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("$homeScore - $awayScore", color = Color(0xFFFFD700), style = MaterialTheme.typography.displaySmall)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(opponent.name, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    LinearProgressIndicator(
                        progress = { minute / 90f },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Green
                    )

                    // Commentary
                    Text("Live Commentary", color = Color.Gray, modifier = Modifier.padding(8.dp))
                    Card(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha=0.5f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            log.take(6).forEach {
                                Text(it, color = Color.White, fontSize = 12.sp)
                                Divider(color = Color.DarkGray)
                            }
                        }
                    }
                }

                // Decision Popup Overlay
                if (matchState == MatchState.DECISION_TIME) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("Quick Decision!") },
                        text = { Text("You have the ball near the box. What do you do?") },
                        confirmButton = {
                            Button(onClick = {
                                // Logic: Shoot
                                if (Math.random() > 0.5) {
                                    homeScore++
                                    log.add(0, "GOAL!!! You scored!")
                                } else {
                                    log.add(0, "Missed! Wide.")
                                }
                                currentDecision = null
                                matchState = MatchState.IN_PROGRESS
                            }) { Text("Shoot") }
                        },
                        dismissButton = {
                            Button(onClick = {
                                // Logic: Pass
                                log.add(0, "Nice pass to teammate.")
                                currentDecision = null
                                matchState = MatchState.IN_PROGRESS
                            }) { Text("Pass") }
                        }
                    )
                }
            }
            MatchState.FULL_TIME -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("FULL TIME", style = MaterialTheme.typography.displayMedium, color = Color.White)
                    Text("$homeScore - $awayScore", style = MaterialTheme.typography.displayLarge, color = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { onMatchEnd(homeScore, awayScore) }) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}
