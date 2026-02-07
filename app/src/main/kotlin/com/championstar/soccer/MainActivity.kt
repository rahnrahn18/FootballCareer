package com.championstar.soccer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.simulation.engine.MatchEngine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Basic check to ensure math works
        val randomCheck = GameMath.nextFloat()

        setContent {
            Text(text = "Championstar Soccer Simulation Running... Random: $randomCheck")
        }
    }
}
