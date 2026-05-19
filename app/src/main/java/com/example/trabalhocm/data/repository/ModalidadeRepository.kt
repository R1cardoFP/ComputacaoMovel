package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Modalidade
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

class ModalidadeRepository {

    private val client = SupabaseClient.client

    suspend fun listarModalidades(): Result<List<Modalidade>> {
        return runCatching {
            client.from("modalidade")
                .select()
                .decodeList<Modalidade>()
        }
    }
}