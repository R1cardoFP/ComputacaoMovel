package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class PlayerCalendarMatchInfo(
    val idJogo: Long,
    val torneioNome: String,
    val modalidadeNome: String,
    val data: String,
    val hora: String,
    val local: String,
    val estadoJogo: String,
    val equipaCasa: String,
    val equipaFora: String,
    val pontosCasa: Int,
    val pontosFora: Int
) {
    val dataLocal: LocalDate?
        get() = runCatching { LocalDate.parse(data.take(10)) }.getOrNull()

    val horaLocal: LocalTime?
        get() = runCatching { LocalTime.parse(hora.take(5)) }.getOrNull()

    val isLive: Boolean
        get() = estadoJogo.lowercase() == "em_direto"

    val isFinished: Boolean
        get() = estadoJogo.lowercase() == "terminado"
}

@Serializable
data class PlayerCalendarJogoDto(
    val id: Long,

    @SerialName("id_torneio")
    val idTorneio: Long? = null,

    val data: String,
    val hora: String,
    val local: String? = null,

    @SerialName("estado_jogo")
    val estadoJogo: String = "agendado",

    @SerialName("resultado_final")
    val resultadoFinal: String? = null
)

@Serializable
data class PlayerCalendarJogoEquipaDto(
    @SerialName("id_equipa")
    val idEquipa: Long,

    @SerialName("id_jogo")
    val idJogo: Long,

    @SerialName("papel_equipa")
    val papelEquipa: String,

    @SerialName("pontos_marcados")
    val pontosMarcados: Int = 0
)

@Serializable
data class PlayerCalendarEquipaDto(
    val id: Long,
    val nome: String,

    @SerialName("id_modalidade")
    val idModalidade: Long? = null
)

@Serializable
data class PlayerCalendarTorneioDto(
    val id: Long,
    val nome: String,
    val local: String? = null,

    @SerialName("id_modalidade")
    val idModalidade: Long? = null
)

@Serializable
data class PlayerCalendarModalidadeDto(
    val id: Long,
    val nome: String
)

class PlayerMatchCalendarRepository {

    private val client = SupabaseClient.client

    suspend fun listarJogosCalendario(): Result<List<PlayerCalendarMatchInfo>> {
        return runCatching {
            val jogos = client.from("jogo")
                .select()
                .decodeList<PlayerCalendarJogoDto>()

            val jogoEquipas = client.from("jogo_equipa")
                .select()
                .decodeList<PlayerCalendarJogoEquipaDto>()

            val equipas = client.from("equipa")
                .select()
                .decodeList<PlayerCalendarEquipaDto>()

            val torneios = client.from("torneio")
                .select()
                .decodeList<PlayerCalendarTorneioDto>()

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<PlayerCalendarModalidadeDto>()

            val equipasPorId = equipas.associateBy { it.id }
            val torneiosPorId = torneios.associateBy { it.id }
            val modalidadesPorId = modalidades.associateBy { it.id }

            jogos.mapNotNull { jogo ->
                val equipasDoJogo = jogoEquipas.filter { it.idJogo == jogo.id }

                val casa = equipasDoJogo.firstOrNull {
                    it.papelEquipa.lowercase() == "casa"
                }

                val fora = equipasDoJogo.firstOrNull {
                    it.papelEquipa.lowercase() == "fora"
                }

                val equipaCasa = casa?.let { equipasPorId[it.idEquipa] }
                val equipaFora = fora?.let { equipasPorId[it.idEquipa] }

                val torneio = jogo.idTorneio?.let { torneiosPorId[it] }

                val idModalidade =
                    torneio?.idModalidade
                        ?: equipaCasa?.idModalidade
                        ?: equipaFora?.idModalidade

                val modalidadeNome = idModalidade?.let {
                    modalidadesPorId[it]?.nome
                } ?: "Modalidade"

                PlayerCalendarMatchInfo(
                    idJogo = jogo.id,
                    torneioNome = torneio?.nome ?: "Match",
                    modalidadeNome = modalidadeNome,
                    data = jogo.data,
                    hora = jogo.hora,
                    local = jogo.local ?: torneio?.local ?: "Local por definir",
                    estadoJogo = jogo.estadoJogo,
                    equipaCasa = equipaCasa?.nome ?: "Home",
                    equipaFora = equipaFora?.nome ?: "Away",
                    pontosCasa = casa?.pontosMarcados ?: 0,
                    pontosFora = fora?.pontosMarcados ?: 0
                )
            }.sortedWith(
                compareBy<PlayerCalendarMatchInfo> {
                    it.dataLocal ?: LocalDate.MAX
                }.thenBy {
                    it.horaLocal ?: LocalTime.MAX
                }
            )
        }
    }
}