package com.championstar.soccer.data.static

object LeagueDatabase {

    // 30 Leagues, Tiered
    val leagueTemplates = listOf(
        // Tier 1 (Global Giants)
        LeagueTemplate("L01", "Premier League", "England", 1, 20),
        LeagueTemplate("L02", "La Liga", "Spain", 1, 20),
        LeagueTemplate("L03", "Bundesliga", "Germany", 1, 18),
        LeagueTemplate("L04", "Serie A", "Italy", 1, 20),
        LeagueTemplate("L05", "Ligue 1", "France", 1, 18),

        // Tier 2 (Strong European & South American)
        LeagueTemplate("L06", "Primeira Liga", "Portugal", 2, 18),
        LeagueTemplate("L07", "Eredivisie", "Netherlands", 2, 18),
        LeagueTemplate("L08", "Serie A Brasil", "Brazil", 2, 20),
        LeagueTemplate("L09", "Primera Argentina", "Argentina", 2, 20),
        LeagueTemplate("L10", "Super Lig", "Turkey", 2, 20),
        LeagueTemplate("L11", "Jupiler Pro League", "Belgium", 2, 18),
        LeagueTemplate("L12", "Championship", "England", 2, 24), // Division 2
        LeagueTemplate("L13", "Segunda Division", "Spain", 2, 22),
        LeagueTemplate("L14", "Serie B", "Italy", 2, 20),

        // Tier 3 (Global & Mid-Level)
        LeagueTemplate("L15", "MLS", "USA", 3, 20),
        LeagueTemplate("L16", "J-League", "Japan", 3, 20),
        LeagueTemplate("L17", "K-League", "South Korea", 3, 12),
        LeagueTemplate("L18", "Saudi Pro League", "Saudi Arabia", 3, 18),
        LeagueTemplate("L19", "Liga MX", "Mexico", 3, 18),
        LeagueTemplate("L20", "A-League", "Australia", 3, 12),
        LeagueTemplate("L21", "Scottish Premiership", "Scotland", 3, 12),
        LeagueTemplate("L22", "Super League Greece", "Greece", 3, 14),

        // Tier 4 (Emerging & Lower Divisions)
        LeagueTemplate("L23", "League One", "England", 3, 24),
        LeagueTemplate("L24", "Ekstraklasa", "Poland", 4, 18),
        LeagueTemplate("L25", "Allsvenskan", "Sweden", 4, 16),
        LeagueTemplate("L26", "Eliteserien", "Norway", 4, 16),
        LeagueTemplate("L27", "Superliga", "Denmark", 4, 12),
        LeagueTemplate("L28", "Bundesliga Austria", "Austria", 4, 12),
        LeagueTemplate("L29", "Super League Swiss", "Switzerland", 4, 12),
        LeagueTemplate("L30", "CSL", "China", 4, 16)
    )

    data class LeagueTemplate(
        val id: String,
        val name: String,
        val region: String,
        val tier: Int,
        val teamCount: Int
    )

    // Helper for team names (generic for now to avoid licensing issues)
    val teamSuffixes = listOf("United", "City", "FC", "Athletic", "Rovers", "Wanderers", "Sporting", "Real", "Dynamo", "Inter")

    val cities = listOf(
        // England
        "London", "Manchester", "Liverpool", "Leeds", "Newcastle", "Birmingham", "Sheffield", "Bristol",
        // Spain
        "Madrid", "Barcelona", "Seville", "Valencia", "Bilbao", "Vigo", "Gijon", "Malaga",
        // Germany
        "Munich", "Berlin", "Dortmund", "Hamburg", "Leipzig", "Frankfurt", "Cologne", "Stuttgart",
        // Italy
        "Rome", "Milan", "Turin", "Naples", "Florence", "Genoa", "Bologna", "Verona",
        // France
        "Paris", "Marseille", "Lyon", "Lille", "Bordeaux", "Nice", "Monaco", "Nantes",
        // Others
        "Lisbon", "Porto", "Amsterdam", "Rotterdam", "Sao Paulo", "Rio", "Buenos Aires", "Istanbul", "Brussels",
        "New York", "Los Angeles", "Tokyo", "Seoul", "Riyadh", "Mexico City", "Sydney", "Glasgow", "Athens"
    )

    fun generateTeamName(city: String): String {
        return "$city ${teamSuffixes.random()}"
    }
}
