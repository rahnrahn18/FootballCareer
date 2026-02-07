package com.championstar.soccer.data.model

// Pilihan yang bisa diambil pemain saat momen krusial
data class PlayerChoice(
    val description: String, // Misal: "Tembak ke sudut atas"
    val primaryAttribute: BuffType, // Atribut utama yang diuji
    val secondaryAttribute: BuffType // Atribut pendukung
)

// Momen krusial yang membutuhkan input pemain
data class KeyMoment(
    val minute: Int,
    val description: String,
    val choices: List<PlayerChoice>
)

// Satu baris komentar dalam log pertandingan
data class MatchCommentary(
    val minute: Int,
    val text: String
)