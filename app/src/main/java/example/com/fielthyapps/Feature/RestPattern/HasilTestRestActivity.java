package example.com.fielthyapps.Feature.RestPattern;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import example.com.fielthyapps.R;
import example.com.fielthyapps.Service.ElevenLabs;

public class HasilTestRestActivity extends AppCompatActivity {
    private TextView tV_status, tV_desc_status;
    private ImageView iV_back;
    private String type_ket;
    private ElevenLabs elevenLabs;

    @Override
    protected void onStop() {
        super.onStop();
        if (elevenLabs != null) {
            elevenLabs.stopMp3();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_test_rest);

        iV_back = findViewById(R.id.iV_kembali);
        tV_status = findViewById(R.id.tV_status);
        tV_desc_status = findViewById(R.id.tV_desc_status);

// Inisialisasi ElevenLabs dengan aman
        try {
            elevenLabs = new ElevenLabs(this);
        } catch (Exception e) {
            elevenLabs = null;
        }

        Intent intent = getIntent();
        type_ket = intent.getStringExtra("type");

        if (type_ket == null || type_ket.isEmpty()) {
            type_ket = "Data Tidak Tersedia";
        }

        tV_status.setText(type_ket);
        String result = getSleepDescription(type_ket);
        tV_desc_status.setText(result);

        ImageButton btnTts = findViewById(R.id.btn_tts);
        btnTts.setOnClickListener(v -> {
            if (elevenLabs != null && !result.isEmpty()) {
                elevenLabs.textToSpeech(result);
            } else {
                Toast.makeText(this, "Fitur suara tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        iV_back.setOnClickListener(view -> {
            Intent backIntent = new Intent(HasilTestRestActivity.this, RestPatternActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(backIntent);
            finish();
        });
    }

    private String getSleepDescription(String type) {
        if ("Bad Sleep".equalsIgnoreCase(type)) {
            return "Hasil Tidur Anda: Bad Sleep\n" +
                    "Tidur Anda kurang berkualitas. Berikut rinciannya:\n" +
                    "1. Durasi Tidur Kurang: Anda tidur kurang dari 7-9 jam.\n" +
                    "2. Kualitas Tidur Rendah: Tidur Anda sering terganggu.\n" +
                    "Tips: Tetapkan jadwal tidur tetap dan hindari kafein sebelum tidur.";
        } else if ("Good Sleep".equalsIgnoreCase(type)) {
            return "Tidur Anda sangat baik! Berikut rincian hasilnya:\n" +
                    "1. Durasi Tidur Optimal: Anda tidur 7-9 jam, sesuai anjuran.\n" +
                    "2. Kualitas Tidur Tinggi: Tidur nyenyak dengan sedikit gangguan.\n" +
                    "Tips: Terus jaga konsistensi jadwal tidur Anda.";
        } else if ("Over Sleep".equalsIgnoreCase(type)) {
            return "Anda mungkin tidur terlalu banyak (Over Sleep):\n" +
                    "1. Durasi Tidur Berlebihan: Lebih dari 9 jam.\n" +
                    "2. Efek: Anda mungkin merasa lelah atau kurang segar saat bangun.\n" +
                    "Tips: Batasi tidur siang dan perhatikan pola makan Anda.";
        }
        return "Belum ada analisis hasil tidur yang tersedia.";
    }
}

