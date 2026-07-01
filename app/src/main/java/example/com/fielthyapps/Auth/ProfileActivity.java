package example.com.fielthyapps.Auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.Database.SessionManager;
import example.com.fielthyapps.Feature.History.HistoryActivity;
import example.com.fielthyapps.Feature.RestPattern.SleepDetectionService;
import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // Header bindings (changed eT_name and eT_email to TextView)
    private TextView eT_name, eT_email;
    private EditText eT_location = null, eT_date = null, eT_gender = null, eT_age = null; // Left for null-safety compatibility

    private CircleImageView image_profile;

    // Settings menu rows
    private View btn_edit_profile;
    private LinearLayout btn_change_password;
    private LinearLayout btn_notifications;
    private LinearLayout btn_help;
    private LinearLayout btn_version;
    private SwitchCompat switch_theme;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigate);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        }

        image_profile = findViewById(R.id.profile_image);
        eT_name = findViewById(R.id.eT_name_profile);
        eT_email = findViewById(R.id.eT_email_profile);
        Log.d("PROFILE_DEBUG", "ImageView = " + image_profile);

        // Find Settings items
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        btn_change_password = findViewById(R.id.row_change_password);
        btn_notifications = findViewById(R.id.row_notifications);
        btn_help = findViewById(R.id.row_help);
        btn_version = findViewById(R.id.row_version);
        switch_theme = findViewById(R.id.switch_theme);
        btn_logout = findViewById(R.id.btn_logout);

        // checkUser dipindah ke onResume()



        // --- 1. EDIT PROFILE CLICK ---
        if (image_profile != null) {
            image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                }
            });
        }

        if (btn_edit_profile != null) {
            btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                }
            });
        }

        // --- 2. GANTI KATA SANDI ---
        if (btn_change_password != null) {
            btn_change_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangePasswordDialog();
                }
            });
        }

        // --- 3. PEMBERITAHUAN ---
        if (btn_notifications != null) {
            btn_notifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNotificationsDialog();
                }
            });
        }

        // --- 4. TEMA APLIKASI (Switch Toggle) ---
        if (switch_theme != null) {
            boolean isDark = getSharedPreferences("theme_pref", MODE_PRIVATE).getBoolean("is_dark", false);

            switch_theme.setOnCheckedChangeListener(null);
            switch_theme.setChecked(isDark);

            switch_theme.setOnCheckedChangeListener((buttonView, isChecked) -> {
                getSharedPreferences("theme_pref", MODE_PRIVATE).edit().putBoolean("is_dark", isChecked).apply();

                // Berikan sedikit delay agar switch terlihat bergeser dulu sebelum transisi smooth
                new Handler().postDelayed(() -> {
                    if (isChecked) {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    // Gunakan transisi halus saat reload activity
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }, 200);
            });
        }

        // --- 5. BANTUAN & KONTAK KAMI ---
        if (btn_help != null) {
            btn_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHelpDialog();
                }
            });
        }

        // --- 6. VERSI APLIKASI ---
        if (btn_version != null) {
            btn_version.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showVersionDialog();
                }
            });
        }

        // --- 7. LOGOUT ---
        if (btn_logout != null) {
            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionManager.logoutUser();
                    // Firebase sign out as well
                    try {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                    } catch (Exception e) {
                        Log.e("Logout", "Firebase signout failed: " + e.getMessage());
                    }
                    // Reset theme preference to Light Mode on logout
                    getSharedPreferences("theme_pref", MODE_PRIVATE).edit().putBoolean("is_dark", false).apply();
                    androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);

                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }

        // --- BOTTOM NAVIGATION BAR ---
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.bottom_home) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (item.getItemId() == R.id.bottom_history) {
                        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (item.getItemId() == R.id.bottom_profile) {
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigate);
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().findItem(R.id.bottom_profile).setChecked(true);
        }
        checkUser();
    }

    private void showChangePasswordDialog() {
        View view = getLayoutInflater()
                .inflate(R.layout.popup_ganti_password, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        EditText inputOld = view.findViewById(R.id.et_old_password);
        EditText inputNew = view.findViewById(R.id.et_new_password);
        EditText inputConfirm = view.findViewById(R.id.et_confirm_password);
        Button btnBatal = view.findViewById(R.id.btn_batal);
        Button btnSimpan = view.findViewById(R.id.btn_simpan);
        ImageView btnClose = view.findViewById(R.id.btn_close);
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnBatal.setOnClickListener(v -> dialog.dismiss());

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = inputOld.getText().toString().trim();
                String newPass = inputNew.getText().toString().trim();
                String confirmPass = inputConfirm.getText().toString().trim();

                if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Harap lengkapi semua bidang", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(ProfileActivity.this, "Konfirmasi kata sandi tidak cocok", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPass.length() < 6) {
                    Toast.makeText(ProfileActivity.this, "Kata sandi baru minimal 6 karakter", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authenticate and update via Firebase Auth
                com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.getEmail() != null) {
                    Toast.makeText(ProfileActivity.this, "Memverifikasi...", Toast.LENGTH_SHORT).show();
                    com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.getEmail(), oldPass);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    // Update local SQLite user database
                                    HashMap<String, Object> localData = dbHelper.getUserData(user.getUid());
                                    dbHelper.insertUser(
                                            user.getUid(),
                                            user.getEmail(),
                                            newPass,
                                            safeGet(localData, "nama"),
                                            safeGet(localData, "location"),
                                            safeGet(localData, "birthday"),
                                            safeGet(localData, "gender"),
                                            localData.get("umur") != null ? (Integer) localData.get("umur") : 0
                                    );
                                    Toast.makeText(ProfileActivity.this, "Kata sandi berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Gagal memperbarui: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Kata sandi saat ini salah", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Offline mode updates locally
                    String offlineUid = sessionManager.getCurrentUserUid();
                    if (offlineUid != null) {
                        HashMap<String, Object> localData = dbHelper.getUserData(offlineUid);
                        String currentLocalPass = safeGet(localData, "password");
                        if (currentLocalPass.equals(oldPass)) {
                            dbHelper.insertUser(
                                    offlineUid,
                                    safeGet(localData, "email"),
                                    newPass,
                                    safeGet(localData, "nama"),
                                    safeGet(localData, "location"),
                                    safeGet(localData, "birthday"),
                                    safeGet(localData, "gender"),
                                    localData.get("umur") != null ? (Integer) localData.get("umur") : 0
                            );
                            Toast.makeText(ProfileActivity.this, "Kata sandi berhasil diperbarui (Mode Offline)", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Kata sandi saat ini salah (Mode Offline)", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void showNotificationsDialog() {

        View view = getLayoutInflater()
                .inflate(R.layout.popup_notification_settings, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        ImageView btnClose =
                view.findViewById(R.id.btn_close);

        android.widget.Switch switchActivity =
                view.findViewById(R.id.switch_activity);

        android.widget.Switch switchMental =
                view.findViewById(R.id.switch_mental);

        android.widget.Switch switchDiet =
                view.findViewById(R.id.switch_diet);

        android.widget.Switch switchRest =
                view.findViewById(R.id.switch_rest);

        SharedPreferences prefs =
                getSharedPreferences("notif_prefs", MODE_PRIVATE);

        switchActivity.setChecked(
                prefs.getBoolean("notif_activity", true));

        switchMental.setChecked(
                prefs.getBoolean("notif_mental", true));

        switchDiet.setChecked(
                prefs.getBoolean("notif_diet", false));

        switchRest.setChecked(
                prefs.getBoolean("notif_rest", true));

        switchActivity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit()
                    .putBoolean("notif_activity", isChecked)
                    .apply();
        });

        switchMental.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit()
                    .putBoolean("notif_mental", isChecked)
                    .apply();
        });

        switchDiet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit()
                    .putBoolean("notif_diet", isChecked)
                    .apply();
        });

        switchRest.setOnCheckedChangeListener((buttonView, isChecked) -> {

            prefs.edit()
                    .putBoolean("notif_rest", isChecked)
                    .apply();

            Intent serviceIntent =
                    new Intent(ProfileActivity.this,
                            SleepDetectionService.class);

            if (isChecked) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }

            } else {

                stopService(serviceIntent);

            }
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void showHelpDialog() {

        View view = getLayoutInflater()
                .inflate(R.layout.popup_help_contact, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        ImageView btnClose =
                view.findViewById(R.id.btn_close);

        Button btnEmail =
                view.findViewById(R.id.btn_email);

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        btnClose.setOnClickListener(v ->
                dialog.dismiss());

        btnEmail.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_SENDTO);

            intent.setData(
                    Uri.parse("mailto:appsfielthy@gmail.com"));

            intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Pertanyaan/Masalah Aplikasi Fielthy");

            try {
                startActivity(
                        Intent.createChooser(
                                intent,
                                "Kirim Email"));
            } catch (Exception e) {
                Toast.makeText(
                        ProfileActivity.this,
                        "Aplikasi email tidak ditemukan",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void showVersionDialog() {

        View view = getLayoutInflater()
                .inflate(R.layout.popup_version, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        ImageView btnClose =
                view.findViewById(R.id.btn_close);

        TextView tvVersion =
                view.findViewById(R.id.tv_version);

        tvVersion.setText("3.0 - Release");

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        btnClose.setOnClickListener(v ->
                dialog.dismiss());
    }

    private void checkUser() {
        if (sessionManager == null || !sessionManager.isLoggedIn()) return;

        String uid = sessionManager.getCurrentUserUid();
        if (uid == null) return;

        // Set default profile icon first
        if (image_profile != null) {
            Glide.with(ProfileActivity.this)
                    .load(R.drawable.ic_profile)
                    .into(image_profile);
        }

        // 1. Ambil data dari SQLite lokal dulu (Sangat Cepat & Offline-support)
        HashMap<String, Object> localData = dbHelper.getUserData(uid);
        if (localData != null) {
            String localEmail = safeGet(localData, "email");
            String localPhoto = safeGet(localData, "photoUrl");
            Log.d("PROFILE_DEBUG", "localPhoto = " + localPhoto);
            String localNama = safeGet(localData, "nama");

            if (!localNama.isEmpty() && eT_name != null) {
                eT_name.setText(localNama);
            } else if (!localEmail.isEmpty() && eT_name != null) {
                eT_name.setText(localEmail);
            }

            if (!localEmail.isEmpty() && eT_email != null) {
                eT_email.setText(localEmail);
            }

            if (!localPhoto.isEmpty()) {
                loadProfileImage(localPhoto);
            }
        }

        // 2. Ambil email asli dari Firebase Auth (Lebih pasti jika online)
        com.google.firebase.auth.FirebaseUser fUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null && fUser.getEmail() != null && !fUser.getEmail().isEmpty()) {
            if (eT_email != null) eT_email.setText(fUser.getEmail());
        }

        // 3. Sinkronkan dan perbarui dengan data terbaru dari Cloud Firestore
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("user").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fsNama = documentSnapshot.getString("nama");
                        String email = documentSnapshot.getString("email");
                        String fsPhotoUrl = documentSnapshot.getString("photoUrl");
                        Log.d("PROFILE_DEBUG", "fsPhotoUrl = " + fsPhotoUrl);

                        // Gunakan nama dari Firestore jika ada, jika tidak gunakan email
                        if (fsNama != null && !fsNama.isEmpty()) {
                            if (eT_name != null) eT_name.setText(fsNama);
                        } else if (email != null && !email.isEmpty()) {
                            if (eT_name != null) eT_name.setText(email);
                        }

                        if (email != null && !email.isEmpty()) {
                            if (eT_email != null) eT_email.setText(email);
                        }

                        if (fsPhotoUrl != null && !fsPhotoUrl.isEmpty()) {
                            // Sinkronkan foto dari cloud ke SQLite lokal
                            dbHelper.updateProfileImage(uid, fsPhotoUrl);
                            loadProfileImage(fsPhotoUrl);
                        } else if (fUser != null && fUser.getPhotoUrl() != null) {
                            // Fallback ke Firebase Auth photo
                            loadProfileImage(fUser.getPhotoUrl().toString());
                        }
                    }
                });
    }

    /**
     * Fungsi universal untuk memuat gambar profil dari berbagai sumber:
     * - Base64 data URI ("data:image/jpeg;base64,...") -> decode langsung di memori
     * - URL HTTPS biasa -> Glide memuat dari internet
     * - URI file lokal -> Glide memuat dari file sistem
     */
    private void loadProfileImage(String photoSource) {
        Log.d("PROFILE_DEBUG", "loadProfileImage = " + photoSource);
        if (image_profile == null || photoSource == null || photoSource.isEmpty()) return;
        try {
            if (photoSource.startsWith("data:image")) {
                // Decode Base64 data URI menjadi Bitmap
                String base64Data = photoSource.substring(photoSource.indexOf(",") + 1);
                byte[] decodedBytes = Base64.decode(base64Data, Base64.NO_WRAP);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) {
                    image_profile.setImageBitmap(bitmap);
                } else {
                    image_profile.setImageResource(R.drawable.ic_profile);
                }
            } else {
                // URL HTTPS atau URI file lokal - gunakan Glide seperti biasa
                Glide.with(ProfileActivity.this)
                        .load(photoSource)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(image_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            image_profile.setImageResource(R.drawable.ic_profile);
        }
    }

    private String safeGet(HashMap<String, Object> map, String key) {
        if (map == null) return "";
        Object val = map.get(key);
        return val != null ? String.valueOf(val) : "";
    }
}