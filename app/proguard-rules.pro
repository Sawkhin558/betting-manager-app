# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keepclassmembers class * {
    @androidx.room.* *;
}

# Hilt
-keep class * extends dagger.hilt.android.internal.earlyentrypoint.HiltWrapper_HiltComponents_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.earlyentrypoint.HiltWrapper_HiltComponents_ActivityComponentManager_ActivityComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.earlyentrypoint.HiltWrapper_HiltComponents_ViewComponentManager_ViewComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.earlyentrypoint.HiltWrapper_HiltComponents_ViewModelComponentManager_ViewModelComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.earlyentrypoint.HiltWrapper_HiltComponents_ServiceComponentManager_ServiceComponentBuilderEntryPoint { *; }

# Dagger
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection

# Coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.reflect.** { *; }

# Material3
-keep class com.google.android.material.** { *; }

# Serialization
-keep class kotlinx.serialization.** { *; }

# ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# Navigation
-keep class androidx.navigation.** { *; }

# DataStore
-keep class androidx.datastore.** { *; }

# Coil
-keep class coil.** { *; }

# Keep annotations
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep application class
-keep public class * extends android.app.Application

# Keep activity classes
-keep public class * extends android.app.Activity

# Keep all public classes, methods, and fields that might be accessed via reflection
-keepclasseswithmembers public class * {
    public <init>(...);
}

# Keep all enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep R classes
-keep class **.R$* {
    *;
}

# Keep resource classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom view classes
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep onClick listeners
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Optimizations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively
-dontpreverify
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose