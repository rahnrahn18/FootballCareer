package com.championstar.soccer.ui.mainmenu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.championstar.soccer.core.SoundManager
import com.championstar.soccer.data.repository.DatabaseRepository
import com.championstar.soccer.data.repository.EventRepository // <-- IMPORT BARU
import com.championstar.soccer.data.repository.SaveRepository
import com.championstar.soccer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SaveRepository
        SaveRepository.init(applicationContext)
        
        // Inisialisasi SoundManager
        SoundManager.initialize(this)
        
        // INISIALISASI DATABASE DUNIA GAME
        DatabaseRepository.init(applicationContext)
        
        // INISIALISASI EVENT REPOSITORY
        EventRepository.init(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.release()
    }
}
