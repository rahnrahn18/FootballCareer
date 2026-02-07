package com.championstar.soccer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onNewGame: () -> Unit,
    onLoadGame: () -> Unit,
    hasSave: Boolean
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FOOTBALL CAREER", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            Text("SIMULATOR", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNewGame,
                modifier = Modifier.width(200.dp).height(56.dp)
            ) {
                Text("NEW CAREER")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLoadGame,
                enabled = hasSave,
                modifier = Modifier.width(200.dp).height(56.dp)
            ) {
                Text("CONTINUE")
            }
        }
    }
}