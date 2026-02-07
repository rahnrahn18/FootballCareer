package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.championstar.soccer.data.entities.PlayerEntity

data class MenuItem(val title: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    player: PlayerEntity,
    onNavigate: (String) -> Unit,
    onAdvanceWeek: () -> Unit
) {
    val menuItems = listOf(
        MenuItem("Play Match", Icons.Default.SportsSoccer, "match"),
        MenuItem("Training", Icons.Default.FitnessCenter, "training"),
        MenuItem("Business", Icons.Default.Business, "business"),
        MenuItem("Transfer", Icons.Default.SwapHoriz, "transfer"),
        MenuItem("Lifestyle", Icons.Default.ShoppingBag, "lifestyle"),
        MenuItem("Stats", Icons.Default.BarChart, "stats"),
        MenuItem("History", Icons.Default.History, "history"),
        MenuItem("Settings", Icons.Default.Settings, "settings")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(player.name, style = MaterialTheme.typography.titleMedium)
                        Text("Week ${player.week}, Year ${player.year}", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    Text("$${player.money}", modifier = Modifier.padding(end = 16.dp))
                    IconButton(onClick = onAdvanceWeek) {
                        Icon(Icons.Default.NextWeek, contentDescription = "Next Week")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Team: ${player.teamName}")
                        Text("Position: ${player.position}")
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("OVR: ${player.overallRating}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Morale: ${player.morale}%")
                    }
                }
            }

            // Menu Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    MenuCard(item, onNavigate)
                }
            }
        }
    }
}

@Composable
fun MenuCard(item: MenuItem, onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onNavigate(item.route) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(item.icon, contentDescription = item.title, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.title, style = MaterialTheme.typography.labelLarge)
        }
    }
}