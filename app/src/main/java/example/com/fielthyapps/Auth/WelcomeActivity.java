package example.com.fielthyapps.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import example.com.fielthyapps.R;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnDaftarSekarang, btnMasukAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnDaftarSekarang = findViewById(R.id.btn_daftar_sekarang);
        btnMasukAkun = findViewById(R.id.btn_masuk_akun);

        // Navigasi ke halaman Registrasi
        btnDaftarSekarang.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, RegistrasiActivity.class));
        });

        // Navigasi ke halaman Login
        btnMasukAkun.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        });
    }
}
