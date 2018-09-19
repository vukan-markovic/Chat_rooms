-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontwarn com.facebook.**
-dontwarn com.twitter.**
-dontwarn com.google.android.gms.measurement.AppMeasurement*
-dontwarn com.firebase.ui.auth.data.remote.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn com.squareup.okhttp.**
-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn retrofit2.**
-dontwarn okio.**
-dontwarn com.crashlytics.**
-keepnames class com.facebook.login.LoginManager
-keepnames class com.twitter.sdk.android.core.identity.TwitterAuthClient
-keepnames class com.facebook.FacebookActivity
-keepnames class com.facebook.CustomTabActivity
-dontnote com.google.**
-dontnote com.facebook.**
-dontnote com.twitter.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep class com.facebook.all.All
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-printmapping mapping.txt
-renamesourcefileattribute SourceFile

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keepclassmembers class * extends com.google.android.gms.internal.measurement.zzyv {
  <fields>;
}

-keepclassmembers class vukan.com.chat_rooms.models.** {
    *;
}

-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class vukan.com.chat_rooms.viewholder.** {
    *;
}

-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep public class com.android.vending.billing.IInAppBillingService {
    public static com.android.vending.billing.IInAppBillingService asInterface(android.os.IBinder);
    public android.os.Bundle getSkuDetails(int, java.lang.String, java.lang.String, android.os.Bundle);
}