package com.championstar.soccer.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.domain.models.EventType
import com.championstar.soccer.simulation.engine.MatchEngine
import com.championstar.soccer.simulation.engine.SquadEngine
import com.championstar.soccer.ui.components.ClubAssetImage
import kotlinx.coroutines.delay
import kotlin.random.Random

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
    var interactiveState by remember { mutableStateOf(MatchEngine.createInteractiveMatch(team, opponent)) }
    var log by remember { mutableStateOf(mutableListOf<String>()) }
    var currentDecision by remember { mutableStateOf<MatchEngine.DecisionContext?>(null) }
    var isStarter by remember { mutableStateOf(false) }

    // Decision Logic
    val decisionMinutes = remember {
        val count = Random.nextInt(4, 8) // 4 to 7
        val minutes = mutableSetOf<Int>()
        while (minutes.size < count) {
            minutes.add(Random.nextInt(5, 88))
        }
        minutes.sorted()
    }

    // Simulation Loop
    LaunchedEffect(matchState) {
        if (matchState == MatchState.IN_PROGRESS) {
            while (interactiveState.minute < 90) {
                delay(150) // Simulation speed

                // Check for User Decision
                if (isStarter && decisionMinutes.contains(interactiveState.minute + 1)) {
                    // Pause for decision
                    val posGroup = if (player.position.contains("F") || player.position == "ST") "FW"
                                   else if (player.position.contains("M")) "MF"
                                   else if (player.position.contains("B")) "DF"
                                   else "GK"

                    currentDecision = MatchEngine.generateDecision(player, posGroup)
                    matchState = MatchState.DECISION_TIME
                    break // Exit loop to wait for user
                }

                // Simulate Minute
                val events = MatchEngine.simulateMinute(interactiveState)

                // Process Events for Log
                events.forEach { event ->
                    val color = if (event.type == EventType.GOAL) "GOAL" else "Event"
                    log.add(0, "Min ${event.minute}: ${event.description}")
                }
            }

            if (interactiveState.minute >= 90) {
                matchState = MatchState.FULL_TIME
                MatchEngine.finalizeInteractiveMatch(interactiveState)
            }
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
                        Text(team.name, color = Color.White, style = MaterialTheme.typography.headlineSmall)
                        Text(" VS ", color = Color.Gray, style = MaterialTheme.typography.headlineLarge)
                        Text(opponent.name, color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    val squad = SquadEngine.selectMatchSquad(team)
                    isStarter = squad.starters.any { it.id == player.id }

                    Text(
                        if (isStarter) "You are in the STARTING XI!" else "You are on the BENCH.",
                        color = if (isStarter) Color.Green else Color.Yellow,
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (!isStarter) {
                        Text("Simulating match from bench...", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { matchState = MatchState.IN_PROGRESS }) {
                        Text("KICK OFF")
                    }
                }
            }
            MatchState.IN_PROGRESS, MatchState.DECISION_TIME -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Scoreboard
                    Scoreboard(interactiveState)

                    Spacer(modifier = Modifier.height(8.dp))

                    // VISUALIZER (Center Stage)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp)) // Pitch Green
                    ) {
                        MatchVisualizer(interactiveState)

                        // Decision Overlay
                        if (matchState == MatchState.DECISION_TIME && currentDecision != null) {
                            DecisionOverlay(
                                decision = currentDecision!!,
                                onOptionSelected = { option ->
                                    val success = MatchEngine.processDecision(interactiveState, player, option)
                                    log.add(0, "Min ${interactiveState.minute}: You chose ${option.text}. ${if(success) "SUCCESS!" else "FAILED."}")
                                    currentDecision = null
                                    matchState = MatchState.IN_PROGRESS
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Commentary Log
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Live Commentary", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                            log.take(4).forEach {
                                Text(it, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            MatchState.FULL_TIME -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("FULL TIME", style = MaterialTheme.typography.displayMedium, color = Color.White)
                    Text("${interactiveState.homeScore} - ${interactiveState.awayScore}", style = MaterialTheme.typography.displayLarge, color = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { onMatchEnd(interactiveState.homeScore, interactiveState.awayScore) }) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}

@Composable
fun Scoreboard(state: MatchEngine.InteractiveMatchState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        modifier = Modifier.fillMaxWidth().height(60.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(state.homeTeam.name, color = Color.White, fontWeight = FontWeight.Bold)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${state.homeScore} - ${state.awayScore}", color = Color(0xFFFFD700), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${state.minute}'", color = Color.Green, fontSize = 12.sp)
            }
            Text(state.awayTeam.name, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DecisionOverlay(
    decision: MatchEngine.DecisionContext,
    onOptionSelected: (MatchEngine.DecisionOption) -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.85f),
        modifier = Modifier.fillMaxSize().padding(32.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(decision.title, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFFFD700))
            Spacer(modifier = Modifier.height(8.dp))
            Text(decision.description, style = MaterialTheme.typography.bodyLarge, color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)

            Spacer(modifier = Modifier.height(32.dp))

            decision.options.forEach { option ->
                Button(
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(option.text, fontWeight = FontWeight.Bold)
                        Text("Risk: ${option.riskRating} | Reward: ${option.rewardRating}", fontSize = 10.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchVisualizer(state: MatchEngine.InteractiveMatchState) {
    // Determine target based on possession
    // Home attacks Right (>), Away attacks Left (<)
    val isHomePossession = state.homePossessionTicks > state.awayPossessionTicks // Rough approximation for this frame

    // Animate Ball Position
    // We use a simple infinite transition that oscillates or moves based on state
    val infiniteTransition = rememberInfiniteTransition(label = "ball")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Draw Pitch Lines
        val lineColor = Color.White.copy(alpha = 0.5f)

        // Center Line
        drawLine(lineColor, Offset(w/2, 0f), Offset(w/2, h))
        drawCircle(lineColor, radius = h*0.15f, center = Offset(w/2, h/2), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))

        // Goals (Boxes)
        drawRect(lineColor, topLeft = Offset(0f, h*0.3f), size = androidx.compose.ui.geometry.Size(w*0.1f, h*0.4f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
        drawRect(lineColor, topLeft = Offset(w*0.9f, h*0.3f), size = androidx.compose.ui.geometry.Size(w*0.1f, h*0.4f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))

        // Players (Dots) - Randomly scattered but clustered
        // Home (Red) Left side mostly
        // Away (Blue) Right side mostly

        // Use a seed from minute to make them "move" every tick
        val random = Random(state.minute)

        // Home Players
        repeat(10) {
            val x = random.nextFloat() * w * 0.8f // Mostly left
            val y = random.nextFloat() * h
            drawCircle(Color.Red, radius = 8f, center = Offset(x, y))
        }

        // Away Players
        repeat(10) {
            val x = w - (random.nextFloat() * w * 0.8f) // Mostly right
            val y = random.nextFloat() * h
            drawCircle(Color.Blue, radius = 8f, center = Offset(x, y))
        }

        // Ball (Yellow)
        // Position depends on minute/possession
        val ballX = if (isHomePossession) w * 0.7f + pulse else w * 0.3f - pulse
        val ballY = h * 0.5f + (random.nextFloat() - 0.5f) * h * 0.5f

        drawCircle(Color.Yellow, radius = 6f, center = Offset(ballX, ballY))
    }
}
