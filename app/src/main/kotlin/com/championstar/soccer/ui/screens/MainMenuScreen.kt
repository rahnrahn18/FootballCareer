package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.championstar.soccer.ui.components.ClubAssetImage

@Composable
fun MainMenuScreen(
    hasSaveGame: Boolean,
    onNewGame: () -> Unit,
    onLoadGame: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- LEFT PANEL: Branding (50%) ---
        Column(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClubAssetImage(modifier = Modifier.size(160.dp)) // Game Logo Placeholder
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "FOOTBALL",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "CAREER SIMULATOR",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // --- RIGHT PANEL: Actions (50%) ---
        Column(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton("NEW CAREER", Color(0xFF4CAF50), onNewGame)
            Spacer(modifier = Modifier.height(16.dp))
            if (hasSaveGame) {
                MenuButton("CONTINUE CAREER", Color(0xFF00ACC1), onLoadGame)
            } else {
                MenuButton("CONTINUE CAREER (Locked)", Color.Gray, {}, enabled = false)
            }
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton("SETTINGS", Color.Gray, {}) // Placeholder
        }
    }
}

@Composable
fun MenuButton(text: String, color: Color, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .width(280.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
    }
}
