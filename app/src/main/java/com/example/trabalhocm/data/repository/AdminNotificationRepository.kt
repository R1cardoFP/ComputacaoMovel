package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminNotification
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.text.Normalizer
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdminNotificationRepository {

    private val client = SupabaseClient.client
    private val zonaPortugal = ZoneId.of("Europe/Lisbon")

    suspend fun listarNotificacoes(): Result<List<AdminNotification>> {
        return runCatching {
            val notificacoes = client.from("notificacao_admin")
                .select()
                .decodeList<JsonObject>()

            val utilizadores = runCatching {
                client.from("utilizador")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val torneios = runCatching {
                client.from("torneio")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val equipas = runCatching {
                client.from("equipa")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            notificacoes
                .sortedByDescending { it.text("criada_em", "created_at") }
                .map { notificacao ->
                    val titulo = notificacao.text("titulo", "title").ifBlank { "Notification" }
                    val descricao = notificacao.text("descricao", "description")
                    val acaoTexto = notificacao.text("acao_texto", "action_text").takeIf { it.isNotBlank() }
                    val criadaEm = notificacao.text("criada_em", "created_at")

                    val idUtilizadorDireto = notificacao.text(
                        "id_utilizador",
                        "user_id",
                        "id_jogador",
                        "player_id",
                        "id_organizador",
                        "organizer_id",
                        "utilizador_id"
                    ).takeIf { it.isNotBlank() }

                    val idTorneioDireto = notificacao.text(
                        "id_torneio",
                        "tournament_id",
                        "torneio_id"
                    ).takeIf { it.isNotBlank() }

                    val idEquipaDireto = notificacao.text(
                        "id_equipa",
                        "team_id",
                        "equipa_id"
                    ).takeIf { it.isNotBlank() }

                    val idUtilizadorFinal = idUtilizadorDireto
                        ?: inferirUtilizador(titulo, descricao, utilizadores)

                    val idTorneioFinal = idTorneioDireto
                        ?: inferirTorneio(titulo, descricao, torneios)

                    val idEquipaFinal = idEquipaDireto
                        ?: inferirEquipa(titulo, descricao, equipas)

                    AdminNotification(
                        id = notificacao.text("id"),
                        title = titulo,
                        description = descricao,
                        type = notificacao.text("tipo", "type").ifBlank { "SYSTEM" },
                        actionText = acaoTexto,
                        unread = !(notificacao.booleanValue("lida", "read") ?: false),
                        timeText = formatTime(criadaEm),
                        createdAt = criadaEm,
                        userId = idUtilizadorFinal,
                        tournamentId = idTorneioFinal,
                        teamId = idEquipaFinal
                    )
                }
        }
    }

    suspend fun marcarComoLida(id: String): Result<Unit> {
        return runCatching {
            client.from("notificacao_admin")
                .update(
                    mapOf("lida" to true)
                ) {
                    filter {
                        eq("id", id)
                    }
                }

            Unit
        }
    }

    private fun inferirUtilizador(
        titulo: String,
        descricao: String,
        utilizadores: List<JsonObject>
    ): String? {
        val texto = normalizarTexto("$titulo $descricao")

        return utilizadores
            .mapNotNull { utilizador ->
                val id = utilizador.text("id")
                if (id.isBlank()) return@mapNotNull null

                val nomeOriginal = utilizador.text("nome", "name")
                val usernameOriginal = utilizador.text("username")
                val emailOriginal = utilizador.text("email")

                val nome = normalizarTexto(nomeOriginal)
                val username = normalizarTexto(usernameOriginal)
                val email = normalizarTexto(emailOriginal)

                val partesNome = nome
                    .split(" ")
                    .filter { it.length >= 2 }

                val primeiroNome = partesNome.firstOrNull().orEmpty()
                val ultimoNome = partesNome.lastOrNull().orEmpty()

                val inicialUltimoNome = if (primeiroNome.isNotBlank() && ultimoNome.isNotBlank()) {
                    "${primeiroNome.first()}. $ultimoNome"
                } else {
                    ""
                }

                val score = when {
                    nome.isNotBlank() && texto.contains(nome) -> 100
                    email.isNotBlank() && texto.contains(email) -> 95
                    username.isNotBlank() && texto.contains(username) -> 90
                    inicialUltimoNome.isNotBlank() && texto.contains(inicialUltimoNome) -> 85
                    primeiroNome.length >= 3 && ultimoNome.length >= 3 &&
                            texto.contains(primeiroNome) && texto.contains(ultimoNome) -> 80
                    primeiroNome.length >= 3 && texto.contains(primeiroNome) -> 50
                    ultimoNome.length >= 3 && texto.contains(ultimoNome) -> 45
                    else -> 0
                }

                if (score > 0) {
                    id to score
                } else {
                    null
                }
            }
            .maxByOrNull { it.second }
            ?.first
    }

    private fun inferirTorneio(
        titulo: String,
        descricao: String,
        torneios: List<JsonObject>
    ): String? {
        val texto = normalizarTexto("$titulo $descricao")

        return torneios
            .mapNotNull { torneio ->
                val id = torneio.text("id")
                if (id.isBlank()) return@mapNotNull null

                val nomeOriginal = torneio.text("nome", "name")
                val nome = normalizarTexto(nomeOriginal)

                val palavras = nome
                    .split(" ")
                    .filter { it.length >= 3 }

                val score = when {
                    nome.isNotBlank() && texto.contains(nome) -> 100
                    palavras.size >= 2 && palavras.all { texto.contains(it) } -> 80
                    palavras.any { texto.contains(it) } -> 45
                    else -> 0
                }

                if (score > 0) {
                    id to score
                } else {
                    null
                }
            }
            .maxByOrNull { it.second }
            ?.first
    }

    private fun inferirEquipa(
        titulo: String,
        descricao: String,
        equipas: List<JsonObject>
    ): String? {
        val texto = normalizarTexto("$titulo $descricao")

        return equipas
            .mapNotNull { equipa ->
                val id = equipa.text("id")
                if (id.isBlank()) return@mapNotNull null

                val nomeOriginal = equipa.text("nome", "name")
                val nome = normalizarTexto(nomeOriginal)

                val palavras = nome
                    .split(" ")
                    .filter { it.length >= 3 }

                val score = when {
                    nome.isNotBlank() && texto.contains(nome) -> 100
                    palavras.size >= 2 && palavras.all { texto.contains(it) } -> 80
                    palavras.any { texto.contains(it) } -> 45
                    else -> 0
                }

                if (score > 0) {
                    id to score
                } else {
                    null
                }
            }
            .maxByOrNull { it.second }
            ?.first
    }

    private fun formatTime(data: String): String {
        return try {
            val dataHora = parseDataSupabase(data)
            val formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale("pt", "PT"))

            dataHora.format(formatter)
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    private fun parseDataSupabase(data: String): LocalDateTime {
        return try {
            OffsetDateTime
                .parse(data)
                .atZoneSameInstant(zonaPortugal)
                .toLocalDateTime()
        } catch (e1: Exception) {
            try {
                Instant
                    .parse(data)
                    .atZone(zonaPortugal)
                    .toLocalDateTime()
            } catch (e2: Exception) {
                LocalDateTime.parse(data)
            }
        }
    }

    private fun normalizarTexto(texto: String): String {
        val semAcentos = Normalizer
            .normalize(texto, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")

        return semAcentos.lowercase(Locale.ROOT)
    }

    private fun JsonObject.text(vararg keys: String): String {
        keys.forEach { key ->
            val value = this[key]
                ?.jsonPrimitive
                ?.contentOrNull

            if (!value.isNullOrBlank()) {
                return value
            }
        }

        return ""
    }

    private fun JsonObject.booleanValue(vararg keys: String): Boolean? {
        keys.forEach { key ->
            val primitive = this[key]?.jsonPrimitive

            val direct = primitive?.booleanOrNull
            if (direct != null) {
                return direct
            }

            val fromText = primitive
                ?.contentOrNull
                ?.toBooleanStrictOrNull()

            if (fromText != null) {
                return fromText
            }
        }

        return null
    }
}