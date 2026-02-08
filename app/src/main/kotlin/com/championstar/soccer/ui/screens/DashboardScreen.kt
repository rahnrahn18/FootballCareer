package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.ui.components.GloryCurrency
import com.championstar.soccer.ui.components.PitchBackground
import com.championstar.soccer.ui.components.PlayerAvatar
import com.championstar.soccer.ui.components.StarCurrency

@Composable
fun DashboardScreen(
    player: Player,
    currentDate: String,
    onSimulate: () -> Unit
) {
    PitchBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Row {
                    StarCurrency(player.stars)
                    GloryCurrency(player.glory)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MAIN CARD (Player) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    // Avatar
                    Box(modifier = Modifier.size(100.dp)) {
                         PlayerAvatar(modifier = Modifier.fillMaxSize())
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Stats
                    Column {
                        Text(text = player.name, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        Text(text = "${player.position} | Age ${player.age}", color = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "OVR: ${String.format("%.1f", player.overallRating)}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Form: ${String.format("%.1f", player.form)}", color = Color.Green)
                        Text(text = "Stamina: ${String.format("%.1f", player.stamina)}%", color = if(player.stamina < 50) Color.Red else Color.Cyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ACTIONS ---
            Button(
                onClick = onSimulate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ADVANCE WEEK (SIMULATE)", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- NEWS FEED ---
            Text("News Feed", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Placeholder news
                    Text("• Transfer window is open!", color = Color.White)
                    Text("• Coach demands better fitness.", color = Color.White)
                    if (player.contract != null) {
                         Text("• Contract active with ${player.contract?.salary}/wk.", color = Color.Cyan)
                    } else {
                         Text("• You are a Free Agent. Find a club!", color = Color.Red)
                    }
                }
            }
        }
    }
}
