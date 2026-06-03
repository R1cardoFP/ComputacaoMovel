package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminUser
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.OffsetDateTime

class AdminUserRepository {

    private val client = SupabaseClient.client

    suspend fun listarUtilizadores(): Result<List<AdminUser>> {
        return runCatching {
            val utilizadores = client.from("utilizador")
                .select()
                .decodeList<UtilizadorAdminDto>()

            val utilizadorPapeis = client.from("utilizador_papel")
                .select()
                .decodeList<UtilizadorPapelDto>()

            val papeis = client.from("papel")
                .select()
                .decodeList<PapelDto>()

            utilizadores.map { utilizador ->
                val papeisDoUtilizador = utilizadorPapeis
                    .filter { it.idUtilizador == utilizador.id }
                    .mapNotNull { utilizadorPapel ->
                        papeis.firstOrNull { it.id == utilizadorPapel.idPapel }?.nomePapel
                    }

                val papelPrincipal = escolherPapelPrincipal(papeisDoUtilizador)

                AdminUser(
                    id = utilizador.id,
                    nome = utilizador.nome,
                    email = utilizador.email,
                    role = mapRoleToUi(papelPrincipal),
                    status = formatLastLogin(utilizador.ultimoLogin ?: utilizador.createdAt)
                )
            }.sortedBy { it.nome.lowercase() }
        }
    }

    suspend fun tornarAdministrador(userId: String): Result<Unit> {
        return adicionarPapel(userId = userId, nomePapel = "Administrador")
    }

    suspend fun tornarOrganizador(userId: String): Result<Unit> {
        return adicionarPapel(userId = userId, nomePapel = "Organizador")
    }

    suspend fun removerAdministrador(userId: String): Result<Unit> {
        return removerPapel(userId = userId, nomePapel = "Administrador")
    }

    suspend fun removerOrganizador(userId: String): Result<Unit> {
        return removerPapel(userId = userId, nomePapel = "Organizador")
    }

    suspend fun apagarUtilizador(userId: String): Result<Unit> {
        return runCatching {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador autenticado não encontrado.")

            if (currentUserId == userId) {
                throw Exception("Não podes apagar a tua própria conta.")
            }

            client.from("utilizador_papel")
                .delete {
                    filter {
                        eq("id_utilizador", userId)
                    }
                }

            client.from("utilizador")
                .delete {
                    filter {
                        eq("id", userId)
                    }
                }
        }
    }

    private suspend fun adicionarPapel(userId: String, nomePapel: String): Result<Unit> {
        return runCatching {
            val papel = obterPapelPorNome(nomePapel)

            val papeisAtuais = client.from("utilizador_papel")
                .select {
                    filter {
                        eq("id_utilizador", userId)
                        eq("id_papel", papel.id)
                    }
                }
                .decodeList<UtilizadorPapelDto>()

            if (papeisAtuais.isEmpty()) {
                client.from("utilizador_papel")
                    .insert(
                        NovoUtilizadorPapelDto(
                            idUtilizador = userId,
                            idPapel = papel.id
                        )
                    )
            }
        }
    }

    private suspend fun removerPapel(userId: String, nomePapel: String): Result<Unit> {
        return runCatching {
            val papel = obterPapelPorNome(nomePapel)

            client.from("utilizador_papel")
                .delete {
                    filter {
                        eq("id_utilizador", userId)
                        eq("id_papel", papel.id)
                    }
                }
        }
    }

    private suspend fun obterPapelPorNome(nomePapel: String): PapelDto {
        return client.from("papel")
            .select {
                filter {
                    eq("nome_papel", nomePapel)
                }
            }
            .decodeSingle<PapelDto>()
    }

    private fun escolherPapelPrincipal(papeis: List<String>): String {
        return when {
            papeis.contains("Administrador") -> "Administrador"
            papeis.contains("Organizador") -> "Organizador"
            papeis.contains("Jogador/Atleta") -> "Jogador/Atleta"
            else -> "Jogador/Atleta"
        }
    }

    private fun mapRoleToUi(role: String): String {
        return when (role) {
            "Administrador" -> "ADMINISTRATOR"
            "Organizador" -> "ORGANIZER"
            "Jogador/Atleta" -> "PLAYER"
            else -> role.uppercase()
        }
    }

    private fun formatLastLogin(ultimoLogin: String?): String {
        if (ultimoLogin.isNullOrBlank()) {
            return "Last active unknown"
        }

        return try {
            val lastLogin = OffsetDateTime.parse(ultimoLogin)
            val now = OffsetDateTime.now()
            val duration = Duration.between(lastLogin, now)

            when {
                duration.toMinutes() < 1 -> "Active Now"
                duration.toMinutes() < 60 -> "Last active ${duration.toMinutes()}m ago"
                duration.toHours() < 24 -> "Last active ${duration.toHours()}h ago"
                duration.toDays() == 1L -> "Last active 1d ago"
                else -> "Last active ${duration.toDays()}d ago"
            }
        } catch (e: Exception) {
            "Last active unknown"
        }
    }
}

@Serializable
private data class UtilizadorAdminDto(
    val id: String,
    val nome: String,
    val email: String,

    @SerialName("ultimo_login")
    val ultimoLogin: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
private data class UtilizadorPapelDto(
    @SerialName("id_utilizador")
    val idUtilizador: String,

    @SerialName("id_papel")
    val idPapel: Int
)

@Serializable
private data class PapelDto(
    val id: Int,

    @SerialName("nome_papel")
    val nomePapel: String
)

@Serializable
private data class NovoUtilizadorPapelDto(
    @SerialName("id_utilizador")
    val idUtilizador: String,

    @SerialName("id_papel")
    val idPapel: Int
)