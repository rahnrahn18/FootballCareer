package com.championstar.soccer.ui.creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.championstar.soccer.data.model.PlayerAttributes

class CharacterCreationViewModel : ViewModel() {

    // Data yang diisi oleh user (nama, negara, posisi)
    val playerName = MutableLiveData<String>()
    val selectedCountry = MutableLiveData<String>()
    val selectedPosition = MutableLiveData<String>()

    // Logika untuk pemilihan avatar
    private val _currentAvatarIndex = MutableLiveData(0)
    val currentAvatarIndex: LiveData<Int> = _currentAvatarIndex
    private val totalAvatars = 7

    // BARU: Sistem alokasi poin awal
    private val _unallocatedPoints = MutableLiveData(15) // Beri 15 poin awal
    val unallocatedPoints: LiveData<Int> = _unallocatedPoints

    private val _initialAttributes = MutableLiveData(PlayerAttributes())
    val initialAttributes: LiveData<PlayerAttributes> = _initialAttributes

    fun nextAvatar() {
        val currentIndex = _currentAvatarIndex.value ?: 0
        _currentAvatarIndex.value = (currentIndex + 1) % totalAvatars
    }

    fun previousAvatar() {
        val currentIndex = _currentAvatarIndex.value ?: 0
        _currentAvatarIndex.value = if (currentIndex > 0) currentIndex - 1 else totalAvatars - 1
    }

    // BARU: Fungsi untuk menambah poin ke atribut
    fun allocatePoint(attributeType: String) {
        if ((_unallocatedPoints.value ?: 0) > 0) {
            _unallocatedPoints.value = (_unallocatedPoints.value ?: 0) - 1

            val currentAttrs = _initialAttributes.value!!
            when (attributeType) {
                "Finishing" -> currentAttrs.technical.finishing++
                "Speed" -> currentAttrs.physical.sprintSpeed++
                "Dribbling" -> currentAttrs.technical.dribbling++
            }
            // Memicu update LiveData dengan objek yang sudah dimodifikasi
            _initialAttributes.value = currentAttrs
        }
    }

    fun isDataValid(): Boolean {
        if (playerName.value.isNullOrBlank()) return false
        if (selectedCountry.value.isNullOrEmpty() || selectedCountry.value == "Pilih Negara") return false
        if (selectedPosition.value.isNullOrEmpty()) return false
        return true
    }
}