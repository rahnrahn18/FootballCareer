package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.championstar.soccer.data.entities.BusinessEntity
import com.championstar.soccer.data.entities.PlayerEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    player: PlayerEntity,
    businesses: List<BusinessEntity>,
    onBuy: (BusinessEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Empire") },
                actions = {
                    Text("$${player.money}", modifier = Modifier.padding(end = 16.dp))
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(businesses) { business ->
                BusinessItem(business, player.money, onBuy)
            }
        }
    }
}

@Composable
fun BusinessItem(
    business: BusinessEntity,
    playerMoney: Long,
    onBuy: (BusinessEntity) -> Unit
) {
    val cost = business.baseCost * (business.level + 1)
    val income = business.baseIncome * (business.level + 1)
    val canAfford = playerMoney >= cost

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (business.level > 0) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(business.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(business.type, style = MaterialTheme.typography.labelSmall)
                }
                if (business.level > 0) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text("Level ${business.level}", color = Color.White, modifier = Modifier.padding(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Income: $${income}/week")
                Text("Cost: $${cost}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onBuy(business) },
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (business.level > 0) "Upgrade" else "Buy")
            }
        }
    }
}