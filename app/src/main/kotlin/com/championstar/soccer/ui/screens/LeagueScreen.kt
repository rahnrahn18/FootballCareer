package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Team

@Composable
fun LeagueScreen(
    leagues: List<League>,
    currentLeagueId: String? // If null, show all
) {
    // Basic fallback logic
    val leagueList = if (leagues.isNotEmpty()) leagues else emptyList()
    val currentLeague = leagueList.find { it.id == currentLeagueId } ?: leagueList.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        if (currentLeague == null) {
            Text("No Leagues Found", color = Color.Red)
            return@Column
        }

        // --- HEADER ---
        Text(
            text = currentLeague.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Tier ${currentLeague.tier} | Matchday ${currentLeague.currentMatchday}",
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- TABLE HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF333333))
                .padding(8.dp)
        ) {
            Text("Club", color = Color.White, modifier = Modifier.weight(0.5f))
            Text("P", color = Color.Gray, modifier = Modifier.weight(0.1f))
            Text("Pts", color = Color.Yellow, modifier = Modifier.weight(0.15f))
        }

        // --- TABLE ROWS (Simulated Stats for now) ---
        LazyColumn {
            items(currentLeague.teams.sortedByDescending { it.reputation }) { team ->
                // Simulate points roughly based on reputation for demo
                // In a real implementation, we'd read `team.points` from a league table engine
                val points = (team.reputation * 0.8).toInt()
                val played = currentLeague.currentMatchday

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFF1E1E1E))
                        .padding(8.dp)
                ) {
                    Text(team.name, color = Color.White, modifier = Modifier.weight(0.5f))
                    Text("$played", color = Color.Gray, modifier = Modifier.weight(0.1f))
                    Text("$points", color = Color.Yellow, modifier = Modifier.weight(0.15f))
                }
            }
        }
    }
}
