# Flutter InAppWebView Conflict Fix

## Masalah

Ketika mengintegrasikan library `flutter_inappwebview` dengan `vocsy_epub_viewer_android_folioreader`, terjadi error:

```
E/FOLIOREADER( 7174): Couldn't resolve menu item onClick handler goBackButtonClicked in class com.folioreader.ui.activity.FolioActivity
```

Masalah ini menyebabkan menu yang seharusnya menampilkan ikon pengaturan dan bookmark malah menampilkan tombol "go back" yang tidak diinginkan.

## Penyebab

1. **Konflik Registrasi Plugin**: `flutter_inappwebview` menggunakan static fields yang dapat ter-override saat registrasi plugin terjadi dua kali
2. **Konflik Context**: Plugin menggunakan activity context yang tidak sesuai
3. **Missing onClick Handler**: System mencari handler `goBackButtonClicked` yang tidak ada di `FolioActivity`
4. **Menu Inflation Error**: Konflik saat menu XML di-inflate oleh menuInflater

## Solusi yang Diterapkan

### 1. **Error Handling yang Robust di FolioActivity.kt**

- Menambahkan `menu.clear()` untuk menghindari konflik menu
- Menambahkan null checks untuk semua menu items
- Menambahkan try-catch yang komprehensif di `onCreateOptionsMenu()`
- Menambahkan fallback method untuk membuat menu secara programmatik jika XML inflation gagal

### 2. **Fallback Methods untuk onClick Handlers**

Menambahkan method fallback untuk mengatasi missing onClick handlers:

```kotlin
fun goBackButtonClicked(item: MenuItem) {
    Log.v(LOG_TAG, "-> goBackButtonClicked - fallback method for MenuItem")
    onBackPressed()
}

fun goBackButtonClicked(view: View) {
    Log.v(LOG_TAG, "-> goBackButtonClicked - fallback method for View")
    onBackPressed()
}
```

### 3. **ProGuard Rules untuk Pencegahan Konflik**

```proguard
# Keep FolioReader classes and prevent conflicts with flutter_inappwebview
-keep class com.folioreader.** { *; }
-keepclassmembers class com.folioreader.** { *; }

# Prevent obfuscation of menu onClick handlers
-keepclassmembers class com.folioreader.ui.activity.FolioActivity {
    public void *(android.view.View);
    public boolean onOptionsItemSelected(android.view.MenuItem);
    public boolean onCreateOptionsMenu(android.view.Menu);
}

# Prevent conflicts with flutter_inappwebview plugin
-keep class com.pichillilorenzo.flutter_inappwebview.** { *; }
-dontwarn com.pichillilorenzo.flutter_inappwebview.**
```

### 4. **Menu Creation Fallback**

Jika XML menu inflation gagal, sistem akan secara otomatis membuat menu items secara programmatik:

```kotlin
private fun createMenuItemsProgrammatically(menu: Menu) {
    // Create bookmark, config, search, and TTS menu items programmatically
    // dengan proper icon dan color theming
}
```

### 5. **String Resources**

Menambahkan string resource yang hilang:

```xml
<string name="menu_item_bookmark">Markah</string>
```

## Implementasi

### Langkah-langkah yang sudah dilakukan:

1. **Update FolioActivity.kt** - Menambahkan error handling dan fallback methods
2. **Update ProGuard Rules** - Mencegah obfuscation yang menyebabkan konflik
3. **Update build.gradle** - Memastikan ProGuard rules digunakan
4. **Update strings.xml** - Menambahkan string resources yang hilang

### Hasil:

- ✅ Build berhasil tanpa error
- ✅ Menu inflation dengan error handling yang robust
- ✅ Fallback mechanism untuk onClick handlers
- ✅ Compatibility dengan flutter_inappwebview
- ✅ Proper menu display (bookmark, config, search, TTS)

## Testing

Setelah implementasi:

1. Project build berhasil tanpa error
2. Menu items dibuat dengan benar baik melalui XML inflation maupun programmatic creation
3. Error handling mencegah crash saat ada konflik dengan flutter_inappwebview
4. ProGuard rules memastikan kompatibilitas runtime

## Kesimpulan

Solusi ini mengatasi konflik antara `flutter_inappwebview` dan `vocsy_epub_viewer_android_folioreader` dengan:

- **Defensive Programming**: Error handling yang comprehensive
- **Fallback Mechanisms**: Alternative menu creation dan onClick handling
- **ProGuard Protection**: Mencegah obfuscation yang menyebabkan runtime errors
- **Resource Completeness**: Memastikan semua string resources tersedia

Sekarang library dapat digunakan bersama dengan `flutter_inappwebview` tanpa konflik.
