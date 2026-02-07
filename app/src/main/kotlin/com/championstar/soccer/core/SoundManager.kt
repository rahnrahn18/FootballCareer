package com.championstar.soccer.core

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.championstar.soccer.R // Pastikan baris ini ada

object SoundManager {

    private lateinit var soundPool: SoundPool
    private var uiClickSoundId: Int = 0
    private var isSoundPoolLoaded = false

    private var menuMusicPlayer: MediaPlayer? = null

    fun initialize(context: Context) {
        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            isSoundPoolLoaded = true
        }
        // Sekarang R.raw.ui_click akan dikenali
        uiClickSoundId = soundPool.load(context, R.raw.ui_click, 1)
    }

    fun playUiClick() {
        if (isSoundPoolLoaded) {
            soundPool.play(uiClickSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun playMenuMusic(context: Context) {
        if (menuMusicPlayer == null) {
            // Dan R.raw.menu_music juga akan dikenali
            menuMusicPlayer = MediaPlayer.create(context, R.raw.menu_music)
            menuMusicPlayer?.isLooping = true
        }
        menuMusicPlayer?.start()
    }

    fun stopMenuMusic() {
        menuMusicPlayer?.stop()
        menuMusicPlayer?.release()
        menuMusicPlayer = null
    }

    fun release() {
        soundPool.release()
        stopMenuMusic()
    }
}