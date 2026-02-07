package com.championstar.soccer.ui.world

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.championstar.soccer.data.model.League
import com.championstar.soccer.data.repository.DatabaseRepository

class LeagueListViewModel : ViewModel() {

    private val _leagues = MutableLiveData<List<League>>()
    val leagues: LiveData<List<League>> = _leagues

    init {
        loadLeagues()
    }

    private fun loadLeagues() {
        _leagues.value = DatabaseRepository.getLeagues()
    }
}