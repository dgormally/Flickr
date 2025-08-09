# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/amitshekhar/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep model classes used by Gson
-keep class com.davidg.syncinteractive.test.data.model.api.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# Dagger
-dontwarn dagger.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }

# Glide
-dontwarn com.bumptech.glide.**
