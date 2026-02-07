package com.championstar.soccer.data.repository

import com.championstar.soccer.data.model.Agent
import com.championstar.soccer.data.model.AgentSpecialty

// Menggunakan 'object' untuk membuatnya Singleton, sehingga data agen konsisten di seluruh aplikasi
object AgentRepository {

    private val allAgents = listOf(
        Agent(
            id = "ag_001",
            name = "David Roth",
            reputation = 25,
            commissionRate = 0.15, // 15%
            specialty = AgentSpecialty.BALANCED,
            description = "Seorang agen pemula yang lapar akan kesuksesan, sama sepertimu. Baik dalam segala hal, tapi tidak ahli di bidang tertentu.",
            portraitResourceName = "agent_david_roth"
        ),
        Agent(
            id = "ag_002",
            name = "Sofia Vargas",
            reputation = 40,
            commissionRate = 0.20, // 20%
            specialty = AgentSpecialty.NEGOTIATOR,
            description = "Mantan pengacara yang ahli dalam negosiasi. Dia akan memastikan kamu mendapatkan gaji terbaik, dengan potongan yang sepadan.",
            portraitResourceName = "agent_sofia_vargas"
        ),
        Agent(
            id = "ag_003",
            name = "Kenji Tanaka",
            reputation = 35,
            commissionRate = 0.18, // 18%
            specialty = AgentSpecialty.MARKETING_GURU,
            description = "Punya koneksi luas di dunia sponsorship. Bekerja dengannya akan membuatmu cepat terkenal di luar lapangan.",
            portraitResourceName = "agent_kenji_tanaka"
        )
    )

    // Fungsi untuk mendapatkan agen yang tersedia untuk pemain baru
    fun getInitialAgents(): List<Agent> {
        // Untuk pemain baru, kita hanya tawarkan agen dengan reputasi di bawah 50
        return allAgents.filter { it.reputation < 50 }
    }

    fun getAgentById(id: String): Agent? {
        return allAgents.find { it.id == id }
    }
}