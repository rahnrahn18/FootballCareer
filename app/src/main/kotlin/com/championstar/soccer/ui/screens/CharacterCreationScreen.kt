package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.SeasonStats
import com.championstar.soccer.ui.components.PlayerAssetImage
import java.util.UUID

// Position Data Class for UI
data class PositionOption(
    val code: String,
    val name: String,
    val group: String // GK, DEF, MID, ATT
)

val availablePositions = listOf(
    PositionOption("GK", "Goalkeeper", "GK"),
    PositionOption("CB", "Center Back", "DEF"),
    PositionOption("LB", "Left Back", "DEF"),
    PositionOption("RB", "Right Back", "DEF"),
    PositionOption("CDM", "Defensive Mid", "MID"),
    PositionOption("CM", "Center Mid", "MID"),
    PositionOption("CAM", "Attacking Mid", "MID"),
    PositionOption("LM", "Left Mid", "MID"),
    PositionOption("RM", "Right Mid", "MID"),
    PositionOption("LW", "Left Wing", "ATT"),
    PositionOption("RW", "Right Wing", "ATT"),
    PositionOption("CF", "Center Forward", "ATT"),
    PositionOption("ST", "Striker", "ATT")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (Player) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf<PositionOption?>(null) }

    // Validation
    val isNameValid = name.isNotBlank() && name.length <= 16
    val isPositionValid = selectedPosition != null
    val canProceed = isNameValid && isPositionValid

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Darker background
            .padding(16.dp)
    ) {
        // --- LEFT PANEL: Identity (35%) ---
        Column(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight()
                .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "NEW CAREER",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFFD700), // Gold
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar Box
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .border(2.dp, Color(0xFFFFD700), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                 PlayerAssetImage(modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 16) name = it },
                label = { Text("Player Name (Max 16)") },
                singleLine = true,
                isError = name.isNotEmpty() && !isNameValid,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFFD700),
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (canProceed && selectedPosition != null) {
                        val newPlayer = Player(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            age = 17,
                            position = selectedPosition!!.code,
                            overallRating = 60.0,
                            potential = 85.0 + Math.random() * 10.0,
                            seasonStats = SeasonStats(seasonYear = 2024),
                            matchHistory = mutableListOf()
                        )
                        onCharacterCreated(newPlayer)
                    }
                },
                enabled = canProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color.DarkGray
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("START JOURNEY", fontWeight = FontWeight.Bold, color = if(canProceed) Color.White else Color.Gray)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // --- RIGHT PANEL: Position Selection (65%) ---
        Column(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight()
                .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "SELECT POSITION",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choose your role on the pitch.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(availablePositions) { position ->
                    PositionCard(
                        position = position,
                        isSelected = selectedPosition == position,
                        onClick = { selectedPosition = position }
                    )
                }
            }
        }
    }
}

@Composable
fun PositionCard(
    position: PositionOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFFFD700) else Color(0xFF2C2C2C)
    val contentColor = if (isSelected) Color.Black else Color.White
    val borderColor = if (isSelected) Color.White else Color.Transparent

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .aspectRatio(1f) // Square
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Placeholder (Text circle or Icon)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Black.copy(alpha=0.1f) else Color.Black.copy(alpha=0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = position.code.take(1),
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = position.code,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = position.name,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
