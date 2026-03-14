package com.example.kotlin_app.data

import com.example.kotlin_app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL
    private val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    val client: SupabaseClient? by lazy {
        try {
            if (SUPABASE_URL.isNotBlank() && SUPABASE_ANON_KEY.isNotBlank() && 
                SUPABASE_URL != "https://your-project-id.supabase.co" && 
                SUPABASE_ANON_KEY.isNotEmpty()) {
                createSupabaseClient(
                    supabaseUrl = SUPABASE_URL,
                    supabaseKey = SUPABASE_ANON_KEY
                ) {
                    install(Postgrest)
                    install(Realtime)
                }
            } else {
                android.util.Log.w("SupabaseClient", "Supabase credentials not configured. App will work offline only.")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("SupabaseClient", "Failed to initialize Supabase client", e)
            null
        }
    }
}
