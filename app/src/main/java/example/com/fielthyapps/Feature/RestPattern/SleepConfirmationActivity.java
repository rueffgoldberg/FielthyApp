package example.com.fielthyapps.Feature.RestPattern;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class SleepConfirmationActivity extends AppCompatActivity {

    private TextView tvQuestion, tvDuration;
    private Button btnYes, btnNo;
    private long durationMillis, startTime, endTime;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;

    // Pembuatan Jalur Belakang (Background Thread) untuk kelancaran UI
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_confirmation);

        // 1. PEMBUNUH NOTIFIKASI OTOMATIS
        // Langsung hapus notifikasi dari laci sistem saat layar Pop-up ini muncul
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(4); // 4 adalah CONFIRM_NOTIFICATION_ID dari Service
        }

        tvQuestion = findViewById(R.id.tv_sleep_question);
        tvDuration = findViewById(R.id.tv_sleep_duration);
        btnYes = findViewById(R.id.btn_sleep_yes);
        btnNo = findViewById(R.id.btn_sleep_no);

        fStore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        durationMillis = getIntent().getLongExtra("duration", 0);
        startTime = getIntent().getLongExtra("start_time", 0);
        endTime = getIntent().getLongExtra("end_time", 0);

        // Menambahkan detik pada tampilan durasi di aktivitas konfirmasi
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        String durationStr = String.format(Locale.getDefault(), "%d jam %d menit %d detik", hours, minutes, seconds);
        tvDuration.setText("Durasi: " + durationStr);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kunci tombol agar tidak bisa ditekan 2 kali (mencegah data ganda)
                btnYes.setEnabled(false);
                btnNo.setEnabled(false);
                Toast.makeText(SleepConfirmationActivity.this, "Menyimpan data istirahat...", Toast.LENGTH_SHORT).show();

                saveSleepData();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kunci tombol saat ditekan
                btnYes.setEnabled(false);
                btnNo.setEnabled(false);

                Toast.makeText(SleepConfirmationActivity.this, "Data diabaikan.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveSleepData() {
        if (currentUser == null) {
            finish();
            return;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        String formattedTime = String.format(Locale.getDefault(), "%d jam %d menit %d detik", hours, minutes, seconds);

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", new Locale("id","ID"));
        String dateStr = sdfDate.format(new Date(startTime));
        String dayStr = sdfDay.format(new Date(startTime));

        DocumentReference docRef = fStore.collection("restpattern").document();
        HashMap<String, Object> map = new HashMap<>();
        SimpleDateFormat sdfJam = new SimpleDateFormat("HH:mm", Locale.getDefault());

        map.put("uid", currentUser.getUid());
        map.put("id", docRef.getId());
        map.put("date", dateStr);
        map.put("day", dayStr);
        map.put("timesleep", formattedTime);
        map.put("start_sleep", sdfJam.format(new Date(startTime)));
        map.put("end_sleep", sdfJam.format(new Date(endTime)));
        map.put("start_timestamp", startTime);
        map.put("end_timestamp", endTime);
        map.put("timestamp", startTime); // Digunakan untuk sorting

        HashMap<String, Object> localMap = new HashMap<>(map);
        localMap.remove("timestamp");

        // 2. MELEMPAR PROSES DATABASE KE JALUR BELAKANG
        executor.execute(() -> {
            // Simpan ke SQLite di jalur belakang (agar UI tidak freeze/ngelag)
            new DatabaseHelper(SleepConfirmationActivity.this).insertOrUpdateRecord(DatabaseHelper.TABLE_REST, docRef.getId(), localMap);

            // Simpan ke Firestore tanpa memblokir navigasi
            docRef.set(map);

            runOnUiThread(() -> {
                Toast.makeText(SleepConfirmationActivity.this, "Data istirahat disimpan!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}