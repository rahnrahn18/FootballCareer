package com.championstar.soccer.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.Player
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class TrainingMode {
    MENU, PENALTY, PASSING, RUNNER
}

@Composable
fun TrainingScreen(
    player: Player,
    onTrainingComplete: (Boolean) -> Unit // success
) {
    var mode by remember { mutableStateOf(TrainingMode.MENU) }
    var score by remember { mutableStateOf(0) }
    var attempts by remember { mutableStateOf(0) }
    val maxAttempts = 5

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        when (mode) {
            TrainingMode.MENU -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("TRAINING GROUND", style = MaterialTheme.typography.displayMedium, color = Color.White)
                    Text("Sharpen your skills to improve form.", color = Color.Gray)

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TrainingCard("Penalty Practice", "Timing based shooting", Color(0xFFEF5350)) {
                            mode = TrainingMode.PENALTY
                            score = 0
                            attempts = 0
                        }
                        TrainingCard("Passing Drill", "Precision passing", Color(0xFF42A5F5)) {
                            mode = TrainingMode.PASSING
                            score = 0
                            attempts = 0
                        }
                        TrainingCard("Endurance Run", "Stamina obstacle course", Color(0xFF66BB6A)) {
                            mode = TrainingMode.RUNNER
                            score = 0
                            attempts = 0
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { onTrainingComplete(false) }, // Skipped
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Skip Training (Form -5)")
                    }
                }
            }
            TrainingMode.PENALTY -> {
                PenaltyMinigame(
                    score = score,
                    attempts = attempts,
                    maxAttempts = maxAttempts,
                    onResult = { success ->
                        attempts++
                        if (success) score++
                        if (attempts >= maxAttempts) {
                            // Finish
                            if (score >= 3) player.form = (player.form + 5).coerceAtMost(100.0)
                            else player.form = (player.form + 1).coerceAtMost(100.0)
                            onTrainingComplete(true)
                        }
                    }
                )
            }
            TrainingMode.PASSING -> {
                PassingMinigame(
                     score = score,
                    attempts = attempts,
                    maxAttempts = maxAttempts,
                    onResult = { success ->
                        attempts++
                        if (success) score++
                        if (attempts >= maxAttempts) {
                             // Finish
                            if (score >= 3) player.form = (player.form + 5).coerceAtMost(100.0)
                            else player.form = (player.form + 1).coerceAtMost(100.0)
                            onTrainingComplete(true)
                        }
                    }
                )
            }
            TrainingMode.RUNNER -> {
                RunnerGameScreen(player) { finalScore ->
                    // Reward based on score (obstacles avoided)
                    if (finalScore >= 10) {
                        player.stamina = (player.stamina + 10.0).coerceAtMost(100.0)
                        player.form = (player.form + 5.0).coerceAtMost(100.0)
                        onTrainingComplete(true)
                    } else {
                        player.stamina = (player.stamina + 2.0).coerceAtMost(100.0)
                         onTrainingComplete(false)
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingCard(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.size(200.dp, 150.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha=0.8f))
        }
    }
}

// --- MINIGAMES ---

@Composable
fun PenaltyMinigame(
    score: Int,
    attempts: Int,
    maxAttempts: Int,
    onResult: (Boolean) -> Unit
) {
    var isRunning by remember(attempts) { mutableStateOf(true) }
    var cursorPosition by remember(attempts) { mutableStateOf(0.5f) } // 0.0 to 1.0
    var direction by remember(attempts) { mutableStateOf(1) } // 1 or -1

    // Target Zone (Randomized per attempt)
    val targetStart by remember(attempts) { mutableStateOf(Random.nextFloat() * 0.6f + 0.1f) } // 0.1 to 0.7
    val targetWidth = 0.2f
    val targetEnd = targetStart + targetWidth

    LaunchedEffect(isRunning, attempts) {
        while (isRunning) {
            delay(16) // ~60 FPS
            cursorPosition += 0.02f * direction
            if (cursorPosition >= 1f || cursorPosition <= 0f) {
                direction *= -1
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("PENALTY DRILL: ${attempts + 1}/$maxAttempts", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("Score: $score", style = MaterialTheme.typography.titleLarge, color = Color(0xFFFFD700))

        Spacer(modifier = Modifier.height(64.dp))

        // The Bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(60.dp)
                .background(Color.Gray, RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        if (isRunning) {
                            isRunning = false
                            val success = cursorPosition in targetStart..targetEnd
                            onResult(success)
                        }
                    })
                }
        ) {
            // Target Zone (Green)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(targetWidth)
                    .align(Alignment.CenterStart)
                    .offset(x = (targetStart * 800).dp) // Approximate sizing issue? Need relative offset
                    // Better approach: Draw Canvas or calculate weight
            )

            // Let's use Canvas for precision
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw Target
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(w * targetStart, 0f),
                    size = Size(w * targetWidth, h)
                )

                // Draw Cursor
                drawLine(
                    color = Color.Red,
                    start = Offset(w * cursorPosition, 0f),
                    end = Offset(w * cursorPosition, h),
                    strokeWidth = 10f
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Tap to Shoot!", color = Color.White)
    }
}

@Composable
fun PassingMinigame(
    score: Int,
    attempts: Int,
    maxAttempts: Int,
    onResult: (Boolean) -> Unit
) {
    var isRunning by remember(attempts) { mutableStateOf(true) }
    var angle by remember(attempts) { mutableStateOf(0f) } // 0 to 360

    val targetAngle by remember(attempts) { mutableStateOf(Random.nextFloat() * 360f) }
    val tolerance = 20f // +/- 20 degrees

    LaunchedEffect(isRunning, attempts) {
        while (isRunning) {
            delay(16)
            angle = (angle + 5f) % 360f
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("PASSING DRILL: ${attempts + 1}/$maxAttempts", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("Score: $score", style = MaterialTheme.typography.titleLarge, color = Color(0xFFFFD700))

        Spacer(modifier = Modifier.height(32.dp))

        Box(
             modifier = Modifier
                .size(300.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                         if (isRunning) {
                            isRunning = false
                            // Check angle difference (handle wrap around)
                            val diff = kotlin.math.abs(angle - targetAngle)
                            val normalizedDiff = if (diff > 180) 360 - diff else diff
                            val success = normalizedDiff <= tolerance
                            onResult(success)
                        }
                    })
                },
             contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val cy = size.height / 2
                val r = size.width / 2

                // Draw Circle
                drawCircle(color = Color.Gray, radius = r, style = Stroke(width = 4f))

                // Draw Target Wedge
                drawArc(
                    color = Color.Green,
                    startAngle = targetAngle - tolerance,
                    sweepAngle = tolerance * 2,
                    useCenter = true,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )

                // Draw Rotating Arrow
                val rad = Math.toRadians(angle.toDouble())
                val endX = cx + (r * 0.9f) * cos(rad).toFloat()
                val endY = cy + (r * 0.9f) * sin(rad).toFloat()

                drawLine(
                    color = Color.Yellow,
                    start = Offset(cx, cy),
                    end = Offset(endX, endY),
                    strokeWidth = 8f
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Tap to Pass!", color = Color.White)
    }
}
