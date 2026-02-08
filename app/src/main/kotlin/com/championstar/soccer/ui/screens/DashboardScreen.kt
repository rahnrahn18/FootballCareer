package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
    onSaveAndExit: () -> Unit
) {
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Use a solid color or very subtle gradient for minimalism
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp) // Minimal padding
        ) {
            // --- LEFT PANEL: Player Compact Profile (25%) ---
            Column(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()
                    .padding(end = 8.dp)
            ) {
                CompactPlayerProfile(player)
            }

            // --- RIGHT PANEL: Action Hub (75%) ---
            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
            ) {
                // Top Bar: Dense Resources & Date
                CompactTopBar(player, currentDate)

                Spacer(modifier = Modifier.height(8.dp))

                // Center: Split View (Event Box on Left, Grid on Right)
                Row(modifier = Modifier.weight(1f)) {
                    // Event/News Box (40%)
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .padding(end = 8.dp)
                    ) {
                        if (currentEvent != null) {
                            EventCard(
                                event = currentEvent,
                                onEventDismissed = onEventCompleted,
                                onChoiceMade = { msg -> feedbackMessage = msg },
                                player = player
                            )
                        } else {
                            DailyNewsBox(onAdvanceTime)
                        }
                    }

                    // Navigation Grid (60%)
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                    ) {
                        CompactNavigationGrid(
                            onNavigateToLeague,
                            onNavigateToShop,
                            onNavigateToBusiness,
                            onSaveAndExit
                        )
                    }
                }
            }
        }

        // Feedback Dialog (Minimal)
        if (feedbackMessage != null) {
            AlertDialog(
                onDismissRequest = {
                    feedbackMessage = null
                    onEventCompleted()
                },
                title = { Text("Result", fontSize = 16.sp) },
                text = { Text(feedbackMessage!!, fontSize = 14.sp) },
                confirmButton = {
                    TextButton(onClick = {
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
fun CompactPlayerProfile(player: Player) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar (Smaller, Circle)
            Box(
                modifier = Modifier
                    .size(80.dp) // Reduced size
                    .background(Color.Gray, CircleShape)
            ) {
                 PlayerAssetImage(modifier = Modifier.fillMaxSize()) // Using Coil
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Info (Dense)
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "${player.position} | ${player.age}yo",
                color = Color.LightGray,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CompactStat("OVR", String.format("%.0f", player.overallRating), Color(0xFFFFD700))
                CompactStat("FORM", String.format("%.0f", player.form), if(player.form > 70) Color.Green else Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stamina (Thin)
            LinearProgressIndicator(
                progress = { (player.stamina / 100f).toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = if(player.stamina < 30) Color.Red else Color.Cyan,
                trackColor = Color.DarkGray,
            )
        }
    }
}

@Composable
fun CompactStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun CompactTopBar(player: Player, currentDate: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth().height(40.dp) // Fixed height
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(currentDate, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                StarCurrency(player.stars)
                Spacer(modifier = Modifier.width(8.dp))
                GloryCurrency(player.glory)
            }
        }
    }
}

@Composable
fun CompactNavigationGrid(
    onNavigateToLeague: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToBusiness: () -> Unit,
    onSaveAndExit: () -> Unit
) {
    val items = listOf(
        NavItem("League", Icons.Filled.EmojiEvents, Color(0xFF1565C0), onNavigateToLeague),
        NavItem("Shop", Icons.Filled.ShoppingCart, Color(0xFF2E7D32), onNavigateToShop),
        NavItem("Squad", Icons.Filled.Person, Color(0xFF6A1B9A), {}),
        NavItem("Biz", Icons.Filled.Business, Color(0xFF00ACC1), onNavigateToBusiness),
        NavItem("Transfer", Icons.Filled.CompareArrows, Color(0xFFEF6C00), {}),
        NavItem("Exit", Icons.Filled.ExitToApp, Color(0xFFC62828), onSaveAndExit)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 columns
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            CompactNavCard(item)
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)

@Composable
fun CompactNavCard(item: NavItem) {
    Card(
        onClick = item.onClick,
        colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.aspectRatio(1.5f) // Fixed aspect ratio
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Text(item.label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
fun DailyNewsBox(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B4E)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "NEXT DAY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Tap to advance",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
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
