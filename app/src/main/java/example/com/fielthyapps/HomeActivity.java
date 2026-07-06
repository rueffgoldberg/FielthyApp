package example.com.fielthyapps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import example.com.fielthyapps.Auth.ProfileActivity;
import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.Database.SessionManager;
import example.com.fielthyapps.Feature.History.HistoryActivity;
import example.com.fielthyapps.Feature.Medcheck.MedCheckActivity;
import example.com.fielthyapps.Feature.Nutrition.NutritionActivity;
import example.com.fielthyapps.Feature.Physical.HealthConnectHelper;
import example.com.fielthyapps.Feature.Physical.PhysicalActivity;
import example.com.fielthyapps.Feature.RestPattern.RestPatternActivity;
import example.com.fielthyapps.Feature.Smoker.SmokerActivity;
import example.com.fielthyapps.Feature.Stress.StressActivity;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout medcheck, nutrition, physical, restpattern, smoker, stress, card_water;
    private TextView tV_profile, tV_home_step, tV_home_step_status, tV_home_water, tV_home_water_status;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore fStore;
    private CircleImageView image_profile;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigate);
        medcheck = findViewById(R.id.LL_medcheck);
        nutrition = findViewById(R.id.LL_nutrition);
        physical = findViewById(R.id.LL_physical);
        restpattern = findViewById(R.id.LL_rest);
        smoker = findViewById(R.id.LL_smoker);
        stress = findViewById(R.id.LL_stress);
        card_water = findViewById(R.id.card_water);
        image_profile = findViewById(R.id.profile_image);
        tV_profile = findViewById(R.id.tV_profile);
        tV_home_step = findViewById(R.id.tV_home_step);
        tV_home_step_status = findViewById(R.id.tV_home_step_status);
        tV_home_water = findViewById(R.id.tV_home_water);
        tV_home_water_status = findViewById(R.id.tV_home_water_status);
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        tV_home_step.setText("--");
        tV_home_step_status.setText("Memuat...");
        tV_home_water.setText("0");
        tV_home_water_status.setText("0%");

        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        checkUserLogin();
        refreshStepFromHealthConnect();
        loadWaterSummary();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            java.time.LocalDateTime currentDateTime = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            formattedDate = currentDateTime.format(formatter);
        }

        card_water.setOnClickListener(v -> {
            showWaterDialog();
        });

        // --- MENU CLICK LISTENERS ---
        medcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MedCheckActivity.class));
            }
        });

        nutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) return;
                String uid = firebaseUser.getUid();

                // Dokumen belum ada, buat dokumen baru
                DocumentReference documentReference = fStore.collection("nutritiontest").document();
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("uid", uid);
                hashMap.put("id", documentReference.getId());
                hashMap.put("date", formattedDate);
                hashMap.put("laukpauk", "0");
                hashMap.put("makanan", "0");
                hashMap.put("sayuran", "0");
                hashMap.put("buah", "0");

                DatabaseHelper dbHelper = new DatabaseHelper(HomeActivity.this);
                dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_NUTRITION, documentReference.getId(), hashMap);

                // Pindahkan halaman SEKARANG (Offline-first)
                Intent intent = new Intent(HomeActivity.this, NutritionActivity.class);
                intent.putExtra("id", documentReference.getId());
                startActivity(intent);
            }
        });

        physical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PhysicalActivity.class));
            }
        });

        restpattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RestPatternActivity.class));
            }
        });

        smoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SmokerActivity.class));
            }
        });

        stress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) return;
                String uid = firebaseUser.getUid();

                // Buat ID dokumen terlebih dahulu secara lokal (offline-first)
                DocumentReference documentReference = fStore.collection("stresstest").document();
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("uid", uid);
                hashMap.put("id", documentReference.getId());
                hashMap.put("date", formattedDate);
                hashMap.put("stress", "0");
                hashMap.put("depresi", "0");
                hashMap.put("cemas", "0");

                // Simpan ke SQLite lokal dulu agar tidak tergantung koneksi
                DatabaseHelper dbHelper = new DatabaseHelper(HomeActivity.this);
                dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_STRESS, documentReference.getId(), hashMap);

                // Langsung buka StressActivity dengan ID (offline-first, tidak perlu tunggu Firestore)
                Intent intent = new Intent(HomeActivity.this, StressActivity.class);
                intent.putExtra("id", documentReference.getId());
                startActivity(intent);

                // Simpan ke Firestore di background (tidak perlu tunggu)
                documentReference.set(hashMap);
            }
        });

        // --- BOTTOM NAVIGATION ---
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_home) {
                    return true;
                } else if (item.getItemId() == R.id.bottom_history) {
                    Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.bottom_profile) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigate);
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().findItem(R.id.bottom_home).setChecked(true);
        }
        checkUserLogin();
        refreshStepFromHealthConnect();
        loadWaterSummary();
    }

    private void loadStepSummary() {
        int totalStep = getSharedPreferences("health_data", MODE_PRIVATE)
                .getInt("total_step", 0);

        tV_home_step.setText(String.valueOf(totalStep));
        tV_home_step_status.setText(getActivityStatus(totalStep));
    }

    private String getActivityStatus(int totalStep) {
        if (totalStep <= 1000) {
            return "Aktivitas Rendah";
        } else if (totalStep <= 3000) {
            return "Aktivitas Ringan";
        } else if (totalStep <= 6000) {
            return "Aktivitas Sedang";
        } else if (totalStep <= 10000) {
            return "Aktivitas Aktif";
        } else if (totalStep <= 15000) {
            return "Aktivitas Tinggi";
        } else {
            return "Aktivitas Optimal";
        }
    }

    private void updateActivityStatusStyle(int totalStep) {
        if (totalStep <= 1000) {
            tV_home_step_status.setBackgroundResource(R.drawable.bg_py_rendah);
            tV_home_step_status.setTextColor(getColor(R.color.black));
        } else if (totalStep <= 3000) {
            tV_home_step_status.setBackgroundResource(R.drawable.bg_py_ringan);
            tV_home_step_status.setTextColor(getColor(R.color.black));
        } else if (totalStep <= 6000) {
            tV_home_step_status.setBackgroundResource(R.drawable.bg_py_sedang);
            tV_home_step_status.setTextColor(getColor(R.color.black));
        } else if (totalStep <= 10000) {
            tV_home_step_status.setBackgroundResource(R.drawable.bg_py_aktif);
            tV_home_step_status.setTextColor(getColor(R.color.black));
        } else if (totalStep <= 15000) {
            tV_home_step_status.setBackgroundResource(R.drawable.bg_py_tinggi);
            tV_home_step_status.setTextColor(getColor(R.color.black));
        } else {
            tV_home_step_status.setBackgroundResource(R.drawable.bg_py_optimal);
            tV_home_step_status.setTextColor(getColor(R.color.black));
        }
    }

    private void refreshStepFromHealthConnect() {
        HealthConnectHelper.INSTANCE.getTodaySteps(
                this,
                totalStep -> {
                    // Belum ada izin Health Connect
                    if (totalStep == -1) {
                        tV_home_step.setText("--");
                        tV_home_step_status.setText("Hubungkan Health Connect");
                        return null;
                    }

                    getSharedPreferences("health_data", MODE_PRIVATE)
                            .edit()
                            .putInt("total_step", totalStep)
                            .apply();

                    tV_home_step.setText(String.valueOf(totalStep));
                    tV_home_step_status.setText(getActivityStatus(totalStep));
                    updateActivityStatusStyle(totalStep);

                    return null;
                }
        );
    }

    private void checkUserLogin() {
        firebaseUser = firebaseAuth.getCurrentUser(); // Selalu dapatkan instance FirebaseUser terbaru
        if (firebaseUser == null) return;

        String uid = firebaseUser.getUid();

        // 1. Tampilkan sapaan awal dan foto Google/default secara cepat
        setGreeting("User");
        if (firebaseUser.getPhotoUrl() != null) {
            Glide.with(HomeActivity.this)
                    .load(firebaseUser.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(image_profile);
        } else {
            Glide.with(HomeActivity.this)
                    .load(R.drawable.ic_profile)
                    .into(image_profile);
        }

        // 2. Ambil data profil langsung dan eksklusif dari Cloud Firestore secara real-time
        fStore.collection("user").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        String fsPhotoUrl = documentSnapshot.getString("photoUrl");

                        if (name != null && !name.isEmpty()) {
                            setGreeting(name);
                        }

                        if (fsPhotoUrl != null && !fsPhotoUrl.isEmpty()) {
                            Glide.with(HomeActivity.this)
                                    .load(fsPhotoUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .error(R.drawable.ic_profile)
                                    .into(image_profile);
                        }
                    }
                });
    }

    private void setGreeting(String nama) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 4 && hour < 12) {
            greeting = "Selamat Pagi";
        } else if (hour >= 12 && hour < 15) {
            greeting = "Selamat Siang";
        } else if (hour >= 15 && hour < 18) {
            greeting = "Selamat Sore";
        } else {
            greeting = "Selamat Malam";
        }

        tV_profile.setText(greeting + ", " + (nama != null ? nama : "User") + "!");
    }

    private void showWaterDialog() {

        BottomSheetDialog dialog =
                new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(
                R.layout.bottom_sheet_water,
                null
        );

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        ImageView btnClose = view.findViewById(R.id.btnClose);
        TextView tvWater = view.findViewById(R.id.tV_water);
        TextView tvPercent = view.findViewById(R.id.tV_percent);
        TextView tvMl = view.findViewById(R.id.tV_ml);
        ProgressBar progressWater = view.findViewById(R.id.progressWater);
        Button btnTambah = view.findViewById(R.id.btnTambah);
        Button btnKurang = view.findViewById(R.id.btnKurang);
        ImageButton btnMinusTarget = view.findViewById(R.id.btnMinusTarget);
        ImageButton btnPlusTarget = view.findViewById(R.id.btnPlusTarget);
        TextView tvTarget = view.findViewById(R.id.tV_target);
        FlexboxLayout layoutCup =
                view.findViewById(R.id.layoutCup);

        SharedPreferences pref =
                getSharedPreferences("water_tracker", MODE_PRIVATE);

        int water = pref.getInt("water", 0);

        int target = pref.getInt("target", 8);
        tvTarget.setText(String.valueOf(target));
        btnClose.setOnClickListener(v -> dialog.dismiss());

        tvWater.setText(water + " / " + target);

        int currentMl = water * 250;
        int targetMl = target * 250;

        tvMl.setText(currentMl + " / " + targetMl + " mL");

        int percent = 0;

        if (target > 0) {
            percent = (water * 100) / target;
        }

        tvPercent.setText(percent + "%");
        progressWater.setProgress(percent);
        updateCupIcons(layoutCup, water, target);

        btnTambah.setOnClickListener(v -> {

            int currentWater = pref.getInt("water", 0);
            int currentTarget = pref.getInt("target", 8);

            if (currentWater < currentTarget) {

                currentWater++;

                pref.edit()
                        .putInt("water", currentWater)
                        .apply();

                tvWater.setText(currentWater + " / " + currentTarget);

                tvMl.setText((currentWater * 250) + " / " + (currentTarget * 250) + " mL");

                int currentPercent = 0;

                if (currentTarget > 0) {
                    currentPercent = (currentWater * 100) / currentTarget;
                }

                tvPercent.setText(currentPercent + "%");
                progressWater.setProgress(currentPercent);

                updateCupIcons(
                        layoutCup,
                        currentWater,
                        currentTarget
                );

                loadWaterSummary();
            }

        });

        btnKurang.setOnClickListener(v -> {

            int currentWater = pref.getInt("water", 0);
            int currentTarget = pref.getInt("target", 8);

            if (currentWater > 0) {

                currentWater--;

                pref.edit()
                        .putInt("water", currentWater)
                        .apply();

                tvWater.setText(currentWater + " / " + currentTarget);

                tvMl.setText((currentWater * 250) + " / " + (currentTarget * 250) + " mL");

                int currentPercent = 0;

                if (currentTarget > 0) {
                    currentPercent = (currentWater * 100) / currentTarget;
                }

                tvPercent.setText(currentPercent + "%");
                progressWater.setProgress(currentPercent);

                updateCupIcons(
                        layoutCup,
                        currentWater,
                        currentTarget
                );

                loadWaterSummary();
            }

        });

        btnMinusTarget.setOnClickListener(v -> {

            int currentTarget = pref.getInt("target", 8);
            int currentWater = pref.getInt("water", 0);

            if (currentTarget > 4) {

                currentTarget--;

                pref.edit()
                        .putInt("target", currentTarget)
                        .apply();

                tvTarget.setText(String.valueOf(currentTarget));

                tvWater.setText(currentWater + " / " + currentTarget);

                tvMl.setText((currentWater * 250) + " / " + (currentTarget * 250) + " mL");

                int currentPercent = 0;

                if (currentTarget > 0) {
                    currentPercent = (currentWater * 100) / currentTarget;
                }

                tvPercent.setText(currentPercent + "%");
                progressWater.setProgress(currentPercent);

                updateCupIcons(
                        layoutCup,
                        currentWater,
                        currentTarget
                );

                loadWaterSummary();
            }

        });

        btnPlusTarget.setOnClickListener(v -> {

            int currentTarget = pref.getInt("target", 8);
            int currentWater = pref.getInt("water", 0);

            if (currentTarget < 12) {

                currentTarget++;

                pref.edit()
                        .putInt("target", currentTarget)
                        .apply();

                tvTarget.setText(String.valueOf(currentTarget));

                tvWater.setText(currentWater + " / " + currentTarget);

                tvMl.setText((currentWater * 250) + " / " + (currentTarget * 250) + " mL");

                int currentPercent = 0;

                if (currentTarget > 0) {
                    currentPercent = (currentWater * 100) / currentTarget;
                }

                tvPercent.setText(currentPercent + "%");
                progressWater.setProgress(currentPercent);

                updateCupIcons(
                        layoutCup,
                        currentWater,
                        currentTarget
                );

                loadWaterSummary();
            }

        });

        dialog.show();
    }

    private void loadWaterSummary() {

        SharedPreferences pref =
                getSharedPreferences("water_tracker", MODE_PRIVATE);

        int water = pref.getInt("water", 0);
        int target = pref.getInt("target", 8);

        tV_home_water.setText(String.valueOf(water));

        int percent = 0;

        if (target > 0) {
            percent = (water * 100) / target;
        }

        tV_home_water_status.setText(percent + "%");
    }

    private void updateCupIcons(
            FlexboxLayout layoutCup,
            int water,
            int target) {

        layoutCup.removeAllViews();

        float density = getResources().getDisplayMetrics().density;

        int size = (int) (22 * density);
        int margin = (int) (2 * density);

        for (int i = 0; i < target; i++) {

            ImageView cup = new ImageView(this);

            FlexboxLayout.LayoutParams params =
                    new FlexboxLayout.LayoutParams(size, size);

            params.setMargins(margin, margin, margin, margin);

            cup.setLayoutParams(params);

            cup.setScaleType(ImageView.ScaleType.FIT_CENTER);

            if (i < water) {

                cup.setImageResource(R.drawable.ic_gelas_penuh);

            } else {

                cup.setImageResource(R.drawable.ic_gelas_kosong);

            }

            layoutCup.addView(cup);

        }

    }
}