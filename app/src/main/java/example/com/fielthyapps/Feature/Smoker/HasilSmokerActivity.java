package example.com.fielthyapps.Feature.Smoker;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class HasilSmokerActivity extends AppCompatActivity {
    private LinearLayout layoutkategori;
    private ImageView iVBack, iVKategori;
    private TextView tVKategori, tVDeskripsi, tVJawaban1, tVJawaban2, tVSaran, tVTarget;
    private Button btnKalkulatorPenghematan, btnKembaliSmoker;
    private ImageButton iBVolume;
    private String statusPerokok;
    private int totalPoin = -1;
    private TextToSpeech tts;

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_smoker);

        layoutkategori = findViewById(R.id.layoutkategori);
        iVBack = findViewById(R.id.iV_kembali);
        iVKategori = findViewById(R.id.iV_kategori);
        tVKategori = findViewById(R.id.tV_kategori);
        tVDeskripsi = findViewById(R.id.tV_deskripsi);
        tVJawaban1 = findViewById(R.id.tV_jawaban1);
        tVJawaban2 = findViewById(R.id.tV_jawaban2);
        tVSaran = findViewById(R.id.tV_saran);
        tVTarget = findViewById(R.id.tV_target);
        btnKalkulatorPenghematan = findViewById(R.id.btn_kalkulator_penghematan);
        btnKembaliSmoker = findViewById(R.id.btn_kembali_smoker);
        iBVolume = findViewById(R.id.iB_volume);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("id", "ID"));
                tts.setSpeechRate(1.0f);
            }
        });

        Intent intent = getIntent();
        statusPerokok = intent.getStringExtra("status_perokok");
        totalPoin = intent.getIntExtra("total_poin", -1);

        String id = intent.getStringExtra("id");
        if (id != null && (statusPerokok == null || totalPoin == -1)) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            HashMap<String, String> record = dbHelper.getRecordById(DatabaseHelper.TABLE_SMOKER, id);

            if (record != null && !record.isEmpty()) {
                statusPerokok = record.get("status_perokok");
                totalPoin = parseInt(record.get("total_poin"), -1);
            }
        }

        if ((statusPerokok == null || statusPerokok.isEmpty()) && totalPoin >= 0) {
            statusPerokok = getStatusPerokok(totalPoin);
        }

        tampilkanHasil();

        View.OnClickListener kembaliListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kembaliKeSmoker();
            }
        };

        iVBack.setOnClickListener(kembaliListener);
        btnKembaliSmoker.setOnClickListener(kembaliListener);

        btnKalkulatorPenghematan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HasilSmokerActivity.this, KalkulatorMerokokActivity.class);
                startActivity(intent);
            }
        });

        iBVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HasilSmokerActivity.this, "Sedang memuat suara... mohon tunggu.", Toast.LENGTH_SHORT).show();

                if (tts != null) {
                    String speechText = "Hasil Pemeriksaan Kebiasaan Merokok. "
                            + "Skor FTND Anda adalah " + getScoreText() + ". "
                            + "Kategori Anda adalah " + tVKategori.getText().toString() + ". "
                            + tVDeskripsi.getText().toString();
                    tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "HASIL_SMOKER");
                }
            }
        });
    }

    private void kembaliKeSmoker() {
        Intent intent = new Intent(HasilSmokerActivity.this, SmokerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void tampilkanHasil() {
        tVJawaban1.setText(getScoreText());
        tVJawaban2.setText(getInterpretasiSkor(totalPoin));

        if (statusPerokok == null) {
            statusPerokok = "";
        }

        if (statusPerokok.equalsIgnoreCase("perokok ringan")) {
            setKategoriRingan();
        } else if (statusPerokok.equalsIgnoreCase("perokok sedang")) {
            setKategoriSedang();
        } else if (statusPerokok.equalsIgnoreCase("perokok berat")) {
            setKategoriBerat();
        } else {
            setKategoriKosong();
        }
    }

    private void setKategoriRingan() {
        int warna = ContextCompat.getColor(this, R.color.tab);

        layoutkategori.setBackgroundResource(R.drawable.bg_smoker_result_card_ringan);
        iVKategori.setBackgroundResource(R.drawable.circle_blue);
        iVKategori.setImageResource(R.drawable.ic_paru_hijau);
        iVKategori.clearColorFilter();

        tVKategori.setText("KETERGANTUNGAN NIKOTIN RENDAH");
        tVKategori.setTextColor(warna);
        tVDeskripsi.setText("Berdasarkan skor FTND, tingkat ketergantungan nikotin Anda termasuk kategori rendah.");
        tVSaran.setText("- Pertahankan kebiasaan mengurangi rokok.\n- Hindari merokok pada jam pertama setelah bangun tidur.\n- Kenali situasi yang membuat ingin merokok.");
        tVTarget.setText("- Catat jumlah rokok setiap hari.\n- Kurangi 1 batang per hari bila memungkinkan.\n- Ganti kebiasaan merokok dengan minum air putih atau aktivitas ringan.");

        tVJawaban1.setTextColor(warna);
        tVJawaban2.setTextColor(warna);
    }

    private void setKategoriSedang() {
        int warna = ContextCompat.getColor(this, R.color.orange);

        layoutkategori.setBackgroundResource(R.drawable.bg_smoker_result_card_sedang);
        iVKategori.setBackgroundResource(R.drawable.circle_yellow);
        iVKategori.setImageResource(R.drawable.ic_paru_kuning);
        iVKategori.clearColorFilter();

        tVKategori.setText("KETERGANTUNGAN NIKOTIN SEDANG");
        tVKategori.setTextColor(warna);
        tVDeskripsi.setText("Berdasarkan skor FTND, tingkat ketergantungan nikotin Anda termasuk kategori sedang.");
        tVSaran.setText("- Buat batas jumlah rokok harian.\n- Jauhkan rokok dari tempat yang mudah dijangkau.\n- Minta dukungan keluarga atau teman untuk mengingatkan target berhenti.");
        tVTarget.setText("- Kurangi 2-3 batang rokok per hari.\n- Tunda rokok pertama setelah bangun tidur.\n- Hindari kopi, stres, atau lingkungan yang memicu merokok.");

        tVJawaban1.setTextColor(warna);
        tVJawaban2.setTextColor(warna);
    }

    private void setKategoriBerat() {
        int warna = ContextCompat.getColor(this, R.color.red);

        layoutkategori.setBackgroundResource(R.drawable.bg_smoker_result_card_berat);
        iVKategori.setBackgroundResource(R.drawable.circle_red);
        iVKategori.setImageResource(R.drawable.ic_paru_merah);
        iVKategori.clearColorFilter();

        tVKategori.setText("KETERGANTUNGAN NIKOTIN TINGGI");
        tVKategori.setTextColor(warna);
        tVDeskripsi.setText("Berdasarkan skor FTND, tingkat ketergantungan nikotin Anda termasuk kategori tinggi.");
        tVSaran.setText("- Pertimbangkan konsultasi dengan tenaga kesehatan.\n- Buat rencana berhenti merokok secara bertahap.\n- Hindari menyimpan rokok dan korek di tempat yang mudah dijangkau.");
        tVTarget.setText("- Kurangi 3-5 batang rokok per hari secara bertahap.\n- Tunda rokok pertama selama mungkin setelah bangun tidur.\n- Catat pemicu merokok dan cari pengganti aktivitas yang lebih sehat.");

        tVJawaban1.setTextColor(warna);
        tVJawaban2.setTextColor(warna);
    }

    private void setKategoriKosong() {
        layoutkategori.setBackgroundResource(R.drawable.bg_smoker_card);
        iVKategori.setBackgroundResource(R.drawable.circle_dark);
        iVKategori.setImageResource(R.drawable.ic_paru_hijau);
        iVKategori.clearColorFilter();

        tVKategori.setText("-");
        tVKategori.setTextColor(ContextCompat.getColor(this, R.color.black));
        tVDeskripsi.setText("Data hasil pemeriksaan tidak ditemukan.");
        tVSaran.setText("-");
        tVTarget.setText("-");

        tVJawaban1.setTextColor(ContextCompat.getColor(this, R.color.black));
        tVJawaban2.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private String getStatusPerokok(int totalScore) {
        if (totalScore <= 3) {
            return "perokok ringan";
        } else if (totalScore <= 6) {
            return "perokok sedang";
        } else {
            return "perokok berat";
        }
    }

    private String getInterpretasiSkor(int score) {
        if (score < 0) {
            return "-";
        } else if (score <= 3) {
            return "Ketergantungan nikotin rendah";
        } else if (score <= 6) {
            return "Ketergantungan nikotin sedang";
        } else {
            return "Ketergantungan nikotin tinggi";
        }
    }

    private String getScoreText() {
        if (totalPoin < 0) return "-";
        return totalPoin + "/10";
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
