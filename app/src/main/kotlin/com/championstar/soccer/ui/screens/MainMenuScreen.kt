package com.championstar.soccer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.championstar.soccer.ui.components.PitchBackground

@Composable
fun MainMenuScreen(
    hasSaveGame: Boolean,
    onNewGame: () -> Unit,
    onLoadGame: () -> Unit
) {
    PitchBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CHAMPIONSTAR",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
            Text(
                text = "SOCCER SIMULATOR",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // NEW GAME
            Button(
                onClick = onNewGame,
                modifier = Modifier.fillMaxWidth(0.6f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("NEW GAME", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LOAD GAME
            Button(
                onClick = onLoadGame,
                enabled = hasSaveGame,
                modifier = Modifier.fillMaxWidth(0.6f).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    disabledContainerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Text("LOAD GAME", color = if (hasSaveGame) Color.White else Color.Gray)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("v1.0.0 Alpha", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}
