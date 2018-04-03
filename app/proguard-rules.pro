# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*

-keep public class com.mi.adtracker.MiAdTracker{ *; }

#安智广告混淆代码
-keep public class com.leedavid.adslib.comm.**
-dontwarn com.leedavid.**
-keepclassmembers public class com.leedavid.adslib.comm.** { public *** ***(...);}
-keepclassmembers public class com.leedavid.adslib.comm.** { public <fields> ;}
-keep class com.qq.e.** { public protected *;}
-keep class android.support.v4.app.NotificationCompat**{ public *;}
-keepclassmembers class * extends android.app.Activity { public void *(android.view.View);}
-keepclassmembers enum * { public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep class com.baidu.mobads.*.** { *; }
-keep class com.afk.** {*;}
-keep class com.google.protobuf.** {*;}
-keepattributes *Annotation* -keepattributes *JavascriptInterface*
-keep public class * implements com.afk.client.ads.inf.BaseListener
-keep public class com.afk.client.ads.inf.BaseListener
-keep public class * extends android.app.Activity
-keep public class * extends android.webkit.WebChromeClient
-keep public class com.anzhi.usercenter.sdk.AnzhiUserCenter
-keep class * implements com.anzhi.usercenter.sdk.BaseWebViewActivity$JsCallJavaInterface{*; }
-keep public class com.anzhi.sdk.ad.**{*;}
-keepclassmembers class com.afk.client.ads.inf.BaseListener {
<fields>;
<methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.control.MediaCallbackAz {
<fields>;
<methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.control.GetPrerollInfoControl {
<fields>;
<methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.manage.AnzhiAdPrerollAdCallBack {
public <fields>;
public <methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.manage.AnzhiNativeAdCallBack {
public <fields>;
public <methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.manage.AzMediaCallback {
public <fields>;
public <methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.manage.AnzhiAdCallBack {
public <fields>;
public <methods>;
}
-keepclassmembers class * extends com.anzhi.sdk.ad.control.GetThrInfo{
public <fields>;
public <methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.manage.AnzhiVideCallBack {
public <fields>;
public <methods>;
}
-keepclassmembers class * extends android.webkit.WebChromeClient{
public <fields>;
public <methods>;
}
-keepclassmembers class * extends com.anzhi.sdk.ad.main.AdBaseView{
public <fields>;
public <methods>;
}
-keepclassmembers class com.anzhi.sdk.ad.main.AdBaseView{
public <fields>;
public <methods>;
}
-keepclassmembers class * implements com.afk.client.ads.inf.BannerAdListener{
public <fields>;
public <methods>;
}
-keep class com.anzhi.usercenter.sdk.item.** {
<fields>;
<methods>;
}

