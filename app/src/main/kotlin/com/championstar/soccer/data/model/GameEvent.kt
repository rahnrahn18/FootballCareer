package com.championstar.soccer.data.model

import com.google.gson.annotations.SerializedName

// Kelas utama yang merepresentasikan satu event naratif
data class GameEvent(
    val eventId: String,
    val category: String,
    val title: String,
    val description: String,
    val triggers: List<Trigger>,
    val choices: List<Choice>
)

// Mendefinisikan kondisi kapan sebuah event bisa muncul
data class Trigger(
    val type: String,
    val value: String? = null,
    val min: Int? = null,
    val max: Int? = null,
    val attribute: String? = null
)

// Mendefinisikan satu pilihan yang bisa diambil pemain
data class Choice(
    val text: String,
    val effects: List<Effect>
)

// Mendefinisikan konsekuensi dari sebuah pilihan
data class Effect(
    val type: String,
    val target: String? = null,
    val value: Double? = null,
    @SerializedName("duration_weeks")
    val durationWeeks: Int? = null,
    val outcomes: List<RandomOutcome>? = null
)

// Untuk efek yang memiliki hasil acak (misalnya, perjudian atau investasi)
data class RandomOutcome(
    val chance: Double,
    val label: String,
    val effects: List<Effect>
)