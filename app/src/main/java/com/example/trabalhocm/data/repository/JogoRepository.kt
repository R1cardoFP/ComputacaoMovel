package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.EventoJogo
import com.example.trabalhocm.data.model.Jogo
import com.example.trabalhocm.data.model.JogoEquipa
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

class JogoRepository {
    private val client = SupabaseClient.client

    suspend fun listarJogos(): Result<List<Jogo>> = runCatching {
        client.from("jogo").select().decodeList<Jogo>()
    }

    suspend fun listarJogoEquipas(): Result<List<JogoEquipa>> = runCatching {
        client.from("jogo_equipa").select().decodeList<JogoEquipa>()
    }

    suspend fun listarMeusEventos(userId: String): Result<List<EventoJogo>> = runCatching {
        client.from("evento_jogo")
            .select { filter { eq("id_jogador", userId) } }
            .decodeList<EventoJogo>()
    }
}