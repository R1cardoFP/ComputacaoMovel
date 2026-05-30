package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AuthRepository {

    private val client = SupabaseClient.client

    suspend fun login(email: String, password: String): Result<Utilizador> {
        return runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador autenticado não encontrado.")

            client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Utilizador>()
        }
    }

    suspend fun registar(nome: String, email: String, password: String): Result<Utilizador> {
        return runCatching {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Conta criada, mas o utilizador autenticado não foi encontrado.")

            val novoUtilizador = NovoUtilizador(
                id = userId,
                username = email.substringBefore("@"),
                nome = nome,
                email = email
            )

            client.from("utilizador")
                .insert(novoUtilizador)

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

    suspend fun logout(): Result<Unit> {
        return runCatching {
            client.auth.signOut()
        }
    }

    // Função necessária para carregar os dados reais no ProfileScreen
    suspend fun obterUtilizadorAtual(): Result<Utilizador> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Nenhum utilizador logado.")

            client.from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Utilizador>()
        }
    }
}

@Serializable
private data class NovoUtilizador(
    val id: String,
    val username: String,
    val nome: String,
    val email: String,

    @SerialName("raio_km")
    val raioKm: Int = 25
)