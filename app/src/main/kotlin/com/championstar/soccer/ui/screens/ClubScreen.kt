package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team
import com.championstar.soccer.ui.components.ClubAssetImage

@Composable
fun ClubScreen(
    team: Team?
) {
    if (team == null) {
        Text("No Club", color = Color.Red)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(team.name, style = MaterialTheme.typography.displaySmall, color = Color.White)
                Text("Reputation: ${team.reputation}", color = Color.Gray)
            }
            Box(modifier = Modifier.size(64.dp)) {
                 ClubAssetImage()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Squad", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFD700))

        LazyColumn {
            items(team.players) { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(player.name, color = Color.White)
                    Text("${player.position} | OVR: ${String.format("%.0f", player.overallRating)}", color = Color.Cyan)
                }
                Divider(color = Color.DarkGray)
            }
        }
    }
}
