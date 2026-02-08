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
import com.championstar.soccer.data.static.AchievementDatabase
import com.championstar.soccer.domain.models.Player

@Composable
fun AchievementScreen(player: Player) {
    val allAchievements = AchievementDatabase.achievements

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Text("TROPHY ROOM", style = MaterialTheme.typography.displaySmall, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(allAchievements) { achievement ->
                val isUnlocked = player.unlockedAchievements.contains(achievement.id)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) Color(0xFF2E7D32) else Color(0xFF424242)
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            achievement.title,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color.White else Color.Gray
                        )
                        Text(
                            achievement.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}
