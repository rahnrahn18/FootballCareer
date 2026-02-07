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
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Generate World (Simulation Check)
        val leagues = WorldGenerator.generateWorld()
        val topPlayers = RankingEngine.getTop100Players(leagues)

        // Squad Check
        val firstTeam = leagues.first().teams.first()
        val squad = SquadEngine.selectMatchSquad(firstTeam)

        setContent {
            SimulationDebugView(leagues, topPlayers, squad.starters)
        }
    }
}

@Composable
fun SimulationDebugView(leagues: List<League>, topPlayers: List<Player>, starters: List<Player>) {
    LazyColumn {
        item { Text("Total Leagues: ${leagues.size}") }
        item { Text("Total Teams: ${leagues.sumOf { it.teams.size }}") }
        item { Text("Total Players: ${leagues.sumOf { l -> l.teams.sumOf { t -> t.players.size } }}") }

        item { Text("--- Top 5 Players ---") }
        items(topPlayers.take(5)) { player ->
            Text("${player.name} (${player.position}) - ${String.format("%.1f", player.overallRating)}")
        }

        item { Text("--- Example Squad Starters ---") }
        items(starters) { player ->
             Text("${player.position}: ${player.name}")
        }
    }
}
