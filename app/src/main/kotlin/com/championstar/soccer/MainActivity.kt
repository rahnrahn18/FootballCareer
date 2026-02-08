package com.championstar.soccer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.championstar.soccer.simulation.engine.RankingEngine
import com.championstar.soccer.simulation.engine.SquadEngine
import com.championstar.soccer.simulation.engine.WorldGenerator
import com.championstar.soccer.simulation.engine.CareerEngine
import com.championstar.soccer.simulation.engine.AgentEngine
import com.championstar.soccer.simulation.engine.TimeEngine
import com.championstar.soccer.simulation.engine.EventEngine
import com.championstar.soccer.domain.models.Contract
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Generate World
        val leagues = WorldGenerator.generateWorld()

        // 2. Start Career (Zero to Hero)
        val myPlayer = CareerEngine.startCareer("User Hero", "ST", "Europe")

        // 3. Initial Trial
        val trialOffers = CareerEngine.generateTrialOffers(myPlayer, leagues)
        if (trialOffers.isNotEmpty()) {
            val (team, contract) = trialOffers.first()
            myPlayer.contract = contract // Auto-accept for demo
            team.players.add(myPlayer) // Join team
        }

        setContent {
            GameLoopView(leagues, myPlayer)
        }
    }
}

@Composable
fun GameLoopView(leagues: List<League>, player: Player) {
    val gameLog = remember { mutableStateOf("Game Started!\nSigned with ${player.contract?.salary?.let { "$" + it } ?: "No one"} / wk.") }
    val currentWeek = remember { mutableStateOf(TimeEngine.currentDate.toString()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Week: ${currentWeek.value}")
        Text("Player: ${player.name} (${player.position})")
        Text("Stats: OVR ${String.format("%.1f", player.overallRating)} | Form ${String.format("%.1f", player.form)} | Sta ${String.format("%.1f", player.stamina)}")
        Text("Goals: ${player.goals} | Apps: ${player.appearances}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // --- THE GAME LOOP ---
            val sb = StringBuilder()

            // 1. Process Week (Match, Training, Finance)
            sb.append(TimeEngine.processWeek(player, leagues))

            // 2. Random Event
            val event = EventEngine.generateWeeklyEvent(player)
            if (event != null) {
                sb.append("❗ EVENT: ${event.title}\n${event.description}\n")
                // Auto-pick first choice for demo
                val choice = event.choices.first()
                sb.append("   > Chose: ${choice.text}\n   > Result: ${choice.consequence(player)}\n")
            }

            // 3. Update UI State
            gameLog.value = sb.toString()
            currentWeek.value = TimeEngine.currentDate.toString()

        }) {
            Text("Advance Week (Play/Train)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("--- Log ---")
        Text(gameLog.value)
    }
}
