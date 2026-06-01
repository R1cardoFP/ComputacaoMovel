package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminStats
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject

class AdminRepository {

    private val client = SupabaseClient.client

    suspend fun carregarEstatisticasAdmin(): Result<AdminStats> {
        return runCatching {
            val users = client.from("utilizador")
                .select()
                .decodeList<JsonObject>()
                .size

            val teams = client.from("equipa")
                .select()
                .decodeList<JsonObject>()
                .size

            val tournaments = client.from("torneio")
                .select()
                .decodeList<JsonObject>()
                .size

            AdminStats(
                totalUsers = users,
                totalTeams = teams,
                totalTournaments = tournaments
            )
        }
    }
}