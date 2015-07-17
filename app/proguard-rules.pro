# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/jake/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-dontwarn com.squareup.okhttp.**
-keep class com.github.mikephil.charting.** { *; }
-dontwarn butterknife.Views$InjectViewProcessor
-keep class my.package.DbModel { *; }
-dontshrink
-dontoptimize
-keep public class org.stuartresearch.snapzuisfunner.xx.Profile extends SugarRecord{*;}
-keep public class org.stuartresearch.snapzuisfunner.XX.Profile extends SugarApp{*;}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn icepick.**
-keep class **$$Icicle { *; }
-keepnames class * { @icepick.Icicle *;}
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keep class org.** { *; }
