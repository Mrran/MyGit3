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
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

-keepattributes Signature

# 代码混淆压缩比，在0~7之间
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
-verbose
# 避免混淆泛型
-keepattributes Signature

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses
#google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod
# 重命名抛出异常时的文件名称
-renamesourcefileattribute SourceFile
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 处理support包
-dontnote android.support.**
-dontwarn android.support.**
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}
# 保留四大组件，自定义的Application等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod


# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[]   serialPersistentFields;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}
#assume no side effects:删除android.util.Log输出的日志
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
#保留Keep注解的类名和方法
-keep,allowobfuscation @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclassmembers class * {
    @android.support.annotation.Keep *;
}


# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform




-keep class com.iflytek.**{*;}
-keepattributes Signature
-keep class com.matou.smartcar.protocol.*{*;}
-keep class com.matou.smartcar.bean.*{*;}
-keep class com.matou.smartcar.view.*{*;}
-keep class com.matou.smartcar.net.*{*;}
-keep class skin.support.*{*;}
-keep class org.eclipse.paho.*{*;}
-keep class org.greenrobot.*{*;}
#okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.squareup.okhttp.**


#3D 地图

-keep class com.amap.api.mapcore.**{*;}

-keep class com.amap.api.maps.**{*;}

-keep class com.autonavi.amap.mapcore.*{*;}

#定位

-keep class com.amap.api.location.**{*;}

-keep class com.loc.**{*;}

-keep class com.amap.api.fence.**{*;}

-keep class com.autonavi.aps.amapapi.model.**{*;}

# 搜索

-keep class com.amap.api.services.**{*;}

# 2D地图

-keep class com.amap.api.maps2d.**{*;}

-keep class com.amap.api.mapcore2d.**{*;}

# 导航

-keep class com.amap.api.navi.**{*;}

-keep class com.autonavi.**{*;}
-keep class com.amap.api.navi.**{*;}
-keep class com.alibaba.idst.nls.** {*;}
-keep class com.nlspeech.nlscodec.** {*;}
-keep class com.google.**{*;}

#导航 V7.3.0及以后：
-keep class com.amap.api.navi.**{*;}
-keep class com.alibaba.mit.alitts.*{*;}
-keep class com.google.**{*;}

#导航 V8.1.0及以后：
-keep class com.amap.api.navi.**{*;}
-keep class com.alibaba.idst.nui.* {*;}
-keep class com.google.**{*;}

#butterknife
-keep class butterknife.* { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-keep class com.baidu.vi.** {*;}
-dontwarn com.baidu.**