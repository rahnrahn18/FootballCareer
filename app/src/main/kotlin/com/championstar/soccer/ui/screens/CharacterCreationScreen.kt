package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.ui.components.PlayerAssetImage
import java.util.UUID

@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (Player) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("New Star") }
    var selectedPosition by remember { mutableStateOf("FW") }
    // var avatarSeed by remember { mutableStateOf(0) } // Can randomize asset seed

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        // --- LEFT PANEL: Input Form (60%) ---
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CREATE YOUR LEGEND",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Player Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Position Selector
            Text("Position", color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("GK", "DF", "MF", "FW").forEach { pos ->
                    val isSelected = selectedPosition == pos
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPosition = pos },
                        label = { Text(pos) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFD700),
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Actions
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                    Text("CANCEL")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val newPlayer = Player(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            age = 17,
                            position = selectedPosition,
                            overallRating = 60.0, // Start low
                            potential = 85.0 + Math.random() * 10.0 // Random potential
                        )
                        onCharacterCreated(newPlayer)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("START CAREER")
                }
            }
        }

        Spacer(modifier = Modifier.width(32.dp))

        // --- RIGHT PANEL: Avatar Preview (40%) ---
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.size(200.dp)
            ) {
                 PlayerAssetImage(modifier = Modifier.fillMaxSize()) // Using Coil Asset
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Avatar", color = Color.Gray)
        }
    }
}
