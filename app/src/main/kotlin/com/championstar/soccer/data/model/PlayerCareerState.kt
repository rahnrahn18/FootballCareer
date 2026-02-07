package com.championstar.soccer.data.model

/**
 * Mendefinisikan fase-fase utama dalam karier seorang pemain.
 * Status ini akan menentukan jenis event apa yang bisa dibuat oleh GameCalendar.
 */
enum class PlayerCareerState {
    UNATTACHED_NO_AGENT,  // Baru memulai, belum punya agen
    UNATTACHED_WITH_AGENT, // Sudah punya agen, mencari klub pertama
    UNDER_CONTRACT        // Sudah terikat kontrak dengan klub
}