package com.championstar.soccer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.gson.Gson

data class CharacterAppearance(
    val skinColor: Int = 0xFFF1C27D.toInt(),
    val hairColor: Int = 0xFF4E342E.toInt(),
    val hairStyle: Int = 0, // 0-2
    val eyeColor: Int = 0xFF000000.toInt()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (String, String, String) -> Unit // name, position, appearanceJson
) {
    var name by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("Forward") }
    var appearance by remember { mutableStateOf(CharacterAppearance()) }

    val positions = listOf("Forward", "Midfielder", "Defender", "Goalkeeper")

    // Simple color lists
    val skinColors = listOf(0xFFF1C27D, 0xFFE0AC69, 0xFF8D5524, 0xFFC68642)
    val hairColors = listOf(0xFF4E342E, 0xFF000000, 0xFFD32F2F, 0xFFFDD835)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Your Player") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Preview
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AvatarView(appearance)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Customization Controls
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    val nextIdx = (skinColors.indexOf(appearance.skinColor.toLong()) + 1) % skinColors.size
                    appearance = appearance.copy(skinColor = skinColors[nextIdx].toInt())
                }) { Text("Skin") }

                Button(onClick = {
                    val nextIdx = (hairColors.indexOf(appearance.hairColor.toLong()) + 1) % hairColors.size
                    appearance = appearance.copy(hairColor = hairColors[nextIdx].toInt())
                }) { Text("Hair Color") }

                Button(onClick = {
                    appearance = appearance.copy(hairStyle = (appearance.hairStyle + 1) % 3)
                }) { Text("Hair Style") }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Player Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Position", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                positions.forEach { pos ->
                    FilterChip(
                        selected = position == pos,
                        onClick = { position = pos },
                        label = { Text(pos.take(3)) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val json = Gson().toJson(appearance)
                        onCharacterCreated(name, position, json)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Start Career")
            }
        }
    }
}

@Composable
fun AvatarView(appearance: CharacterAppearance) {
    Canvas(modifier = Modifier.size(150.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Head
        drawCircle(
            color = Color(appearance.skinColor),
            radius = size.width / 3,
            center = Offset(centerX, centerY)
        )

        // Eyes
        drawCircle(
            color = Color(appearance.eyeColor),
            radius = 5.dp.toPx(),
            center = Offset(centerX - 20.dp.toPx(), centerY - 10.dp.toPx())
        )
        drawCircle(
            color = Color(appearance.eyeColor),
            radius = 5.dp.toPx(),
            center = Offset(centerX + 20.dp.toPx(), centerY - 10.dp.toPx())
        )

        // Mouth
        drawArc(
            color = Color.Black,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - 15.dp.toPx(), centerY + 10.dp.toPx()),
            size = Size(30.dp.toPx(), 15.dp.toPx())
        )

        // Hair
        val hairColor = Color(appearance.hairColor)
        when (appearance.hairStyle) {
            0 -> { // Short
                drawArc(
                    color = hairColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(centerX - 55.dp.toPx(), centerY - 60.dp.toPx()),
                    size = Size(110.dp.toPx(), 100.dp.toPx())
                )
            }
            1 -> { // Long
                drawRect(
                    color = hairColor,
                    topLeft = Offset(centerX - 55.dp.toPx(), centerY - 50.dp.toPx()),
                    size = Size(110.dp.toPx(), 120.dp.toPx())
                )
            }
            2 -> { // Mohawk
                 drawRect(
                    color = hairColor,
                    topLeft = Offset(centerX - 10.dp.toPx(), centerY - 70.dp.toPx()),
                    size = Size(20.dp.toPx(), 60.dp.toPx())
                )
            }
        }
    }
}