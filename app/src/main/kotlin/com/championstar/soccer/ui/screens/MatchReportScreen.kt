package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.EventType
import com.championstar.soccer.domain.models.MatchResult
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team

@Composable
fun MatchReportScreen(
    result: MatchResult,
    userPlayerId: String,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Dark Blue/Slate background
            .padding(16.dp)
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        // --- SCOREBOARD ---
        Scoreboard(result)

        Spacer(modifier = Modifier.height(16.dp))

        // --- MAIN CONTENT (Split View) ---
        Row(modifier = Modifier.fillMaxSize()) {
            // LEFT: Squads / Match Stats
            Column(modifier = Modifier.weight(0.6f)) {
                Text("Match Ratings", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    // Home Team Ratings
                    Column(modifier = Modifier.weight(1f)) {
                        Text(result.homeTeam.name, color = Color.LightGray, fontSize = 12.sp)
                        HorizontalDivider(color = Color.Gray)
                        SquadList(result.homeTeam.players, result.playerRatings, result.manOfTheMatchId)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Away Team Ratings
                    Column(modifier = Modifier.weight(1f)) {
                        Text(result.awayTeam.name, color = Color.LightGray, fontSize = 12.sp)
                        HorizontalDivider(color = Color.Gray)
                        SquadList(result.awayTeam.players, result.playerRatings, result.manOfTheMatchId)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // RIGHT: User Performance & Reputation
            Column(modifier = Modifier.weight(0.4f)) {
                UserPerformancePanel(result, userPlayerId)
                Spacer(modifier = Modifier.height(16.dp))
                MatchReputationPanel(result)
            }
        }
    }
}

@Composable
fun Scoreboard(result: MatchResult) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(result.homeTeam.name, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("${result.homeScore} - ${result.awayScore}", color = Color(0xFFFFD700), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 16.dp))
                Text(result.awayTeam.name, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }

            // Scorers
            val goalEvents = result.events.filter { it.type == EventType.GOAL }
            if (goalEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                goalEvents.forEach { event ->
                    Text(
                        text = "${event.description} (${event.minute}')",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SquadList(players: List<Player>, ratings: Map<String, Double>, motmId: String?) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(players.sortedByDescending { ratings[it.id] ?: 0.0 }) { player ->
            val rating = ratings[player.id] ?: 6.0
            val isMotm = player.id == motmId

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.8f)) {
                    Text(
                        text = "${player.position} ${player.name}",
                        color = Color.White,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    if (isMotm) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Star, contentDescription = "MOTM", tint = Color.Yellow, modifier = Modifier.size(12.dp))
                    }
                }
                Text(
                    text = String.format("%.1f", rating),
                    color = if (rating >= 8.0) Color.Green else if (rating < 6.0) Color.Red else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun UserPerformancePanel(result: MatchResult, userId: String) {
    val rating = result.playerRatings[userId] ?: 6.0
    // Mock bonuses for now
    val posBonus = if (rating > 7.0) 1.0 else 0.0
    val total = rating + posBonus

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Your Performance", color = Color.White, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Match Rating", color = Color.Gray, fontSize = 12.sp)
                Text(String.format("%.1f", rating), color = Color.White, fontSize = 12.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Possession Bonus", color = Color.Gray, fontSize = 12.sp)
                Text(String.format("%.1f", posBonus), color = Color.White, fontSize = 12.sp)
            }
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Performance Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(String.format("%.1f", total), color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun MatchReputationPanel(result: MatchResult) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Match Reputation", color = Color.White, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))

            // Mock Rep logic
            val leagueRep = 0.05
            val clubRep = 0.06
            val status = 1.0
            val multiplier = 1.11

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("League Reputation", color = Color.Gray, fontSize = 12.sp)
                Text(String.format("%.2f", leagueRep), color = Color.White, fontSize = 12.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Club Reputation", color = Color.Gray, fontSize = 12.sp)
                Text(String.format("%.2f", clubRep), color = Color.White, fontSize = 12.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Match Status", color = Color.Gray, fontSize = 12.sp)
                Text(String.format("%.1f", status), color = Color.White, fontSize = 12.sp)
            }
            HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
             Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Rep Multiplier", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(String.format("%.2f", multiplier), color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
