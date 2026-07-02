package example.com.fielthyapps.Feature.RestPattern;

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
import java.util.concurrent.TimeUnit;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class SleepConfirmationActivity extends AppCompatActivity {

    private TextView tvQuestion, tvDuration;
    private Button btnYes, btnNo;
    private long durationMillis, startTime, endTime;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Menambahkan detik pada tampilan durasi di aktivitas konfirmasi
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        String durationStr = String.format(Locale.getDefault(), "%d jam %d menit %d detik", hours, minutes, seconds);
        tvDuration.setText("Durasi: " + durationStr);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSleepData();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        map.put("uid", currentUser.getUid());
        map.put("id", docRef.getId());
        map.put("date", dateStr);
        map.put("day", dayStr);
        map.put("timesleep", formattedTime);

        HashMap<String, Object> localMap = new HashMap<>(map);
        localMap.remove("timestamp");

        
        docRef.set(map).addOnSuccessListener(aVoid -> {
            Toast.makeText(SleepConfirmationActivity.this, "Data istirahat disimpan!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(SleepConfirmationActivity.this, "Gagal menyimpan ke cloud, data tersimpan lokal.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}