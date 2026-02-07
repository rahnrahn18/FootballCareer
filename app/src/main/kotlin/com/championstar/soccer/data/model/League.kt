// app/src/main/kotlin/com/championstar/soccer/data/model/League.kt
package com.championstar.soccer.data.model

import com.google.gson.annotations.SerializedName

data class League(
    @SerializedName("league_id")
    val id: Int,

    @SerializedName("league_name")
    val name: String,

    @SerializedName("country_name")
    val country: String,

    // SEKARANG MENGGUNAKAN Club.kt YANG SUDAH DISATUKAN
    @SerializedName("clubs")
    val clubs: List<Club>
)