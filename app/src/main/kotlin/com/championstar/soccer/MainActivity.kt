package com.championstar.soccer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.championstar.soccer.simulation.engine.RankingEngine
import com.championstar.soccer.simulation.engine.SquadEngine
import com.championstar.soccer.simulation.engine.WorldGenerator
import com.championstar.soccer.simulation.engine.CareerEngine
import com.championstar.soccer.simulation.engine.AgentEngine
import com.championstar.soccer.domain.models.Contract
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.domain.models.Team

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Generate World (Simulation Check)
        val leagues = WorldGenerator.generateWorld()

        // --- CAREER START DEMO ---
        // 1. Create User
        val myPlayer = CareerEngine.startCareer("User Hero", "ST", "Europe")

        // 2. Find Trial Offers (Day 1)
        val trialOffers = CareerEngine.generateTrialOffers(myPlayer, leagues)

        // 3. Simulate Signing & Agent Upgrade
        var signedMessage = "No offers yet."
        if (trialOffers.isNotEmpty()) {
            val (team, contract) = trialOffers.first()
            myPlayer.contract = contract
            myPlayer.contract!!.signingBonus // Access check

            // Agent gets XP
            AgentEngine.upgradeAgent(myPlayer.agent!!)
            signedMessage = "Signed with ${team.name} for $${contract.salary}/week! Agent upgraded to Lvl ${myPlayer.agent!!.level}"
        }

        setContent {
            SimulationDebugView(leagues, myPlayer, trialOffers, signedMessage)
        }
    }
}

@Composable
fun SimulationDebugView(
    leagues: List<League>,
    player: Player,
    offers: List<Pair<Team, Contract>>,
    status: String
) {
    LazyColumn {
        item { Text("--- Career Mode Start ---") }
        item { Text("Player: ${player.name} (${player.position})") }
        item { Text("Rating: ${String.format("%.1f", player.overallRating)} | Pot: ${String.format("%.1f", player.potential)}") }
        item { Text("Agent: ${player.agent?.name} (Lvl ${player.agent?.level})") }

        item { Text("\n--- Offers Received ---") }
        if (offers.isEmpty()) {
            item { Text("No clubs interested. Try training harder!") }
        } else {
            items(offers) { (team, contract) ->
                Text("${team.name} (Tier ${team.leagueId.take(3)}): $${contract.salary}/wk, ${contract.yearsRemaining} yrs")
            }
        }

        item { Text("\n--- Status ---") }
        item { Text(status) }
    }
}
