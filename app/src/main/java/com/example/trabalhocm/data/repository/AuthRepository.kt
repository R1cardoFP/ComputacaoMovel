package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.OffsetDateTime

class AuthRepository {

    private val client = SupabaseClient.client

    suspend fun login(identificador: String, password: String): Result<Utilizador> {
        return runCatching {
            // Verifica se inseriu um email ou um username
            val emailParaLogin = if (identificador.contains("@")) {
                identificador
            } else {
                // Vai procurar o email na base de dados pelo username
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

            // Faz o login nativo com a Supabase
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
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password

                data = buildJsonObject {
                    put("nome", nome)
                    put("username", username)
                }
            }

            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Conta criada, mas o utilizador autenticado não foi encontrado. Verifica se a confirmação por email está ativa na Supabase.")

            client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Utilizador>()
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
}

@Serializable
private data class AtualizarUltimoLogin(
    @SerialName("ultimo_login")
    val ultimoLogin: String
)