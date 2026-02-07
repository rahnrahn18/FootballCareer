package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.championstar.soccer.data.entities.PlayerEntity
import com.championstar.soccer.game.engine.MatchResult
import kotlinx.coroutines.delay

@Composable
fun MatchScreen(
    player: PlayerEntity,
    matchResult: MatchResult?,
    onSimulate: () -> Unit,
    onFinish: () -> Unit
) {
    var visibleCommentary by remember { mutableStateOf<List<String>>(emptyList()) }
    var isSimulating by remember { mutableStateOf(false) }

    LaunchedEffect(matchResult) {
        if (matchResult != null) {
            isSimulating = true
            visibleCommentary = emptyList()
            matchResult.commentary.forEach { line ->
                delay(500) // Simulate time passing
                visibleCommentary = visibleCommentary + line
            }
            isSimulating = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Scoreboard
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(player.teamName, fontWeight = FontWeight.Bold)
                    Text(matchResult?.scorePlayerTeam?.toString() ?: "0", style = MaterialTheme.typography.headlineLarge)
                }
                Text("VS", style = MaterialTheme.typography.titleLarge)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Opponent", fontWeight = FontWeight.Bold)
                    Text(matchResult?.scoreOpponent?.toString() ?: "0", style = MaterialTheme.typography.headlineLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Commentary Log
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.1f))
        ) {
            items(visibleCommentary) { line ->
                Text(
                    text = line,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (matchResult == null) {
            Button(onClick = onSimulate, modifier = Modifier.fillMaxWidth()) {
                Text("Start Match")
            }
        } else if (!isSimulating) {
             Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Text("Continue")
            }
        } else {
             Text("Match in Progress...", style = MaterialTheme.typography.labelLarge)
        }
    }
}