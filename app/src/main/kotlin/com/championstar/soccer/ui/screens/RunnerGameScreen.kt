package com.championstar.soccer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.championstar.soccer.domain.models.Player
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RunnerGameScreen(
    player: Player,
    onGameEnd: (Int) -> Unit // returns score
) {
    var gameState by remember { mutableStateOf("PLAYING") } // PLAYING, GAMEOVER
    var score by remember { mutableStateOf(0) }

    // Physics
    var playerY by remember { mutableStateOf(0f) } // 0 = ground, >0 = air
    var velocity by remember { mutableStateOf(0f) }
    val gravity = 2f
    val jumpForce = 35f
    val groundY = 0f // Relative to bottom

    // Entities
    // Obstacles: x (distance from left), type (0=cone, 1=defender)
    val obstacles = remember { mutableStateListOf<Offset>() } // x, height

    var tick by remember { mutableStateOf(0L) }

    LaunchedEffect(gameState) {
        if (gameState == "PLAYING") {
            while (true) {
                delay(16)
                tick++

                // Physics
                if (playerY > 0 || velocity > 0) {
                    playerY += velocity
                    velocity -= gravity
                    if (playerY < 0) {
                        playerY = 0f
                        velocity = 0f
                    }
                }

                // Spawn Obstacles
                if (tick % 100 == 0L) { // Every ~1.6 seconds
                    obstacles.add(Offset(1200f + Random.nextInt(200), 50f + Random.nextInt(50)))
                }

                // Move Obstacles
                val iterator = obstacles.listIterator()
                while (iterator.hasNext()) {
                    val obs = iterator.next()
                    val newX = obs.x - 10f // Speed
                    if (newX < -100f) {
                        iterator.remove()
                        score++ // Scored for passing
                    } else {
                        iterator.set(Offset(newX, obs.y))

                        // Collision Detection
                        // Player is at X=100, Width=50, Height=100 (visual approx)
                        // Obstacle is at newX, Width=40, Height=obs.y

                        val playerRect = androidx.compose.ui.geometry.Rect(100f, playerY, 150f, playerY + 100f)
                        // Obstacle logic is tricky with ground coordinates.
                        // Let's assume obstacles are on the ground.

                        // Check X overlap
                        if (newX < 150f && newX + 40f > 100f) {
                            // Check Y overlap (Player is on ground if Y=0)
                            // Obstacle height is obs.y
                            // If playerY < obs.y, collision!
                            if (playerY < obs.y) {
                                gameState = "GAMEOVER"
                            }
                        }
                    }
                }

                if (gameState == "GAMEOVER") break
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Sky Blue
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (gameState == "PLAYING" && playerY == 0f) {
                        velocity = jumpForce
                    }
                })
            }
    ) {
        // Ground
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFF4CAF50)) // Grass
                .align(Alignment.BottomCenter)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val groundLevel = h - 100.dp.toPx() // Top of grass

            // Draw Player
            // Y is distance from ground. Canvas Y is top-down.
            // So drawY = groundLevel - playerY - playerHeight
            val playerH = 100f
            val drawY = groundLevel - playerY - playerH

            drawRect(
                color = Color.Red,
                topLeft = Offset(100f, drawY),
                size = Size(50f, playerH)
            )

            // Draw Obstacles
            obstacles.forEach { obs ->
                val obsH = obs.y // Stored as height
                val obsX = obs.x
                val obsY = groundLevel - obsH

                drawRect(
                    color = Color.Black,
                    topLeft = Offset(obsX, obsY),
                    size = Size(40f, obsH)
                )
            }

            // Draw Score
            // Text drawing in Canvas is hard in Compose w/o native canvas access or text measurer
            // So use Overlay Text composable
        }

        Text(
            "Score: $score",
            modifier = Modifier.padding(16.dp).align(Alignment.TopEnd),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        if (gameState == "GAMEOVER") {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("GAME OVER") },
                text = { Text("You avoided $score obstacles!") },
                confirmButton = {
                    Button(onClick = { onGameEnd(score) }) {
                        Text("Finish")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        // Restart
                        obstacles.clear()
                        score = 0
                        playerY = 0f
                        velocity = 0f
                        gameState = "PLAYING"
                    }) {
                        Text("Retry")
                    }
                }
            )
        } else {
             Text(
                "TAP TO JUMP",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
