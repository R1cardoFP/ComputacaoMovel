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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
                    throw Exception("Username not found.")
                }

                utilizadores.first().email
            }

            client.auth.signInWith(Email) {
                this.email = emailParaLogin
                this.password = password
            }

            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Authenticated user not found.")

            val utilizadorJson = client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<JsonObject>()

            val suspended = utilizadorJson.nestedBoolean("dados_pessoais", "suspended") ?: false
            val deleted = utilizadorJson.nestedBoolean("dados_pessoais", "deleted") ?: false
            val accountStatus = utilizadorJson.nestedText("dados_pessoais", "account_status")

            if (suspended || accountStatus == "suspended") {
                client.auth.signOut()
                throw Exception("Your account is suspended. Please contact the administrator.")
            }

            if (deleted || accountStatus == "deleted") {
                client.auth.signOut()
                throw Exception("This account has been deleted. Please contact the administrator.")
            }

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
                throw Exception("already exists")
            }

            val emailExists = client.from("utilizador")
                .select {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeList<Utilizador>()

            if (emailExists.isNotEmpty()) {
                throw Exception("already registered")
            }

            val authResponse = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password

                data = buildJsonObject {
                    put("nome", nome)
                    put("username", username)
                }
            }

            if (authResponse?.identities?.isEmpty() == true) {
                throw Exception("already registered")
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
                ?: throw Exception("Error: User not found after verification.")

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
                ?: throw Exception("Google user not authenticated.")

            val utilizadores = client.from("utilizador")
                .select { filter { eq("id", user.id) } }
                .decodeList<Utilizador>()

            if (utilizadores.isEmpty()) {
                val nomeGoogle = user.userMetadata?.get("full_name")?.toString()?.removeSurrounding("\"") ?: "New Player"
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
                ?: throw Exception("User not authenticated.")

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
                ?: throw Exception("User not authenticated.")

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
                .map { it.idPapel }
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
                ?: throw Exception("Could not retrieve the current user's email.")

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
                ?: throw Exception("No user is currently logged in.")

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
                    filter { isIn("id", idsTorneios.toList()) }
                }
                .decodeList<Torneio>()
                .sortedByDescending { it.dataInicio ?: "" }
        }
    }

    private fun JsonObject.nestedText(objectKey: String, vararg keys: String): String {
        val obj = this[objectKey]?.jsonObject ?: return ""

        keys.forEach { key ->
            val value = obj[key]
                ?.jsonPrimitive
                ?.contentOrNull

            if (!value.isNullOrBlank()) {
                return value
            }
        }

        return ""
    }

    private fun JsonObject.nestedBoolean(objectKey: String, vararg keys: String): Boolean? {
        val obj = this[objectKey]?.jsonObject ?: return null

        keys.forEach { key ->
            val primitive = obj[key]?.jsonPrimitive

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