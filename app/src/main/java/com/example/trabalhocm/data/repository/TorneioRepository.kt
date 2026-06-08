package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class TorneioRepository {

    private val client = SupabaseClient.client

    suspend fun listarTorneios(): Result<List<Torneio>> {
        return runCatching {
            client.from("torneio")
                .select()
                .decodeList<Torneio>()
        }
    }

    suspend fun criarTorneio(torneio: Torneio): Result<Unit> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador não autenticado.")

            val novoTorneio = torneio.copy(
                idOrganizador = userId,
                estado = "aberto"
            )

            client.from("torneio").insert(novoTorneio)
        }
    }
}