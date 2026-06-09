# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.pdfscan.app.** { *; }

# Google Play Services / ML Kit Document Scanner
-keep class com.google.android.gms.** { *; }
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.android.gms.**

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Coil
-dontwarn coil.**
-keep class coil.** { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# AndroidX Lifecycle
-keep class androidx.lifecycle.** { *; }

# Keep Compose runtime
-dontwarn androidx.compose.**
