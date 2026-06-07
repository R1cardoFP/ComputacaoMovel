package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.model.EstatisticaJogador
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.OffsetDateTime

class AuthRepository {

    private val client = SupabaseClient.client

    suspend fun login(identificador: String, password: String): Result<Utilizador> {
        return runCatching {
            val emailParaLogin = if (identificador.contains("@")) {
                identificador
            } else {
                val utilizadores = client.from("utilizador")
                    .select {
                        filter {
                            eq("username", identificador)
                        }
                    }
                    .decodeList<Utilizador>()

                if (utilizadores.isEmpty()) {
                    throw Exception("Username não encontrado.")
                }
                utilizadores.first().email
            }

            client.auth.signInWith(Email) {
                this.email = emailParaLogin
                this.password = password
            }

            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador autenticado não encontrado.")

            client.from("utilizador")
                .update(
                    AtualizarUltimoLogin(
                        ultimoLogin = OffsetDateTime.now().toString()
                    )
                ) {
                    filter {
                        eq("id", userId)
                    }
                }

            client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Utilizador>()
        }
    }

    suspend fun registar(
        nome: String,
        username: String,
        email: String,
        password: String
    ): Result<Utilizador> {
        return runCatching {
            val usernameExists = client.from("utilizador")
                .select {
                    filter {
                        eq("username", username)
                    }
                }
                .decodeList<Utilizador>()

            if (usernameExists.isNotEmpty()) {
                throw Exception("This username is already taken.")
            }

            val emailExists = client.from("utilizador")
                .select {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeList<Utilizador>()

            if (emailExists.isNotEmpty()) {
                throw Exception("This email address is already registered.")
            }

            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password

                data = buildJsonObject {
                    put("nome", nome)
                    put("username", username)
                }
            }

            Utilizador(
                id = "pendente",
                username = username,
                nome = nome,
                email = email
            )
        }
    }

    suspend fun verificarCodigoRegisto(email: String, codigo: String): Result<Utilizador> {
        return runCatching {
            client.auth.verifyEmailOtp(
                type = OtpType.Email.SIGNUP,
                email = email,
                token = codigo
            )

            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Erro: Utilizador não encontrado após verificação.")

            client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Utilizador>()
        }
    }

    suspend fun sincronizarUtilizadorGoogle(): Result<Unit> {
        return runCatching {
            val user = client.auth.currentUserOrNull()
                ?: throw Exception("Utilizador Google não autenticado.")

            val utilizadores = client.from("utilizador")
                .select { filter { eq("id", user.id) } }
                .decodeList<Utilizador>()

            if (utilizadores.isEmpty()) {
                val nomeGoogle = user.userMetadata?.get("full_name")?.toString()?.removeSurrounding("\"") ?: "Novo Jogador"
                val emailGoogle = user.email ?: ""

                val baseUsername = emailGoogle.substringBefore("@").replace(".", "").lowercase()
                val usernameUnico = "${baseUsername}_${(1000..9999).random()}"

                val novoUtilizador = Utilizador(
                    id = user.id,
                    username = usernameUnico,
                    nome = nomeGoogle,
                    email = emailGoogle
                )

                client.from("utilizador").insert(novoUtilizador)
            }
        }
    }

    suspend fun atualizarPerfil(username: String, bio: String): Result<Unit> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador não autenticado.")

            client.from("utilizador")
                .update(
                    AtualizarPerfilRequest(
                        username = username,
                        bio = bio
                    )
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
        }
    }

    suspend fun atualizarFotoPerfil(imageBytes: ByteArray): Result<String> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador não autenticado.")

            val path = "avatar_$userId.jpg"

            client.storage.from("avatars").upload(path, imageBytes) {
                upsert = true
            }

            val publicUrl = client.storage.from("avatars").publicUrl(path)

            client.from("utilizador").update(AtualizarFotoRequest(fotoUrl = publicUrl)) {
                filter { eq("id", userId) }
            }

            publicUrl
        }
    }

    suspend fun obterEstatisticasJogador(userId: String): Result<List<EstatisticaJogador>> {
        return runCatching {
            client.from("estatistica_jogador")
                .select {
                    filter {
                        eq("id_utilizador", userId)
                    }
                }
                .decodeList<EstatisticaJogador>()
        }
    }

    suspend fun obterPapeisUtilizador(userId: String): Result<List<Int>> {
        return runCatching {
            client.from("utilizador_papel")
                .select {
                    filter {
                        eq("id_utilizador", userId)
                    }
                }
                .decodeList<UtilizadorPapel>()
                .map { it.idPapel } // Devolve apenas a lista dos números (ex: [2, 3])
        }
    }

    suspend fun recuperarPassword(email: String): Result<Unit> {
        return runCatching {
            client.auth.resetPasswordForEmail(email = email)
        }
    }

    suspend fun alterarPassword(passwordAtual: String, novaPassword: String): Result<Unit> {
        return runCatching {
            val emailAtual = client.auth.currentUserOrNull()?.email
                ?: throw Exception("Não foi possível obter o email do utilizador atual.")

            client.auth.signInWith(Email) {
                this.email = emailAtual
                this.password = passwordAtual
            }

            client.auth.updateUser {
                password = novaPassword
            }
        }
    }

    suspend fun obterUtilizadorAtual(): Result<Utilizador> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Nenhum utilizador com sessão iniciada.")

            client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Utilizador>()
        }
    }

    suspend fun logout(): Result<Unit> {
        return runCatching {
            client.auth.signOut()
        }
    }

    suspend fun obterNotificacoes(userId: String): Result<List<Notificacao>> {
        return runCatching {
            client.from("notificacoes")
                .select {
                    filter {
                        eq("id_utilizador", userId)
                    }
                }
                .decodeList<Notificacao>()
                .sortedByDescending { it.data }
        }
    }

    suspend fun obterTorneios(): Result<List<Torneio>> {
        return runCatching {
            client.from("torneio")
                .select()
                .decodeList<Torneio>()
        }
    }

    suspend fun obterTorneioDetalhes(idTorneio: Long): Result<Torneio> {
        return runCatching {
            client.from("torneio")
                .select {
                    filter {
                        eq("id", idTorneio)
                    }
                }
                .decodeSingle<Torneio>()
        }
    }

    suspend fun obterHistoricoTorneios(userId: String): Result<List<Torneio>> {
        return runCatching {
            val equipas = client.from("membro_equipa")
                .select {
                    filter {
                        eq("id_utilizador", userId)
                        eq("estado_convite", "aceite")
                    }
                }
                .decodeList<MembroEquipa>()

            val idsEquipas = equipas.map { it.idEquipa }
            val idsTorneios = mutableSetOf<Long>()

            if (idsEquipas.isNotEmpty()) {
                val inscricoes = client.from("inscricao")
                    .select {
                        filter { isIn("id_equipa", idsEquipas) }
                    }
                    .decodeList<InscricaoEquipa>()
                idsTorneios.addAll(inscricoes.map { it.idTorneio })
            }

            val torneiosOrganizados = client.from("torneio")
                .select {
                    filter { eq("id_organizador", userId) }
                }
                .decodeList<Torneio>()

            idsTorneios.addAll(torneiosOrganizados.map { it.id })

            if (idsTorneios.isEmpty()) {
                return@runCatching emptyList()
            }

            client.from("torneio")
                .select {
                    // --- CORREÇÃO AQUI: isIn em vez de inAny ---
                    filter { isIn("id", idsTorneios.toList()) }
                }
                .decodeList<Torneio>()
                .sortedByDescending { it.dataInicio ?: "" }
        }
    }
}

// CLASSES AUXILIARES PARA ENVIAR DADOS PARA A BASE DE DADOS
@Serializable
private data class AtualizarUltimoLogin(
    @SerialName("ultimo_login")
    val ultimoLogin: String
)

@Serializable
private data class AtualizarPerfilRequest(
    val username: String,
    val bio: String
)

@Serializable
private data class AtualizarFotoRequest(
    @SerialName("foto_url")
    val fotoUrl: String
)

@Serializable
private data class UtilizadorPapel(
    @SerialName("id_utilizador") val idUtilizador: String,
    @SerialName("id_papel") val idPapel: Int
)

@Serializable
private data class MembroEquipa(
    @SerialName("id_equipa") val idEquipa: Long,
    @SerialName("estado_convite") val estadoConvite: String? = null
)

@Serializable
private data class InscricaoEquipa(
    @SerialName("id_torneio") val idTorneio: Long
)

@Serializable
data class Notificacao(
    val id: Long = 0,
    @SerialName("id_utilizador") val idUtilizador: String,
    val titulo: String,
    val mensagem: String,
    val tipo: String,
    val data: String,
    val lida: Boolean = false
)

@Serializable
data class Torneio(
    val id: Long = 0,
    val nome: String,
    val descricao: String? = null,
    val regras: String? = null,
    val local: String? = null,
    @SerialName("data_inicio") val dataInicio: String? = null,
    @SerialName("data_fim") val dataFim: String? = null,
    val formato: String? = null,
    @SerialName("taxa_inscricao") val taxaInscricao: Double? = null,
    val premio: Double? = null,
    val estado: String? = null,
    @SerialName("id_organizador") val idOrganizador: String? = null,
    @SerialName("id_modalidade") val idModalidade: Int? = null
)