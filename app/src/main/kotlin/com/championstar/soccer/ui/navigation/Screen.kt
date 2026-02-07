package com.championstar.soccer.ui.navigation

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object CharacterCreation : Screen("character_creation")
    object Dashboard : Screen("dashboard")
    object Match : Screen("match")
    object Business : Screen("business")
    object Story : Screen("story")
}