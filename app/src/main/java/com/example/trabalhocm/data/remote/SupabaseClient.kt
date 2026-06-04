package com.example.trabalhocm.data.remote

import com.example.trabalhocm.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)

        install(ComposeAuth) {
            googleNativeLogin(serverClientId = "479970332367-oupvb78mgrbotfcrfrtj8ko73thjnt0i.apps.googleusercontent.com")
        }
    }
}