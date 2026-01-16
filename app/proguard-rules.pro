########################################
# Kotlin Serialization
########################################
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}

########################################
# Supabase Core
########################################
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

########################################
# Supabase Realtime
########################################
-keep class io.github.jan.supabase.realtime.** { *; }
-dontwarn io.github.jan.supabase.realtime.**

########################################
# Supabase PostgREST
########################################
-keep class io.github.jan.supabase.postgrest.** { *; }
-dontwarn io.github.jan.supabase.postgrest.**

########################################
# Coroutines / Flow
########################################
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

########################################
# UUID (used heavily in decoding)
########################################
-keep class java.util.UUID { *; }

########################################
# HandyHood app models + repositories
########################################
-keep class com.example.handyhood.data.** { *; }
-keep class com.example.handyhood.ui.** { *; }

########################################
# General JSON safety
########################################
-dontwarn sun.misc.**
