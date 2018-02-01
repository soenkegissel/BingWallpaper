# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in d:\Programs\sdk\android/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#
## Keeps line numbers and file name obfuscation
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
#
#
#-keepnames public class * extends com.supercilex.robotscouter.util.ui.FragmentBase
#
#-keep class com.github.liaoheng.** {*;}
#-keep class me.zhanghai.** {*;}
#-keep class com.fasterxml.jackson.** {*;}
#-keepnames class com.fasterxml.jackson.** { *; }
#-dontwarn com.fasterxml.jackson.**
#
#-keep class java.util.concurrent.** {*;}
#-dontwarn java.util.concurrent.**
#
## Extensions may require methods unused in the core app
#-keep class org.jsoup.** { *; }
#-keep class kotlin.** { *; }
#-keep class okhttp3.** { *; }
#-keep class com.google.gson.** { *; }
#-keep class com.github.salomonbrys.kotson.** { *; }
#
## OkHttp
#-dontwarn okhttp3.**
#-dontwarn okio.**
#-dontwarn javax.annotation.**
#-dontwarn retrofit2.Platform$Java8
#
## rxjava
#-keep class rx.schedulers.Schedulers {
#    public static <methods>;
#}
#-keep class rx.schedulers.ImmediateScheduler {
#    public <methods>;
#}
#-keep class rx.schedulers.TestScheduler {
#    public <methods>;
#}
#-keep class rx.schedulers.Schedulers {
#    public static ** test();
#}
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
#    long producerIndex;
#    long consumerIndex;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#    long producerNode;
#    long consumerNode;
#}
#
## Retain generic type information for use by reflection by converters and adapters.
#-keepattributes Signature
## Retain service method parameters.
#-keepclassmembernames,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}
## Ignore annotation used for build tooling.
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#
#-dontwarn okhttp3.**
#-dontwarn okio.**
#-dontwarn javax.annotation.**
## A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#
###---------------Begin: proguard configuration for Gson  ----------
## Gson uses generic type information stored in a class file when working with fields. Proguard
## removes such information by default, so configure it to keep all of it.
#-keepattributes Signature
#
## For using GSON @Expose annotation
#-keepattributes *Annotation*
#
## Gson specific classes
#-dontwarn sun.misc.**
##-keep class com.google.gson.stream.** { *; }
#
## Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }
#
## Prevent proguard from stripping interface information from TypeAdapterFactory,
## JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer
#
###---------------End: proguard configuration for Gson  ----------
#
## Retain generated class which implement Unbinder.
#-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
#
## Prevent obfuscation of types which use ButterKnife annotations since the simple name
## is used to reflectively look up the generated ViewBinding.
#-keep class butterknife.*
#-keepclasseswithmembernames class * { @butterknife.* <methods>; }
#-keepclasseswithmembernames class * { @butterknife.* <fields>; }