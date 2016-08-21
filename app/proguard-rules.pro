# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/sdks/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified image_class name to the JavaScript interface
# image_class:
#-keepclassmembers image_class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Parse
-dontwarn com.parse.**
-keep class com.parse.** { *; }
-keep class com.fasterxml.jackson.databind.ObjectMapper { public <methods>; protected <methods>; }
-keep class com.fasterxml.jackson.databind.ObjectWriter { public ** writeValueAsString(**); }

#rules for design library
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#Rules for support library
-dontwarn android.support.v7.widget.**
-keep public class android.support.v7.widget.** { *; }

# Allow obfuscation of android.support.v7.internal.view.menu.**
# to avoid problem on Samsung 4.2.2 devices with appcompat v21
# see https://code.google.com/p/android/issues/detail?id=78377
-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}