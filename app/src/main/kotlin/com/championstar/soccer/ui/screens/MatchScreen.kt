package com.championstar.soccer.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.championstar.soccer.core.Localization
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.domain.models.EventType
import com.championstar.soccer.simulation.engine.MatchEngine
import com.championstar.soccer.simulation.engine.SquadEngine
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

    // Speed Control (1000ms = 1x, 500ms = 2x)
    var simulationSpeed by remember { mutableLongStateOf(1000L) }

    // Substitution UI
    var showSubDialog by remember { mutableStateOf(false) }

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
    LaunchedEffect(matchState, simulationSpeed) {
        if (matchState == MatchState.IN_PROGRESS) {
            while (!interactiveState.isFinished) {
                if (showSubDialog) {
                    delay(500) // Pause/Slow loop while dialog open (or use separate pause state)
                    continue
                }

                delay(simulationSpeed)

                // Check for User Decision (Only if user is in lineup)
                val userInLineup = interactiveState.homeLineup.any { it.id == player.id }
                if (userInLineup && decisionMinutes.contains(interactiveState.minute + 1)) {
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
                    log.add(0, "Min ${event.minute}: ${event.description}")
                }

                if (interactiveState.isFinished) {
                    matchState = MatchState.FULL_TIME
                }
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

                    // Check if user is in initial lineup
                    isStarter = interactiveState.homeLineup.any { it.id == player.id }

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
                    // Header: Scoreboard + Speed Control
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Scoreboard(interactiveState, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        // Speed Controls & Subs
                        Row {
                             Button(
                                onClick = { showSubDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                modifier = Modifier.height(60.dp),
                                enabled = interactiveState.homeSubsUsed < 5
                            ) { Text("SUB (${interactiveState.homeSubsUsed}/5)") }
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = { simulationSpeed = 1000L },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (simulationSpeed == 1000L) MaterialTheme.colorScheme.primary else Color.DarkGray
                                ),
                                modifier = Modifier.height(60.dp)
                            ) { Text("1x") }
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = { simulationSpeed = 500L },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (simulationSpeed == 500L) MaterialTheme.colorScheme.primary else Color.DarkGray
                                ),
                                modifier = Modifier.height(60.dp)
                            ) { Text("2x") }
                        }
                    }

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
                            .height(120.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(8.dp),
                            reverseLayout = false
                        ) {
                            items(log) { item ->
                                Text(item, color = Color.White, fontSize = 12.sp)
                                Divider(color = Color.DarkGray, thickness = 0.5.dp)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val totalTicks = (interactiveState.homePossessionTicks + interactiveState.awayPossessionTicks).toFloat()
                            val homePoss = if(totalTicks > 0) ((interactiveState.homePossessionTicks / totalTicks) * 100).toInt() else 50
                            val awayPoss = 100 - homePoss

                            StatRow(Localization.get(Localization.STAT_POSSESSION), "$homePoss%", "$awayPoss%")
                            StatRow(Localization.get(Localization.STAT_SHOTS), "${interactiveState.homeShots}", "${interactiveState.awayShots}")
                            StatRow(Localization.get(Localization.STAT_ON_TARGET), "${interactiveState.homeOnTarget}", "${interactiveState.awayOnTarget}")
                            StatRow(Localization.get(Localization.STAT_FOULS), "${interactiveState.homeFouls}", "${interactiveState.awayFouls}")
                            StatRow(Localization.get(Localization.STAT_CARDS), "${interactiveState.homeYellows}/${interactiveState.homeReds}", "${interactiveState.awayYellows}/${interactiveState.awayReds}")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { onMatchEnd(interactiveState.homeScore, interactiveState.awayScore) }) {
                        Text("Continue")
                    }
                }
            }
        }

        if (showSubDialog) {
            SubstitutionDialog(
                state = interactiveState,
                onDismiss = { showSubDialog = false },
                onConfirm = { inP, outP ->
                    MatchEngine.performSubstitution(interactiveState, team, outP.id, inP.id)
                    log.add(0, "Min ${interactiveState.minute}: SUB ${inP.name} IN, ${outP.name} OUT")
                    showSubDialog = false
                }
            )
        }
    }
}

@Composable
fun SubstitutionDialog(
    state: MatchEngine.InteractiveMatchState,
    onDismiss: () -> Unit,
    onConfirm: (Player, Player) -> Unit
) {
    var selectedIn by remember { mutableStateOf<Player?>(null) }
    var selectedOut by remember { mutableStateOf<Player?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("SUBSTITUTION", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.weight(1f)) {
                    // Bench (IN)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("BENCH (IN)", color = Color.Green, fontWeight = FontWeight.Bold)
                        LazyColumn {
                            items(state.homeBench) { p ->
                                Text(
                                    "${p.position} ${p.name}",
                                    color = if (selectedIn == p) Color.Green else Color.White,
                                    modifier = Modifier
                                        .clickable { selectedIn = p }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Lineup (OUT)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PITCH (OUT)", color = Color.Red, fontWeight = FontWeight.Bold)
                        LazyColumn {
                            items(state.homeLineup) { p ->
                                Text(
                                    "${p.position} ${p.name} (${state.playerStamina[p.id]?.toInt()}%)",
                                    color = if (selectedOut == p) Color.Red else Color.White,
                                    modifier = Modifier
                                        .clickable { selectedOut = p }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (selectedIn != null && selectedOut != null) onConfirm(selectedIn!!, selectedOut!!) },
                        enabled = selectedIn != null && selectedOut != null
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, homeVal: String, awayVal: String) {
    Row(
        modifier = Modifier.width(300.dp).padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(homeVal, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, color = Color.Gray)
        Text(awayVal, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Scoreboard(state: MatchEngine.InteractiveMatchState, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        modifier = modifier.height(60.dp)
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
    val isHomePossession = state.homePossessionTicks > state.awayPossessionTicks // Rough approximation

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

        val lineColor = Color.White.copy(alpha = 0.5f)
        drawLine(lineColor, Offset(w/2, 0f), Offset(w/2, h))
        drawCircle(lineColor, radius = h*0.15f, center = Offset(w/2, h/2), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
        drawRect(lineColor, topLeft = Offset(0f, h*0.3f), size = androidx.compose.ui.geometry.Size(w*0.1f, h*0.4f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
        drawRect(lineColor, topLeft = Offset(w*0.9f, h*0.3f), size = androidx.compose.ui.geometry.Size(w*0.1f, h*0.4f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))

        val random = Random(state.minute)

        repeat(10) {
            val x = random.nextFloat() * w * 0.8f
            val y = random.nextFloat() * h
            drawCircle(Color.Red, radius = 8f, center = Offset(x, y))
        }

        repeat(10) {
            val x = w - (random.nextFloat() * w * 0.8f)
            val y = random.nextFloat() * h
            drawCircle(Color.Blue, radius = 8f, center = Offset(x, y))
        }

        val ballX = if (isHomePossession) w * 0.7f + pulse else w * 0.3f - pulse
        val ballY = h * 0.5f + (random.nextFloat() - 0.5f) * h * 0.5f
        drawCircle(Color.Yellow, radius = 6f, center = Offset(ballX, ballY))
    }
}
