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

# Keep FolioReader classes and prevent conflicts with flutter_inappwebview
-keep class com.folioreader.** { *; }
-keepclassmembers class com.folioreader.** { *; }

# Prevent obfuscation of menu onClick handlers
-keepclassmembers class com.folioreader.ui.activity.FolioActivity {
    public void *(android.view.View);
    public boolean onOptionsItemSelected(android.view.MenuItem);
    public boolean onCreateOptionsMenu(android.view.Menu);
}

# Keep WebView related classes
-keep class android.webkit.WebView { *; }
-keep class android.webkit.WebViewClient { *; }
-keep class android.webkit.WebChromeClient { *; }

# Prevent conflicts with flutter_inappwebview plugin
-keep class com.pichillilorenzo.flutter_inappwebview.** { *; }
-dontwarn com.pichillilorenzo.flutter_inappwebview.**

# Keep menu item onClick methods
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
    public void goBackButtonClicked(android.view.View);
}

# Keep activity context and prevent null pointer exceptions
-keepclassmembers class * extends android.app.Activity {
    public void onCreate(android.os.Bundle);
    public boolean onCreateOptionsMenu(android.view.Menu);
    public boolean onOptionsItemSelected(android.view.MenuItem);
} 