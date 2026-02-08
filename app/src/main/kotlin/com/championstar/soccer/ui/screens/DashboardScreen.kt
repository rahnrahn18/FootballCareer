package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.EventChoice
import com.championstar.soccer.simulation.engine.GameTurnEvent
import com.championstar.soccer.ui.components.GloryCurrency
import com.championstar.soccer.ui.components.PitchBackground
import com.championstar.soccer.ui.components.PlayerAvatar
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
    onSaveAndExit: () -> Unit
) {
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    PitchBackground {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- LEFT PANEL: Player Profile (30%) ---
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            ) {
                PlayerProfileCard(player)
            }

            // --- RIGHT PANEL: Hub (70%) ---
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                // Top Bar: Resources & Date
                DashboardTopBar(player, currentDate)

                Spacer(modifier = Modifier.height(16.dp))

                // Center: Main Action Area (Event or Navigation)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (currentEvent != null) {
                        EventCard(
                            event = currentEvent,
                            onEventDismissed = onEventCompleted,
                            onChoiceMade = { msg -> feedbackMessage = msg },
                            player = player
                        )
                    } else {
                        // Default Hub View: Continue Button + Navigation
                        Column(modifier = Modifier.fillMaxSize()) {
                            // "Next Day" / Continue Button
                            Button(
                                onClick = onAdvanceTime,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "CONTINUE / NEXT DAY",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Navigation Grid
                            NavigationGrid(
                                onNavigateToLeague = onNavigateToLeague,
                                onNavigateToShop = onNavigateToShop,
                                onSaveAndExit = onSaveAndExit
                            )
                        }
                    }
                }
            }
        }

        // Feedback Dialog
        if (feedbackMessage != null) {
            AlertDialog(
                onDismissRequest = {
                    feedbackMessage = null
                    onEventCompleted()
                },
                title = { Text("Result") },
                text = { Text(feedbackMessage!!) },
                confirmButton = {
                    Button(onClick = {
                        feedbackMessage = null
                        onEventCompleted()
                    }) { Text("OK") }
                },
                containerColor = Color(0xFF263238),
                titleContentColor = Color(0xFFFFD700),
                textContentColor = Color.White
            )
        }
    }
}

@Composable
fun PlayerProfileCard(player: Player) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
            ) {
                PlayerAvatar(modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info
            Text(
                text = player.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${player.position} | Age ${player.age}",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Key Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBadge("OVR", String.format("%.0f", player.overallRating), Color(0xFFFFD700))
                StatBadge("FORM", String.format("%.0f", player.form), if(player.form > 70) Color.Green else Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stamina Bar
            Text("Stamina", color = Color.Gray, fontSize = 12.sp)
            LinearProgressIndicator(
                progress = { (player.stamina / 100f).toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if(player.stamina < 30) Color.Red else Color.Cyan,
                trackColor = Color.DarkGray,
            )
        }
    }
}

@Composable
fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun DashboardTopBar(player: Player, currentDate: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(currentDate, color = Color.White, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                StarCurrency(player.stars)
                Spacer(modifier = Modifier.width(16.dp))
                GloryCurrency(player.glory)
            }
        }
    }
}

@Composable
fun NavigationGrid(
    onNavigateToLeague: () -> Unit,
    onNavigateToShop: () -> Unit,
    onSaveAndExit: () -> Unit
) {
    val items = listOf(
        NavItem("League", Icons.Filled.EmojiEvents, Color(0xFF1565C0), onNavigateToLeague),
        NavItem("Shop", Icons.Filled.ShoppingCart, Color(0xFF2E7D32), onNavigateToShop),
        NavItem("Squad", Icons.Filled.Person, Color(0xFF6A1B9A), {}), // Placeholder
        NavItem("Transfers", Icons.Filled.CompareArrows, Color(0xFFEF6C00), {}), // Placeholder
        NavItem("Exit", Icons.Filled.ExitToApp, Color(0xFFC62828), onSaveAndExit)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 columns
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            NavCard(item)
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)

@Composable
fun NavCard(item: NavItem) {
    Card(
        onClick = item.onClick,
        colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.9f)),
        modifier = Modifier
            .height(100.dp) // Fixed height for grid
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.label, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EventCard(
    event: GameTurnEvent,
    onEventDismissed: () -> Unit, // For Routine/Match events
    onChoiceMade: (String) -> Unit, // For Story events (returns consequence msg)
    player: Player // Needed for choice consequences execution context
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (event) {
                is GameTurnEvent.StoryEvent -> {
                    Text("EVENT: ${event.title}", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(event.description, style = MaterialTheme.typography.bodyLarge, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(32.dp))

                    event.choices.forEach { choice ->
                        Button(
                            onClick = {
                                val resultMsg = choice.consequence(player)
                                onChoiceMade(resultMsg)
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(choice.text, color = Color.White)
                        }
                    }
                }
                is GameTurnEvent.MatchEvent -> {
                    Text("MATCH RESULT", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Vs ${event.opponentName}", style = MaterialTheme.typography.headlineSmall, color = Color.LightGray)
                    Text(event.resultText, style = MaterialTheme.typography.displayMedium, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Rating: ${event.rating} | Goals: ${event.goals}", color = Color.Cyan)

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onEventDismissed) { Text("Continue") }
                }
                is GameTurnEvent.RoutineEvent -> {
                    Text("WEEKLY REPORT", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(event.weekSummary, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onEventDismissed) { Text("Continue") }
                }
                is GameTurnEvent.SeasonEndEvent -> {
                    Text("SEASON ENDED", style = MaterialTheme.typography.headlineLarge, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("You are now age ${event.age}.", color = Color.White)
                    if (event.isRetired) {
                         Text("You have RETIRED from professional football.", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onEventDismissed) { Text("Start Next Season") }
                }
            }
        }
    }
}
