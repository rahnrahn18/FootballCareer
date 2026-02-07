package com.championstar.soccer.data.model

import java.time.LocalDate

data class Fixture(
    val date: LocalDate,
    val homeClubId: Int,
    val awayClubId: Int
)