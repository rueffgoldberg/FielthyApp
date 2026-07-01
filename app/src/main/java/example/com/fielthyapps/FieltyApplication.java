package example.com.fielthyapps;

import android.app.Application;
import android.content.Intent;
import example.com.fielthyapps.Feature.RestPattern.SleepDetectionService;
import android.os.Build;

import java.util.Locale;

import example.com.fielthyapps.Service.DataLayerListenerService;
import example.com.fielthyapps.Database.DatabaseHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FieltyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Memulai layanan deteksi tidur secara otomatis saat aplikasi dijalankan
        Intent serviceIntent = new Intent(this, SleepDetectionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Load saved theme preference on startup (default to Light Mode if not set)
        boolean isDark = getSharedPreferences("theme_pref", MODE_PRIVATE).getBoolean("is_dark", false);
        if (isDark) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Load data nutrisi (JSON) secara asynchronous agar tidak memblokir main thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            dbHelper.loadNutritionFromAssets(getApplicationContext()); // <--- UBAH BAGIAN INI
        });
    }
}