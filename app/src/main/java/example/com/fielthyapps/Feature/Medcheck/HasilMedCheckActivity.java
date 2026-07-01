package example.com.fielthyapps.Feature.Medcheck;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import example.com.fielthyapps.Feature.History.HistoryActivity;
import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class HasilMedCheckActivity extends AppCompatActivity {
    private TextView imt, lingkarperut, tekanandarah, guladarah, lemak, status_imt, status_lingkar, status_tekanan, status_gula, status_lemak, tV_status_kesehatan, tV_desc_status;
    private TextView tV_interpretasi_imt, tV_risiko_imt, tV_rekomendasi_imt;
    private TextView tV_interpretasi_lingkar, tV_risiko_lingkar, tV_rekomendasi_lingkar;
    private TextView tV_interpretasi_tekanan, tV_risiko_tekanan, tV_rekomendasi_tekanan;
    private TextView tV_interpretasi_gula, tV_risiko_gula, tV_rekomendasi_gula;
    private TextView tV_interpretasi_lemak, tV_risiko_lemak, tV_rekomendasi_lemak;
    private String status, date, uid, id, get_berat, get_tinggi, get_lingkar_perut, get_sistolik, get_diastolik, get_lemak, get_guladarah, get_bmi, get_gender;
    private Button btn_selesai;
    private TextToSpeech textToSpeech;
    private String textLaporanKesehatan = "";
    private ImageView iV_kondisi, iV_expand_imt, iV_expand_lingkar, iV_expand_tekanandarah, iV_expand_gula, iV_expand_lemak, imgBtnHasilPemeriksaan;
    private LinearLayout detail_imt, detail_lingkar, detail_tekanan, detail_gula, detail_lemak;
    private LinearLayout header_imt, header_lingkar, header_tekanan, header_gula, header_lemak;
    private void closeAllDropdown() {
        detail_imt.setVisibility(View.GONE);
        detail_lingkar.setVisibility(View.GONE);
        detail_tekanan.setVisibility(View.GONE);
        detail_gula.setVisibility(View.GONE);
        detail_lemak.setVisibility(View.GONE);

        iV_expand_imt.setRotation(0);
        iV_expand_lingkar.setRotation(0);
        iV_expand_tekanandarah.setRotation(0);
        iV_expand_gula.setRotation(0);
        iV_expand_lemak.setRotation(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_med_check);
        imt = findViewById(R.id.tV_hasil_imt);
        lingkarperut = findViewById(R.id.tV_hasil_lingkar);
        tekanandarah = findViewById(R.id.tV_hasil_tekanan);
        guladarah = findViewById(R.id.tV_hasil_gula);
        btn_selesai = findViewById(R.id.btn_selesai);
        lemak = findViewById(R.id.tV_hasil_lemak);
        status_imt = findViewById(R.id.tV_status_imt);
        status_lingkar = findViewById(R.id.tV_status_lingkar);
        status_tekanan = findViewById(R.id.tV_status_tekanan);
        status_gula = findViewById(R.id.tV_status_gula);
        status_lemak = findViewById(R.id.tV_status_lemak);
        tV_status_kesehatan = findViewById(R.id.tV_status_kesehatan);
        tV_desc_status = findViewById(R.id.tV_desc_status);
        iV_kondisi = findViewById(R.id.iV_kondisi);
        detail_imt = findViewById(R.id.detail_imt);
        detail_lingkar = findViewById(R.id.detail_lingkar);
        detail_tekanan = findViewById(R.id.detail_tekanandarah);
        detail_gula = findViewById(R.id.detail_gula);
        detail_lemak = findViewById(R.id.detail_lemak);
        header_imt =  findViewById(R.id.header_imt);
        header_lingkar =  findViewById(R.id.header_lingkar);
        header_tekanan =  findViewById(R.id.header_tekanan);
        header_gula =  findViewById(R.id.header_gula);
        header_lemak =  findViewById(R.id.header_lemak);
        iV_expand_imt = findViewById(R.id.iV_expand_imt);
        iV_expand_lingkar = findViewById(R.id.iV_expand_lingkar);
        iV_expand_tekanandarah = findViewById(R.id.iV_expand_tekanandarah);
        iV_expand_gula = findViewById(R.id.iV_expand_gula);
        iV_expand_lemak = findViewById(R.id.iV_expand_lemak);
        imgBtnHasilPemeriksaan = findViewById(R.id.imgBtnHasilPemeriksaan);
        // IMT
        tV_interpretasi_imt = findViewById(R.id.tV_interpretasi_imt);
        tV_risiko_imt = findViewById(R.id.tV_risiko_imt);
        tV_rekomendasi_imt = findViewById(R.id.tV_rekomendasi_imt);

// Lingkar Perut
        tV_interpretasi_lingkar = findViewById(R.id.tV_interpretasi_lingkar);
        tV_risiko_lingkar = findViewById(R.id.tV_risiko_lingkar);
        tV_rekomendasi_lingkar = findViewById(R.id.tV_rekomendasi_lingkar);

// Tekanan Darah
        tV_interpretasi_tekanan = findViewById(R.id.tV_interpretasi_tekanan);
        tV_risiko_tekanan = findViewById(R.id.tV_risiko_tekanan);
        tV_rekomendasi_tekanan = findViewById(R.id.tV_rekomendasi_tekanan);

// Gula Darah
        tV_interpretasi_gula = findViewById(R.id.tV_interpretasi_gula);
        tV_risiko_gula = findViewById(R.id.tV_risiko_gula);
        tV_rekomendasi_gula = findViewById(R.id.tV_rekomendasi_gula);

// Kolesterol
        tV_interpretasi_lemak = findViewById(R.id.tV_interpretasi_lemak);
        tV_risiko_lemak = findViewById(R.id.tV_risiko_lemak);
        tV_rekomendasi_lemak = findViewById(R.id.tV_rekomendasi_lemak);
        Intent iin = getIntent();
        final Bundle b = iin.getExtras();
        textToSpeech = new TextToSpeech(
                this,
                status -> {

                    if (status == TextToSpeech.SUCCESS) {

                        textToSpeech.setLanguage(
                                new Locale("id", "ID")
                        );

                        textToSpeech.setSpeechRate(0.9f);

                        textToSpeech.setPitch(1.0f);
                    }
                }
        );


        if (b != null) {
            id = (String) b.get("id");
            date = (String) b.get("date");
            uid = (String) b.get("uid");
            get_gender = (String) b.get("gender");
            get_berat = (String) b.get("berat");
            get_tinggi = (String) b.get("tinggi");
            get_lingkar_perut = (String) b.get("lingkarperut");
            get_sistolik = (String) b.get("sistolik");
            get_diastolik = (String) b.get("diastolik");
            get_guladarah = (String) b.get("guladarah");
            get_lemak = (String) b.get("lemak");
            get_bmi = (String) b.get("hasilbmi");
            status = (String) b.get("status");
            imt.setText(get_bmi + " kg/m2");
            lingkarperut.setText(get_lingkar_perut + " cm");
            tekanandarah.setText(get_sistolik + "/" + get_diastolik + " mmHg");
            lemak.setText(get_lemak + " mg/dL");
            guladarah.setText(get_guladarah + " mg/dL");

            // Ganti koma dengan titik
            String valueWithDot = get_bmi.replace(',', '.');

            // Konversi string menjadi double
            double doubleValue = Double.parseDouble(valueWithDot);

            // Membulatkan double ke bilangan bulat terdekat
            int edu_score_imt = (int) Math.round(doubleValue);
//            int edu_score_imt = Integer.parseInt(get_bmi);


            int edu_lingkar = Integer.parseInt(get_lingkar_perut);
            int edu_sistolik = Integer.parseInt(get_sistolik);
            int edu_diastolik = Integer.parseInt(get_diastolik);
            int edu_gula = Integer.parseInt(get_guladarah);
            int edu_lemak = Integer.parseInt(get_lemak);
            String kondisiKesehatan = "Normal";

            // status imt
            if (edu_score_imt >= 27.1) {

                status_imt.setText("🚨 Waspada");
                status_imt.setBackgroundResource(R.drawable.bg_status_waspada);

                setDetail(
                        tV_interpretasi_imt,
                        tV_risiko_imt,
                        tV_rekomendasi_imt,
                        "IMT menunjukkan obesitas.",
                        "Risiko diabetes, hipertensi, stroke dan penyakit jantung meningkat.",
                        "• Kurangi asupan kalori\n• Tingkatkan aktivitas fisik\n• Konsultasi tenaga kesehatan"
                );
                textLaporanKesehatan +=
                        "Indeks Massa Tubuh. " +
                                tV_interpretasi_imt.getText().toString() + ". " +
                                tV_risiko_imt.getText().toString() + ". ";

            } else if (edu_score_imt >= 25.0) {

                status_imt.setText("⚠ Perhatian");
                status_imt.setBackgroundResource(R.drawable.bg_status_perhatian);

                setDetail(
                        tV_interpretasi_imt,
                        tV_risiko_imt,
                        tV_rekomendasi_imt,
                        "IMT menunjukkan kelebihan berat badan.",
                        "Risiko penyakit metabolik mulai meningkat.",
                        "• Atur pola makan\n• Perbanyak olahraga\n• Pantau berat badan"
                );
                textLaporanKesehatan +=
                        "Indeks Massa Tubuh. " +
                                tV_interpretasi_imt.getText().toString() + ". " +
                                tV_risiko_imt.getText().toString() + ". ";

            } else {

                status_imt.setText("✓ Normal");
                status_imt.setBackgroundResource(R.drawable.bg_status_normal);

                setDetail(
                        tV_interpretasi_imt,
                        tV_risiko_imt,
                        tV_rekomendasi_imt,
                        "IMT berada dalam rentang normal.",
                        "Risiko penyakit akibat berat badan rendah.",
                        "• Pertahankan pola hidup sehat\n• Olahraga rutin\n• Jaga berat badan ideal"
                );
                textLaporanKesehatan +=
                        "Indeks Massa Tubuh. " +
                                tV_interpretasi_imt.getText().toString() + ". " +
                                tV_risiko_imt.getText().toString() + ". ";
            }

            //status lingkar perut
            if (get_gender.equals("Laki - Laki")) {
                if (edu_lingkar >= 90) {
                    status_lingkar.setText("⚠ Perhatian");
                    status_lingkar.setBackgroundResource(R.drawable.bg_status_perhatian);

                    setDetail(
                            tV_interpretasi_lingkar,
                            tV_risiko_lingkar,
                            tV_rekomendasi_lingkar,
                            "Lingkar perut melebihi batas normal untuk laki-laki (≥90 cm).",
                            "Risiko obesitas sentral, diabetes melitus, hipertensi, dan penyakit jantung meningkat.",
                            "• Kurangi makanan tinggi lemak dan gula\n" +
                                    "• Perbanyak aktivitas fisik minimal 150 menit/minggu\n" +
                                    "• Perbanyak konsumsi sayur dan buah\n" +
                                    "• Pantau lingkar perut secara berkala"
                    );
                    textLaporanKesehatan +=
                            "Lingkar Perut. " +
                                    tV_interpretasi_lingkar.getText().toString() + ". " +
                                    tV_risiko_lingkar.getText().toString() + ". ";
                    kondisiKesehatan = "Perhatian";
                } else {
                    status_lingkar.setText("✓ Normal");
                    status_lingkar.setBackgroundResource(R.drawable.bg_status_normal);
                    setDetail(
                            tV_interpretasi_lingkar,
                            tV_risiko_lingkar,
                            tV_rekomendasi_lingkar,
                            "Lingkar perut masih dalam batas normal untuk laki-laki.",
                            "Risiko obesitas sentral relatif rendah.",
                            "• Pertahankan pola makan sehat\n" +
                                    "• Tetap aktif bergerak\n" +
                                    "• Jaga berat badan ideal"
                    );
                    textLaporanKesehatan +=
                            "Lingkar Perut. " +
                                    tV_interpretasi_lingkar.getText().toString() + ". " +
                                    tV_risiko_lingkar.getText().toString() + ". ";
                }
            }

            if (get_gender.equals("Perempuan")) {
                if (edu_lingkar >= 80) {
                    status_lingkar.setText("⚠ Perhatian");
                    status_lingkar.setBackgroundResource(R.drawable.bg_status_perhatian);
                    setDetail(
                            tV_interpretasi_lingkar,
                            tV_risiko_lingkar,
                            tV_rekomendasi_lingkar,
                            "Lingkar perut melebihi batas normal untuk perempuan (≥80 cm).",
                            "Risiko penyakit metabolik dan kardiovaskular meningkat.",
                            "• Kurangi makanan tinggi kalori\n" +
                                    "• Tingkatkan aktivitas fisik\n" +
                                    "• Perbanyak konsumsi serat\n" +
                                    "• Pantau lingkar perut secara rutin"
                    );
                    textLaporanKesehatan +=
                            "Lingkar Perut. " +
                                    tV_interpretasi_lingkar.getText().toString() + ". " +
                                    tV_risiko_lingkar.getText().toString() + ". ";

                    kondisiKesehatan = "Perhatian";

                } else {

                    status_lingkar.setText("✓ Normal");
                    status_lingkar.setBackgroundResource(R.drawable.bg_status_normal);

                    setDetail(
                            tV_interpretasi_lingkar,
                            tV_risiko_lingkar,
                            tV_rekomendasi_lingkar,
                            "Lingkar perut masih dalam batas normal untuk perempuan.",
                            "Risiko gangguan metabolik rendah.",
                            "• Pertahankan gaya hidup sehat\n" +
                                    "• Rutin berolahraga\n" +
                                    "• Konsumsi makanan bergizi seimbang"
                    );
                    textLaporanKesehatan +=
                            "Lingkar Perut. " +
                                    tV_interpretasi_lingkar.getText().toString() + ". " +
                                    tV_risiko_lingkar.getText().toString() + ". ";
                }
            }

            //status tekanan darah
            if (edu_sistolik < 130 && edu_diastolik <= 84) {

                status_tekanan.setText("✓ Normal");
                status_tekanan.setBackgroundResource(R.drawable.bg_status_normal);

                setDetail(
                        tV_interpretasi_tekanan,
                        tV_risiko_tekanan,
                        tV_rekomendasi_tekanan,
                        "Tekanan darah berada dalam rentang normal.",
                        "Risiko hipertensi relatif rendah.",
                        "• Pertahankan pola hidup sehat\n" +
                                "• Batasi konsumsi garam\n" +
                                "• Rutin berolahraga\n" +
                                "• Kelola stres dengan baik"
                );
                textLaporanKesehatan +=
                        "Tekanan Darah. " +
                                tV_interpretasi_tekanan.getText().toString() + ". " +
                                tV_risiko_tekanan.getText().toString() + ". ";

            } else if (edu_sistolik <= 139 && edu_diastolik <= 89) {

                status_tekanan.setText("⚠ Perhatian");
                status_tekanan.setBackgroundResource(R.drawable.bg_status_perhatian);

                setDetail(
                        tV_interpretasi_tekanan,
                        tV_risiko_tekanan,
                        tV_rekomendasi_tekanan,
                        "Tekanan darah berada pada kategori pra-hipertensi.",
                        "Risiko hipertensi dan penyakit jantung mulai meningkat.",
                        "• Kurangi konsumsi garam\n" +
                                "• Hindari merokok\n" +
                                "• Jaga berat badan ideal\n" +
                                "• Rutin memeriksa tekanan darah"
                );
                textLaporanKesehatan +=
                        "Tekanan Darah. " +
                                tV_interpretasi_tekanan.getText().toString() + ". " +
                                tV_risiko_tekanan.getText().toString() + ". ";

                kondisiKesehatan = "Perhatian";

            } else {

                status_tekanan.setText("🚨 Waspada");
                status_tekanan.setBackgroundResource(R.drawable.bg_status_waspada);

                setDetail(
                        tV_interpretasi_tekanan,
                        tV_risiko_tekanan,
                        tV_rekomendasi_tekanan,
                        "Tekanan darah menunjukkan hipertensi.",
                        "Risiko stroke, gagal jantung dan penyakit ginjal meningkat.",
                        "• Segera konsultasi ke tenaga kesehatan\n" +
                                "• Batasi konsumsi garam\n" +
                                "• Hindari rokok dan alkohol\n" +
                                "• Lakukan pemantauan tekanan darah secara berkala"
                );
                textLaporanKesehatan +=
                        "Tekanan Darah. " +
                                tV_interpretasi_tekanan.getText().toString() + ". " +
                                tV_risiko_tekanan.getText().toString() + ". ";

                kondisiKesehatan = "Waspada";
            }

            //status gula darah
            if (edu_gula < 200) {

                status_gula.setText("✓ Normal");
                status_gula.setBackgroundResource(R.drawable.bg_status_normal);

                setDetail(
                        tV_interpretasi_gula,
                        tV_risiko_gula,
                        tV_rekomendasi_gula,
                        "Kadar gula darah sewaktu masih dalam batas normal.",
                        "Risiko diabetes melitus relatif rendah.",
                        "• Batasi konsumsi gula berlebih\n" +
                                "• Pertahankan pola makan sehat\n" +
                                "• Rutin berolahraga\n" +
                                "• Lakukan pemeriksaan berkala"
                );
                textLaporanKesehatan +=
                        "Gula Darah. " +
                                tV_interpretasi_gula.getText().toString() + ". " +
                                tV_risiko_gula.getText().toString() + ". ";

            } else {

                status_gula.setText("🚨 Waspada");
                status_gula.setBackgroundResource(R.drawable.bg_status_waspada);

                setDetail(
                        tV_interpretasi_gula,
                        tV_risiko_gula,
                        tV_rekomendasi_gula,
                        "Kadar gula darah sewaktu tinggi.",
                        "Risiko diabetes melitus meningkat.",
                        "• Kurangi makanan dan minuman manis\n" +
                                "• Tingkatkan aktivitas fisik\n" +
                                "• Konsultasi ke dokter\n" +
                                "• Lakukan pemeriksaan gula darah lanjutan"
                );
                textLaporanKesehatan +=
                        "Gula Darah. " +
                                tV_interpretasi_gula.getText().toString() + ". " +
                                tV_risiko_gula.getText().toString() + ". ";

                kondisiKesehatan = "Waspada";
            }

            //status kolesterol
            if (edu_lemak <= 200) {

                status_lemak.setText("✓ Normal");
                status_lemak.setBackgroundResource(R.drawable.bg_status_normal);

                setDetail(
                        tV_interpretasi_lemak,
                        tV_risiko_lemak,
                        tV_rekomendasi_lemak,
                        "Kadar kolesterol total masih dalam batas normal.",
                        "Risiko penyakit jantung koroner relatif rendah.",
                        "• Pertahankan pola makan sehat\n" +
                                "• Perbanyak konsumsi serat\n" +
                                "• Rutin berolahraga\n" +
                                "• Hindari makanan tinggi lemak jenuh"
                );
                textLaporanKesehatan +=
                        "Kolesterol. " +
                                tV_interpretasi_lemak.getText().toString() + ". " +
                                tV_risiko_lemak.getText().toString() + ". ";

            } else {

                status_lemak.setText("⚠ Perhatian");
                status_lemak.setBackgroundResource(R.drawable.bg_status_perhatian);

                setDetail(
                        tV_interpretasi_lemak,
                        tV_risiko_lemak,
                        tV_rekomendasi_lemak,
                        "Kadar kolesterol total melebihi batas normal.",
                        "Risiko aterosklerosis, penyakit jantung dan stroke meningkat.",
                        "• Kurangi makanan berlemak jenuh\n" +
                                "• Perbanyak konsumsi sayur dan buah\n" +
                                "• Rutin berolahraga\n" +
                                "• Lakukan pemeriksaan kolesterol berkala"
                );
                textLaporanKesehatan +=
                        "Kolesterol. " +
                                tV_interpretasi_lemak.getText().toString() + ". " +
                                tV_risiko_lemak.getText().toString() + ". ";

                kondisiKesehatan = "Perhatian";
            }

            //Status card atas
            tV_status_kesehatan.setText(kondisiKesehatan);
            imgBtnHasilPemeriksaan.setOnClickListener(v -> {
                Toast.makeText(
                        HasilMedCheckActivity.this,
                        "Sedang memuat suara... mohon tunggu.",
                        Toast.LENGTH_SHORT
                ).show();
                try {

                    String finalSpeechText =
                            "Berikut adalah hasil pemeriksaan kesehatan Anda. "
                                    + textLaporanKesehatan;

                    textToSpeech.speak(
                            finalSpeechText,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "HASIL_MEDCHECK"
                    );
                } catch (Exception e) {

                    Toast.makeText(
                            HasilMedCheckActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

            if (kondisiKesehatan.equals("Normal")) {
                iV_kondisi.setImageResource(R.drawable.ic_kualitas_baik);
                tV_status_kesehatan.setTextColor(getResources().getColor(R.color.green));
                tV_desc_status.setText("Seluruh hasil pemeriksaan berada dalam batas normal. Pertahankan pola hidup sehat agar kondisi kesehatan tetap terjaga.");
            } else if (kondisiKesehatan.equals("Perhatian")) {
                iV_kondisi.setImageResource(R.drawable.ic_kualitas_perhatian);
                tV_status_kesehatan.setTextColor(getResources().getColor(R.color.orange));
                tV_desc_status.setText("Terdapat beberapa indikator yang perlu diperhatikan. Mulailah menerapkan pola hidup yang lebih sehat dan lakukan pemeriksaan secara berkala.");
            } else if (kondisiKesehatan.equals("Waspada")) {
                iV_kondisi.setImageResource(R.drawable.ic_kualitas_waspada);
                tV_status_kesehatan.setTextColor(getResources().getColor(R.color.red));
                tV_desc_status.setText("Beberapa hasil pemeriksaan berada di luar batas normal. Disarankan segera berkonsultasi dengan tenaga kesehatan untuk mendapatkan penanganan lebih lanjut.");
            }

            header_imt.setOnClickListener(v -> {
                if(detail_imt.getVisibility() == View.GONE){
                    closeAllDropdown();
                    detail_imt.setVisibility(View.VISIBLE);
                    iV_expand_imt.setRotation(180);
                } else {
                    detail_imt.setVisibility(View.GONE);
                    iV_expand_imt.setRotation(0);
                }
            });

            header_lingkar.setOnClickListener(v -> {
                if(detail_lingkar.getVisibility() == View.GONE){
                    closeAllDropdown();
                    detail_lingkar.setVisibility(View.VISIBLE);
                    iV_expand_lingkar.setRotation(180);
                } else {
                    detail_lingkar.setVisibility(View.GONE);
                    iV_expand_lingkar.setRotation(0);
                }
            });

            header_tekanan.setOnClickListener(v -> {
                if(detail_tekanan.getVisibility() == View.GONE){
                    closeAllDropdown();
                    detail_tekanan.setVisibility(View.VISIBLE);
                    iV_expand_tekanandarah.setRotation(180);
                } else {
                    detail_tekanan.setVisibility(View.GONE);
                    iV_expand_tekanandarah.setRotation(0);
                }
            });

            header_gula.setOnClickListener(v -> {
                if(detail_gula.getVisibility() == View.GONE){
                    closeAllDropdown();
                    detail_gula.setVisibility(View.VISIBLE);
                    iV_expand_gula.setRotation(180);
                } else {
                    detail_gula.setVisibility(View.GONE);
                    iV_expand_gula.setRotation(0);
                }
            });

            header_lemak.setOnClickListener(v -> {
                if(detail_lemak.getVisibility() == View.GONE){
                    closeAllDropdown();
                    detail_lemak.setVisibility(View.VISIBLE);
                    iV_expand_lemak.setRotation(180);
                } else {
                    detail_lemak.setVisibility(View.GONE);
                    iV_expand_lemak.setRotation(0);
                }
            });
        }

        btn_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("testmedcheck".equals(status)) {
                    startActivity(new Intent(HasilMedCheckActivity.this, HomeActivity.class));
                    finish();
                } else if ("historymedcheck".equals(status)) {
                    startActivity(new Intent(HasilMedCheckActivity.this, HistoryActivity.class));
                    finish();
                }
            }
        });
    }
    private void setDetail(
            TextView interpretasi,
            TextView risiko,
            TextView rekomendasi,
            String interpretasiText,
            String risikoText,
            String rekomendasiText
    ) {
        interpretasi.setText(interpretasiText);
        risiko.setText(risikoText);
        rekomendasi.setText(rekomendasiText);
    }
}