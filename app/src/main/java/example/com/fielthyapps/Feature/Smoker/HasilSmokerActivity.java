package example.com.fielthyapps.Feature.Smoker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class HasilSmokerActivity extends AppCompatActivity {
    private LinearLayout layoutkategori;
    private ImageView iV_back, iV_kategori;
    private TextView tV_kategori, tV_deskripsi, tV_jawaban1, tV_jawaban2;
    private Button btnKalkulatorPenghematan, btnKembaliSmoker;
    private ImageButton iB_volume;
    private String jawaban1, jawaban2, statusPerokok;
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

        iV_back = findViewById(R.id.iV_kembali);
        iV_kategori = findViewById(R.id.iV_kategori);

        tV_kategori = findViewById(R.id.tV_kategori);
        tV_deskripsi = findViewById(R.id.tV_deskripsi);
        tV_jawaban1 = findViewById(R.id.tV_jawaban1);
        tV_jawaban2 = findViewById(R.id.tV_jawaban2);

        btnKalkulatorPenghematan = findViewById(R.id.btn_kalkulator_penghematan);
        btnKembaliSmoker = findViewById(R.id.btn_kembali_smoker);

        iB_volume = findViewById(R.id.iB_volume);
        tts = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {

                tts.setLanguage(new Locale("id", "ID"));

                tts.setSpeechRate(1.0f);

            }
        });

        Intent intent = getIntent();
        jawaban1 = intent.getStringExtra("jawaban_pertanyaan_1");
        jawaban2 = intent.getStringExtra("jawaban_pertanyaan_2");
        statusPerokok = intent.getStringExtra("status_perokok");

        String id = intent.getStringExtra("id");

        if (id != null &&
                (jawaban1 == null || statusPerokok == null)) {

            DatabaseHelper dbHelper = new DatabaseHelper(this);

            HashMap<String, String> record =
                    dbHelper.getRecordById(
                            DatabaseHelper.TABLE_SMOKER,
                            id);

            if (record != null && !record.isEmpty()) {

                jawaban1 = record.get("jawaban_pertanyaan_1");
                jawaban2 = record.get("jawaban_pertanyaan_2");
                statusPerokok = record.get("status_perokok");
            }
        }

        tampilkanHasil();

        View.OnClickListener kembaliListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kembaliKeSmoker();
            }
        };

        iV_back.setOnClickListener(kembaliListener);
        btnKembaliSmoker.setOnClickListener(kembaliListener);

        btnKalkulatorPenghematan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HasilSmokerActivity.this, KalkulatorMerokokActivity.class);
                startActivity(intent);
            }
        });
        iB_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(
                        HasilSmokerActivity.this,
                        "Sedang memuat suara... mohon tunggu.",
                        Toast.LENGTH_SHORT
                ).show();

                if (tts != null) {

                    try {

                        String speechText =
                                "Hasil Pemeriksaan Kebiasaan Merokok. " +
                                        "Kategori Anda adalah " +
                                        tV_kategori.getText().toString() +
                                        ". " +
                                        tV_deskripsi.getText().toString();

                        tts.speak(
                                speechText,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "HASIL_SMOKER"
                        );

                    } catch (Exception e) {

                        Toast.makeText(
                                HasilSmokerActivity.this,
                                e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
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
        tV_jawaban1.setText(getTextJawabanPertanyaan1(jawaban1));
        tV_jawaban2.setText(getTextJawabanPertanyaan2(jawaban2));

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
        iV_kategori.setBackgroundResource(R.drawable.circle_blue);
        iV_kategori.setImageResource(R.drawable.ic_paru_hijau);
        iV_kategori.clearColorFilter();

        tV_kategori.setText("PEROKOK RINGAN");
        tV_kategori.setTextColor(warna);
        tV_deskripsi.setText("Berdasarkan jawaban Anda, tingkat ketergantungan nikotin termasuk kategori ringan.");

        tV_jawaban1.setTextColor(warna);
        tV_jawaban2.setTextColor(warna);
    }

    private void setKategoriSedang() {
        int warna = ContextCompat.getColor(this, R.color.orange);

        layoutkategori.setBackgroundResource(R.drawable.bg_smoker_result_card_sedang);
        iV_kategori.setBackgroundResource(R.drawable.circle_yellow);
        iV_kategori.setImageResource(R.drawable.ic_paru_kuning);
        iV_kategori.clearColorFilter();

        tV_kategori.setText("PEROKOK SEDANG");
        tV_kategori.setTextColor(warna);
        tV_deskripsi.setText("Berdasarkan jawaban Anda, tingkat ketergantungan nikotin termasuk kategori sedang.");

        tV_jawaban1.setTextColor(warna);
        tV_jawaban2.setTextColor(warna);
    }

    private void setKategoriBerat() {
        int warna = ContextCompat.getColor(this, R.color.red);

        layoutkategori.setBackgroundResource(R.drawable.bg_smoker_result_card_berat);
        iV_kategori.setBackgroundResource(R.drawable.circle_red);
        iV_kategori.setImageResource(R.drawable.ic_paru_merah);
        iV_kategori.clearColorFilter();

        tV_kategori.setText("PEROKOK BERAT");
        tV_kategori.setTextColor(warna);
        tV_deskripsi.setText("Berdasarkan jawaban Anda, tingkat ketergantungan nikotin termasuk kategori berat.");

        tV_jawaban1.setTextColor(warna);
        tV_jawaban2.setTextColor(warna);
    }

    private void setKategoriKosong() {
        layoutkategori.setBackgroundResource(R.drawable.bg_smoker_card);
        iV_kategori.setBackgroundResource(R.drawable.circle_dark);
        iV_kategori.setImageResource(R.drawable.ic_paru_hijau);
        iV_kategori.clearColorFilter();

        tV_kategori.setText("-");
        tV_kategori.setTextColor(ContextCompat.getColor(this, R.color.black));
        tV_deskripsi.setText("Data hasil pemeriksaan tidak ditemukan.");

        tV_jawaban1.setTextColor(ContextCompat.getColor(this, R.color.black));
        tV_jawaban2.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private String getTextJawabanPertanyaan1(String jawaban) {
        if (jawaban == null) return "-";

        switch (jawaban) {
            case "A":
                return "A. Kurang dari 5 menit";
            case "B":
                return "B. 6 - 30 menit";
            case "C":
                return "C. 31 - 60 menit";
            case "D":
                return "D. Lebih dari 60 menit";
            default:
                return "-";
        }
    }

    private String getTextJawabanPertanyaan2(String jawaban) {
        if (jawaban == null) return "-";

        switch (jawaban) {
            case "A":
                return "A. 10 batang atau kurang";
            case "B":
                return "B. 11 - 20 batang";
            case "C":
                return "C. 21 - 30 batang";
            case "D":
                return "D. 31 batang atau lebih";
            default:
                return "-";
        }
    }
}