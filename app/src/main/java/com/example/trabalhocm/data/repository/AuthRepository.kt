package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.OtpType
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
            // 1. Guardamos a resposta do Supabase numa variável em vez de chamar só por chamar
            val authResponse = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password

                data = buildJsonObject {
                    put("nome", nome)
                    put("username", username)
                }
            }

            // 2. VERIFICAÇÃO ANTI "FAKE SUCCESS" DO SUPABASE
            // Se o Supabase devolver a lista 'identities' vazia num registo,
            // significa que o email já existe, mas ele está a esconder o erro por segurança.
            if (authResponse?.identities?.isEmpty() == true) {
                throw Exception("already registered")
            }

            // ATENÇÃO: Como agora ativaste a confirmação por email (OTP),
            // a Supabase NÃO faz o login automático aqui.
            // Para a app não bloquear e conseguir avançar para o ecrã dos 6 dígitos,
            // enviamos um Utilizador "temporário". O verdadeiro é devolvido após inserir o código!
            Utilizador(
                id = "pendente",
                username = username,
                nome = nome,
                email = email
            )
        }
    }

    // --- NOVA FUNÇÃO: VERIFICAR CÓDIGO DE 6 DÍGITOS ---
    suspend fun verificarCodigoRegisto(email: String, codigo: String): Result<Utilizador> {
        return runCatching {
            // 1. Envia o código à Supabase para confirmar
            client.auth.verifyEmailOtp(
                type = OtpType.Email.SIGNUP,
                email = email,
                token = codigo
            )

            // 2. Se o código estiver certo, o login é feito automaticamente. Vamos buscar os dados:
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

    // --- NOVA FUNÇÃO: ATUALIZAR NOME DE UTILIZADOR (PROFILE) ---
    suspend fun atualizarPerfil(username: String, bio: String): Result<Unit> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador não autenticado.")

            client.from("utilizador")
                .update(
                    AtualizarPerfilRequest(
                        username = username
                    )
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
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

// CLASSES AUXILIARES PARA ENVIAR DADOS PARA A BASE DE DADOS
@Serializable
private data class AtualizarUltimoLogin(
    @SerialName("ultimo_login")
    val ultimoLogin: String
)

@Serializable
private data class AtualizarPerfilRequest(
    val username: String
)