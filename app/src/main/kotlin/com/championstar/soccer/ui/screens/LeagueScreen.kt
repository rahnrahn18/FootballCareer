package com.championstar.soccer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.MatchOutcome
import com.championstar.soccer.domain.models.Team

@Composable
fun LeagueScreen(
    leagues: List<League>,
    currentLeagueId: String? // If null, show all
) {
    val currentLeague = leagues.find { it.id == currentLeagueId } ?: leagues.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        if (currentLeague == null) {
            Text("No Leagues Found", color = Color.Red)
            return@Column
        }

        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currentLeague.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tier ${currentLeague.tier} | Matchday ${currentLeague.currentMatchday}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TABLE HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF333333))
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderCell("#", 0.07f)
            HeaderCell("Team", 0.35f, TextAlign.Start)
            HeaderCell("Form", 0.15f)
            HeaderCell("P", 0.06f)
            HeaderCell("W", 0.06f)
            HeaderCell("D", 0.06f)
            HeaderCell("L", 0.06f)
            HeaderCell("GF", 0.06f)
            HeaderCell("GA", 0.06f)
            HeaderCell("GD", 0.06f)
            HeaderCell("Pts", 0.08f, color = Color(0xFFFFD700))
        }

        // --- TABLE ROWS ---
        val sortedTeams = currentLeague.teams.sortedWith(
            compareByDescending<Team> { it.leagueStats.points }
                .thenByDescending { it.leagueStats.goalDifference }
                .thenByDescending { it.leagueStats.goalsFor }
        )

        LazyColumn {
            itemsIndexed(sortedTeams) { index, team ->
                LeagueTableRow(team, index + 1)
                HorizontalDivider(color = Color(0xFF2C2C2C), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun RowScope.HeaderCell(
    text: String,
    weight: Float,
    align: TextAlign = TextAlign.Center,
    color: Color = Color.LightGray
) {
    Text(
        text = text,
        color = color,
        modifier = Modifier.weight(weight),
        textAlign = align,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
}

@Composable
fun LeagueTableRow(team: Team, position: Int) {
    val stats = team.leagueStats
    val posColor = when(position) {
        1 -> Color(0xFF4CAF50) // Promotion / Champion
        2 -> Color(0xFF8BC34A) // Promotion
        in 3..4 -> Color(0xFFCDDC39) // Playoffs
        in 17..20 -> Color(0xFFF44336) // Relegation
        else -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position
        Box(
            modifier = Modifier
                .weight(0.07f)
                .padding(end = 4.dp),
            contentAlignment = Alignment.Center
        ) {
             Text(
                text = "$position",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(if(position <= 4 || position >= 17) posColor.copy(alpha=0.2f) else Color.Transparent, CircleShape)
                    .padding(4.dp)
            )
        }

        // Team Name
        Text(
            text = team.name,
            color = Color.White,
            modifier = Modifier.weight(0.35f),
            textAlign = TextAlign.Start,
            fontSize = 12.sp,
            maxLines = 1
        )

        // Form
        Row(
            modifier = Modifier.weight(0.15f),
            horizontalArrangement = Arrangement.Center
        ) {
            stats.form.take(5).forEach { outcome ->
                val (color, char) = when(outcome) {
                    MatchOutcome.WIN -> Color(0xFF4CAF50) to "W"
                    MatchOutcome.DRAW -> Color(0xFFFFC107) to "D"
                    MatchOutcome.LOSS -> Color(0xFFF44336) to "L"
                }
                Text(
                    text = char,
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 1.dp)
                )
            }
        }

        // Stats
        DataCell("${stats.played}", 0.06f)
        DataCell("${stats.won}", 0.06f)
        DataCell("${stats.drawn}", 0.06f)
        DataCell("${stats.lost}", 0.06f)
        DataCell("${stats.goalsFor}", 0.06f)
        DataCell("${stats.goalsAgainst}", 0.06f)
        DataCell("${stats.goalDifference}", 0.06f, if(stats.goalDifference > 0) Color.Green else if(stats.goalDifference < 0) Color.Red else Color.Gray)

        // Points
        Text(
            text = "${stats.points}",
            color = Color(0xFFFFD700),
            modifier = Modifier.weight(0.08f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun RowScope.DataCell(
    text: String,
    weight: Float,
    color: Color = Color.LightGray
) {
    Text(
        text = text,
        color = color,
        modifier = Modifier.weight(weight),
        textAlign = TextAlign.Center,
        fontSize = 11.sp
    )
}
