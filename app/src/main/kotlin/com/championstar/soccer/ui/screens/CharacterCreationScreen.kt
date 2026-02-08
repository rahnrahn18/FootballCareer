package com.championstar.soccer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.CareerEngine
import com.championstar.soccer.ui.components.EnhancedPlayerAvatar
import com.championstar.soccer.ui.components.HairStyle

@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (Player) -> Unit,
    onBack: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Identity, 2: Appearance

    // --- STATE ---
    var name by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Europe") }
    var selectedPosition by remember { mutableStateOf("ST") }

    // Appearance
    var skinColorIndex by remember { mutableStateOf(0) }
    var hairStyleIndex by remember { mutableStateOf(0) }

    val skinColors = listOf(Color(0xFFFFDAB9), Color(0xFFD2B48C), Color(0xFF8D5524), Color(0xFF3B240B))
    val hairStyles = HairStyle.values()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CREATE YOUR LEGEND", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        // --- STEP 1: IDENTITY ---
        if (step == 1) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Player Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Region (Nationality)")
            // Simple Buttons for Region
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Europe", "Asia", "SouthAmerica").forEach { reg ->
                    Button(
                        onClick = { selectedRegion = reg },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedRegion == reg) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    ) {
                        Text(reg.take(3), color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Position")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("FW", "MF", "DF", "GK").forEach { pos ->
                    Button(
                        onClick = { selectedPosition = pos },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPosition == pos) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    ) {
                        Text(pos, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = onBack) { Text("CANCEL") }
                Button(
                    onClick = { if (name.isNotEmpty()) step++ },
                    enabled = name.isNotEmpty()
                ) {
                    Text("NEXT")
                }
            }
        }

        // --- STEP 2: APPEARANCE ---
        if (step == 2) {
            Box(modifier = Modifier.size(200.dp)) {
                EnhancedPlayerAvatar(
                    modifier = Modifier.fillMaxSize(),
                    skinColor = skinColors[skinColorIndex],
                    hairStyle = hairStyles[hairStyleIndex],
                    showKit = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Skin Tone")
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                skinColors.indices.forEach { idx ->
                    Button(
                        onClick = { skinColorIndex = idx },
                        modifier = Modifier.size(40.dp).padding(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = skinColors[idx])
                    ) {}
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Hair Style")
            Button(onClick = { hairStyleIndex = (hairStyleIndex + 1) % hairStyles.size }) {
                Text("Current: ${hairStyles[hairStyleIndex].name}")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = { step-- }) { Text("BACK") }
                Button(
                    onClick = {
                        // FINISH & CREATE
                        val newPlayer = CareerEngine.startCareer(name, selectedPosition, selectedRegion)
                        onCharacterCreated(newPlayer)
                    }
                ) {
                    Text("START CAREER")
                }
            }
        }
    }
}
