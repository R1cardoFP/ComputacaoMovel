package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

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

    suspend fun logout(): Result<Unit> {
        return runCatching {
            client.auth.signOut()
        }
    }
}