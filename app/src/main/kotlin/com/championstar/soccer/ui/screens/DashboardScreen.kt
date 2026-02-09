package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.core.Localization
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.GameTurnEvent
import com.championstar.soccer.simulation.engine.LeagueEngine
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
    // Local Event History (Should persist in GameState ideally)
    val eventHistory = remember { mutableStateListOf<String>("Season started. Good luck!") }

    // News Ticker
    var newsHeadlines by remember { mutableStateOf(listOf<String>()) }

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

            // Navigation Icons
            NavigationIconRow(
                onLeague = onNavigateToLeague,
                onShop = onNavigateToShop,
                onBusiness = onNavigateToBusiness,
                onSquad = onNavigateToTraining,
                onTransfer = { /* TODO */ },
                onExit = onSaveAndExit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- MAIN CONTENT ---
        Row(modifier = Modifier.fillMaxSize()) {

            // LEFT PANEL: Player & Status (30%)
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
                StatRow("Rating", String.format("%.1f", player.overallRating), Color(0xFFFFD700))
                Spacer(modifier = Modifier.height(8.dp))
                StatRow("Form", String.format("%.0f", player.form), if(player.form > 80) Color.Green else Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                StatRow("Stamina", "${player.stamina.toInt()}%", if(player.stamina < 30) Color.Red else Color.Cyan)
                Spacer(modifier = Modifier.height(8.dp))
                StatRow("Morale", "${player.morale.toInt()}%", Color.Magenta)

                Spacer(modifier = Modifier.weight(1f))

                Divider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))

                // Current Club Info
                Text("Current Club", color = Color.Gray, fontSize = 12.sp)
                // In real app, pass Club Name
                Text("My Club", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // CENTER/RIGHT PANEL: Interaction & Info (70%)
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                // 1. News Ticker (New Feature)
                if (newsHeadlines.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                        modifier = Modifier.fillMaxWidth().height(40.dp).padding(bottom = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                            Text("BREAKING NEWS:", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                newsHeadlines.first(),
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 2. Main Interaction Area
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
                                if (resultMsg != null) eventHistory.add(0, "$currentDate: $resultMsg")
                                onEventCompleted()
                            }
                        )
                    } else {
                        // EVENT HISTORY / DASHBOARD VIEW
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

                // 3. Action Button (Advance)
                Button(
                    onClick = {
                        // Clear old news before advancing
                        newsHeadlines = emptyList()
                        onAdvanceTime()
                        // Note: In real implementation, news would be populated AFTER advanceTime returns via a state update or effect
                    },
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
                            "ADVANCE DAY", // Changed from "Week" to "Day"
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
                // Match Day - Navigate to Match Screen
                Text("MATCH DAY", style = MaterialTheme.typography.displayMedium, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("vs ${event.opponentName}", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFFFD700))
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { onDismiss("Playing Match vs ${event.opponentName}...") }) {
                    Text("PLAY MATCH", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            is GameTurnEvent.RoutineEvent -> {
                Text(event.message, style = MaterialTheme.typography.bodyLarge, color = Color.LightGray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onDismiss(event.message) }) { Text("Continue") }
            }
            is GameTurnEvent.SeasonEndEvent -> {
                Text("SEASON COMPLETED", style = MaterialTheme.typography.headlineMedium, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Age: ${event.age}", color = Color.White)
                if(event.isRetired) Text("RETIRED", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onDismiss("Season Ended") }) { Text("Next Season") }
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
