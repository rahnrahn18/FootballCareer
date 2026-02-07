package com.championstar.soccer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerCareerStats(
    var appearances: Int = 0,
    var goals: Int = 0,
    var assists: Int = 0
) : Parcelable