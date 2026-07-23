# ZDocs ProGuard Rules

# Keep data models
-keepclassmembers class com.zdocs.data.model.** {
    *;
}

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Compose
-dontwarn androidx.compose.**
