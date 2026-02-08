package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.GameTurnEvent
import com.championstar.soccer.ui.components.GloryCurrency
import com.championstar.soccer.ui.components.PlayerAssetImage
import com.championstar.soccer.ui.components.StarCurrency

@Composable
fun DashboardScreen(
    player: Player,
    currentDate: String,
    currentEvent: GameTurnEvent?,
    onAdvanceTime: () -> Unit,
    onEventCompleted: () -> Unit,
    onNavigateToLeague: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToBusiness: () -> Unit,
    onNavigateToTraining: () -> Unit,
    onSaveAndExit: () -> Unit
) {
    // Local Event History (In a real app, this should be in ViewModel/Model)
    // We add to this when an event is dismissed
    val eventHistory = remember { mutableStateListOf<String>("Welcome to your career! Season started.") }

    // When an event is completed, we might want to log it.
    // However, onEventCompleted is a callback. We can intercept it in the UI logic below.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date
            Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(currentDate, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.weight(1f))

            // Currency
            StarCurrency(player.stars)
            Spacer(modifier = Modifier.width(16.dp))
            GloryCurrency(player.glory)

            Spacer(modifier = Modifier.width(32.dp))

            // Navigation Icons (Top Right, Tight & Small)
            NavigationIconRow(
                onLeague = onNavigateToLeague,
                onShop = onNavigateToShop,
                onBusiness = onNavigateToBusiness,
                onSquad = onNavigateToTraining, // Replaced Squad with Training
                onTransfer = { /* TODO */ },
                onExit = onSaveAndExit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- MAIN CONTENT ---
        Row(modifier = Modifier.fillMaxSize()) {
            // LEFT: Player Profile (30%)
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFFFD700), CircleShape)
                ) {
                     PlayerAssetImage(modifier = Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(player.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("${player.position} | ${player.age} Years Old", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                // Stats
                StatRow("Overall Rating", String.format("%.1f", player.overallRating), Color(0xFFFFD700))
                Spacer(modifier = Modifier.height(8.dp))
                StatRow("Form", String.format("%.0f", player.form), if(player.form > 80) Color.Green else Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                StatRow("Stamina", "${player.stamina.toInt()}%", if(player.stamina < 30) Color.Red else Color.Cyan)

                Spacer(modifier = Modifier.weight(1f))

                // Team Logo / Name placeholder
                Text("Current Club", color = Color.Gray, fontSize = 12.sp)
                Text("Free Agent", color = Color.White, fontWeight = FontWeight.Bold) // Ideally fetch team name
            }

            Spacer(modifier = Modifier.width(16.dp))

            // RIGHT: Event Log & Actions (70%)
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                // Event Box (History or Active Event)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    if (currentEvent != null) {
                        // ACTIVE EVENT OVERLAY
                        EventInteractionView(
                            event = currentEvent,
                            player = player,
                                onDismiss = { resultMsg ->
                                // Add summary to history
                                    val summary = resultMsg ?: when(currentEvent) {
                                    is GameTurnEvent.MatchEvent -> "Match vs ${currentEvent.opponentName}: ${currentEvent.resultText}"
                                    is GameTurnEvent.RoutineEvent -> currentEvent.weekSummary
                                    is GameTurnEvent.StoryEvent -> "Decision made."
                                        is GameTurnEvent.SeasonEndEvent -> "Season Ended."
                                }
                                eventHistory.add(0, "${currentDate}: $summary")
                                onEventCompleted()
                            }
                        )
                    } else {
                        // EVENT HISTORY LIST
                        Column {
                            Text(
                                "ACTIVITY LOG",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 8.dp)
                            ) {
                                items(eventHistory) { log ->
                                    Text(
                                        text = log,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    Divider(color = Color.DarkGray, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Advance Button
                Button(
                    onClick = onAdvanceTime,
                    enabled = currentEvent == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color.DarkGray
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.FastForward, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "ADVANCE TO NEXT WEEK",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationIconRow(
    onLeague: () -> Unit,
    onShop: () -> Unit,
    onBusiness: () -> Unit,
    onSquad: () -> Unit,
    onTransfer: () -> Unit,
    onExit: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        NavIconButton(Icons.Filled.EmojiEvents, "League", onLeague)
        NavIconButton(Icons.Filled.Groups, "Squad", onSquad)
        NavIconButton(Icons.Filled.CompareArrows, "Transfer", onTransfer)
        NavIconButton(Icons.Filled.Business, "Biz", onBusiness)
        NavIconButton(Icons.Filled.ShoppingCart, "Shop", onShop)
        NavIconButton(Icons.Filled.ExitToApp, "Exit", onExit, Color(0xFFC62828))
    }
}

@Composable
fun NavIconButton(icon: ImageVector, label: String, onClick: () -> Unit, tint: Color = Color.White) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF2C2C2C)),
        modifier = Modifier.size(40.dp)
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = color, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EventInteractionView(
    event: GameTurnEvent,
    player: Player,
    onDismiss: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (event) {
            is GameTurnEvent.StoryEvent -> {
                Text(event.title, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFFFD700))
                Spacer(modifier = Modifier.height(16.dp))
                Text(event.description, style = MaterialTheme.typography.bodyLarge, color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                event.choices.forEach { choice ->
                    Button(
                        onClick = {
                            val msg = choice.consequence(player)
                            onDismiss(msg)
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(choice.text)
                    }
                }
            }
            is GameTurnEvent.MatchEvent -> {
                Text("MATCH FINISHED", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(event.resultText, style = MaterialTheme.typography.displaySmall, color = Color(0xFFFFD700))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Rating: ${event.rating}  Goals: ${event.goals}", color = Color.Cyan)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onDismiss(null) }) { Text("Continue") }
            }
            is GameTurnEvent.RoutineEvent -> {
                Text("WEEKLY REPORT", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text(event.weekSummary, color = Color.LightGray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onDismiss(null) }) { Text("Continue") }
            }
            is GameTurnEvent.SeasonEndEvent -> {
                Text("SEASON COMPLETED", style = MaterialTheme.typography.headlineMedium, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Age: ${event.age}", color = Color.White)
                if(event.isRetired) Text("RETIRED", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onDismiss(null) }) { Text("Next Season") }
            }
        }
    }
}
