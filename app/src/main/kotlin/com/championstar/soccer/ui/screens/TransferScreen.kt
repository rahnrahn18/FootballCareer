package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TransferScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Text("TRANSFER MARKET", style = MaterialTheme.typography.displaySmall, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Coming Soon: Interactive Transfer System", color = Color.Gray)
        // Placeholder for future implementation
    }
}
