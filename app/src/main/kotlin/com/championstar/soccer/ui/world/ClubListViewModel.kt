// app/src/main/kotlin/com/championstar/soccer/ui/world/ClubListViewModel.kt
package com.championstar.soccer.ui.world

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.championstar.soccer.data.model.Club // <-- IMPORT DIPERBARUI
import com.championstar.soccer.data.repository.DatabaseRepository

class ClubListViewModel : ViewModel() {

    private val _clubs = MutableLiveData<List<Club>>() // <-- TIPE DATA DIPERBARUI
    val clubs: LiveData<List<Club>> = _clubs // <-- TIPE DATA DIPERBARUI

    fun loadClubs(leagueId: Int) {
        val league = DatabaseRepository.getLeagues().find { it.id == leagueId }
        _clubs.value = league?.clubs ?: emptyList()
    }
}