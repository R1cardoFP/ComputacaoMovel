package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class AdminProfileRepository {

    private val client = SupabaseClient.client

    suspend fun carregarPerfilAtual(): Result<AdminProfile> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador autenticado não encontrado.")

            val perfil = client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<AdminProfileDto>()

            val dados = perfil.dadosPessoais

            AdminProfile(
                id = perfil.id,
                nome = perfil.nome,
                email = perfil.email,
                bio = dados["bio"]?.jsonPrimitive?.content ?: "",
                language = dados["language"]?.jsonPrimitive?.content ?: "English (US)",
                fotoUrl = perfil.fotoUrl
            )
        }
    }

    suspend fun atualizarPerfil(
        nome: String,
        email: String,
        bio: String,
        language: String
    ): Result<Unit> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador autenticado não encontrado.")

            val dadosPessoais = buildJsonObject {
                put("bio", bio)
                put("language", language)
            }

            val update = AdminProfileUpdate(
                nome = nome,
                email = email,
                dadosPessoais = dadosPessoais
            )

            client.from("utilizador")
                .update(update) {
                    filter {
                        eq("id", userId)
                    }
                }
        }
    }

    suspend fun logout(): Result<Unit> {
        return runCatching {
            client.auth.signOut()
        }
    }
}

data class AdminProfile(
    val id: String,
    val nome: String,
    val email: String,
    val bio: String,
    val language: String,
    val fotoUrl: String?
)

@Serializable
private data class AdminProfileDto(
    val id: String,
    val nome: String,
    val email: String,

    @SerialName("dados_pessoais")
    val dadosPessoais: JsonObject = buildJsonObject {},

    @SerialName("foto_url")
    val fotoUrl: String? = null
)

@Serializable
private data class AdminProfileUpdate(
    val nome: String,
    val email: String,

    @SerialName("dados_pessoais")
    val dadosPessoais: JsonObject
)