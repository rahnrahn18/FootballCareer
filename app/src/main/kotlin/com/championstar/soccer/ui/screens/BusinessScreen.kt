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
import com.championstar.soccer.data.static.Business
import com.championstar.soccer.data.static.BusinessDatabase
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.ui.components.StarCurrency

@Composable
fun BusinessScreen(
    player: Player,
    onBuy: (String) -> Unit
) {
    val businesses = BusinessDatabase.businesses

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BUSINESS VENTURES",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
            StarCurrency(player.stars)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LIST ---
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(businesses) { business ->
                BusinessCard(
                    business = business,
                    onBuy = { onBuy(business.id) }
                )
            }
        }
    }
}

@Composable
fun BusinessCard(
    business: Business,
    onBuy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(business.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(business.description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                     Text("Income: $${business.weeklyIncome}/wk", color = Color.Green, style = MaterialTheme.typography.labelMedium)
                     Spacer(modifier = Modifier.width(16.dp))
                     Text("Risk: ${(business.riskFactor * 100).toInt()}%", color = Color(0xFFFF5722), style = MaterialTheme.typography.labelMedium)
                }
            }

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
            ) {
                Text("Buy $${business.cost}")
            }
        }
    }
}
