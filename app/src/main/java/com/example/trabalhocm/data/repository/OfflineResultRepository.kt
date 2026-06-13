package com.example.trabalhocm.data.repository

import android.content.Context
import com.example.trabalhocm.data.local.AppDatabase
import com.example.trabalhocm.data.local.offline.OfflineResultEntity
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class OfflineResultRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).offlineResultDao()
    private val client = SupabaseClient.client

    val resultadosPendentes: Flow<List<OfflineResultEntity>> =
        dao.observarPendentes()

    suspend fun guardarResultadoOffline(
        nomeJogo: String,
        resultadoEquipaA: Int,
        resultadoEquipaB: Int,
        observacoes: String
    ): Result<Unit> {
        return runCatching {
            dao.inserir(
                OfflineResultEntity(
                    nomeJogo = nomeJogo,
                    resultadoEquipaA = resultadoEquipaA,
                    resultadoEquipaB = resultadoEquipaB,
                    observacoes = observacoes
                )
            )
        }
    }

    suspend fun sincronizarResultadosPendentes(): Result<Int> {
        return runCatching {
            val pendentes = dao.listarPendentesUmaVez()
            var totalSincronizados = 0

            pendentes.forEach { resultado ->
                client.from("resultado_offline").insert(
                    ResultadoOfflineInsert(
                        nomeJogo = resultado.nomeJogo,
                        resultadoEquipaA = resultado.resultadoEquipaA,
                        resultadoEquipaB = resultado.resultadoEquipaB,
                        observacoes = resultado.observacoes.ifBlank { null }
                    )
                )

                dao.marcarComoSincronizado(resultado.idLocal)
                totalSincronizados++
            }

            totalSincronizados
        }
    }

    suspend fun removerResultadoPendente(idLocal: Long): Result<Unit> {
        return runCatching {
            dao.remover(idLocal)
        }
    }
}

@Serializable
private data class ResultadoOfflineInsert(
    @SerialName("nome_jogo")
    val nomeJogo: String,

    @SerialName("resultado_equipa_a")
    val resultadoEquipaA: Int,

    @SerialName("resultado_equipa_b")
    val resultadoEquipaB: Int,

    val observacoes: String? = null
)