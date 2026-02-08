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
import com.championstar.soccer.domain.models.Currency
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.ShopItem
import com.championstar.soccer.ui.components.GloryCurrency
import com.championstar.soccer.ui.components.StarCurrency

@Composable
fun ShopScreen(
    player: Player,
    items: List<ShopItem>,
    onBuy: (String) -> Unit
) {
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
                text = "STORE",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
            Row {
                StarCurrency(player.stars)
                GloryCurrency(player.glory)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ITEMS GRID ---
        Text("Consumables & Boosts", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                val cardColor = if (item.currency == Currency.GLORY) Color(0xFF3E2723) else Color(0xFF263238)
                val buttonColor = if (item.currency == Currency.GLORY) Color(0xFFFFD700) else Color(0xFF00E676)

                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, color = if (item.currency == Currency.GLORY) Color(0xFFFFD700) else Color.White, style = MaterialTheme.typography.titleMedium)
                            Text(item.description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = { onBuy(item.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                        ) {
                            Text(
                                text = "${item.cost}",
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
