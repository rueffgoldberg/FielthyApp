package example.com.fielthyapps.Feature.RestPattern;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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

    private boolean isDecisionMade = false;
    private static final String CONFIRM_CHANNEL_ID = "confirm_channel_high";
    private static final int CONFIRM_NOTIFICATION_ID = 4;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- KODE PENDOBRAK LAYAR (WAJIB ADA AGAR BISA MUNCUL OTOMATIS) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }
        // -----------------------------------------------------------------

        setContentView(R.layout.activity_sleep_confirmation);

        tvQuestion = findViewById(R.id.tv_sleep_question);
        tvDuration = findViewById(R.id.tv_sleep_duration);
        btnYes = findViewById(R.id.btn_sleep_yes);
        btnNo = findViewById(R.id.btn_sleep_no);

        fStore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        durationMillis = getIntent().getLongExtra("duration", 0);
        startTime = getIntent().getLongExtra("start_time", 0);
        endTime = getIntent().getLongExtra("end_time", 0);

        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        String durationStr = String.format(Locale.getDefault(), "%d jam %d menit %d detik", hours, minutes, seconds);
        tvDuration.setText("Durasi: " + durationStr);

        // Hapus notifikasi di laci jika Activity ini sudah muncul/terbuka
        hapusNotifikasiPengingat();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDecisionMade = true;
                btnYes.setEnabled(false);
                btnNo.setEnabled(false);
                Toast.makeText(SleepConfirmationActivity.this, "Menyimpan data istirahat...", Toast.LENGTH_SHORT).show();

                hapusNotifikasiPengingat();
                saveSleepData();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDecisionMade = true;
                btnYes.setEnabled(false);
                btnNo.setEnabled(false);
                Toast.makeText(SleepConfirmationActivity.this, "Data diabaikan.", Toast.LENGTH_SHORT).show();

                hapusNotifikasiPengingat();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Jika pengguna keluar (Home/Back) tanpa menekan YA/TIDAK, munculkan reminder di laci notifikasi
        if (!isDecisionMade && !isFinishing()) {
            tampilkanNotifikasiPengingat();
        }
    }

    private void tampilkanNotifikasiPengingat() {
        Intent intent = new Intent(this, SleepConfirmationActivity.class);
        intent.putExtra("duration", durationMillis);
        intent.putExtra("start_time", startTime);
        intent.putExtra("end_time", endTime);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CONFIRM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_rest)
                .setContentTitle("Konfirmasi Bangun")
                .setContentText("Klik untuk mengonfirmasi waktu istirahat Anda.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CONFIRM_CHANNEL_ID, "Konfirmasi Bangun", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }
            manager.notify(CONFIRM_NOTIFICATION_ID, builder.build());
        }
    }

    private void hapusNotifikasiPengingat() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(CONFIRM_NOTIFICATION_ID);
        }
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
        map.put("timestamp", startTime);

        HashMap<String, Object> localMap = new HashMap<>(map);
        localMap.remove("timestamp");

        executor.execute(() -> {
            new DatabaseHelper(SleepConfirmationActivity.this).insertOrUpdateRecord(DatabaseHelper.TABLE_REST, docRef.getId(), localMap);

            docRef.set(map).addOnSuccessListener(aVoid -> {
                runOnUiThread(() -> {
                    Toast.makeText(SleepConfirmationActivity.this, "Data istirahat disimpan!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).addOnFailureListener(e -> {
                runOnUiThread(() -> {
                    Toast.makeText(SleepConfirmationActivity.this, "Gagal sinkron cloud, data aman di lokal.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }
}
