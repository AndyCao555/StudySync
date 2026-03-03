package com.example.kotlin_app.data

import com.example.kotlin_app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Supabase client configuration.
 * 
 * Credentials are loaded from local.properties (not committed to git).
 * 
 * To set up:
 * 1. Create local.properties in the project root (if it doesn't exist)
 * 2. Add these lines:
 *    SUPABASE_URL=https://your-project-id.supabase.co
 *    SUPABASE_ANON_KEY=your-anon-key-here
 * 
 * See local.properties.example for a template.
 */
object SupabaseClient {
    // Load from BuildConfig (which reads from local.properties)
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL
    private val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
    
    // Lazy initialization - only creates client when accessed, and handles errors gracefully
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
                android.util.Log.w("SupabaseClient", "URL: $SUPABASE_URL")
                android.util.Log.w("SupabaseClient", "Key length: ${SUPABASE_ANON_KEY.length}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("SupabaseClient", "Failed to initialize Supabase client", e)
            null
        }
    }
}
