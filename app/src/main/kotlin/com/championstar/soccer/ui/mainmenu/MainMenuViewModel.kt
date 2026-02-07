package com.championstar.soccer.ui.mainmenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.championstar.soccer.core.Event
import com.championstar.soccer.data.model.Player // <-- IMPORT Player
import com.championstar.soccer.data.repository.SaveRepository
import kotlinx.coroutines.launch

class MainMenuViewModel : ViewModel() {

    private val _hasSaveData = MutableLiveData<Boolean>()
    val hasSaveData: LiveData<Boolean> = _hasSaveData

    // SEKARANG MENGIRIMKAN Player SECARA LANGSUNG
    private val _navigateToDashboard = MutableLiveData<Event<Player>>()
    val navigateToDashboard: LiveData<Event<Player>> = _navigateToDashboard

    private val _showNoSaveDataToast = MutableLiveData<Event<Unit>>()
    val showNoSaveDataToast: LiveData<Event<Unit>> = _showNoSaveDataToast

    fun checkForSaveData() {
        viewModelScope.launch {
            _hasSaveData.value = SaveRepository.hasSlot1()
        }
    }

    fun onLoadGameClicked() {
        viewModelScope.launch {
            if (SaveRepository.hasSlot1()) {
                val loadedPlayer = SaveRepository.loadSlot1()
                if (loadedPlayer != null) {
                    _navigateToDashboard.value = Event(loadedPlayer)
                }
            } else {
                _showNoSaveDataToast.value = Event(Unit)
            }
        }
    }
}