package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Equipa
import com.example.trabalhocm.data.model.MembroEquipa
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

class EquipaRepository {
    private val client = SupabaseClient.client

    suspend fun listarTodasEquipas(): Result<List<Equipa>> {
        return runCatching {
            client.from("equipa")
                .select()
                .decodeList<Equipa>()
        }
    }

    suspend fun obterMinhasEquipas(userId: String): Result<List<MembroEquipa>> {
        return runCatching {
            client.from("membro_equipa")
                .select {
                    filter {
                        eq("id_utilizador", userId)
                    }
                }
                .decodeList<MembroEquipa>()
        }
    }
}