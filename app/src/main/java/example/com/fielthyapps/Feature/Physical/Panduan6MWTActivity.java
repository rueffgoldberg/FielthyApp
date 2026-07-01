package example.com.fielthyapps.Feature.Physical;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import example.com.fielthyapps.R;

public class Panduan6MWTActivity extends AppCompatActivity {

    private ImageView iV_kembali, iV_volume;
    private TextToSpeech textToSpeech;

    private final String panduan6MWT =
            "Panduan Six Minute Walk Test. " +
                    "Six Minute Walk Test adalah tes untuk mengukur kemampuan fungsional dan daya tahan seseorang dengan berjalan selama enam menit. " +

                    "Langkah pertama, aktifkan GPS lalu tekan tombol Start Tracking. " +

                    "Langkah kedua, berjalanlah selama enam menit dengan kecepatan yang nyaman tanpa berlari. " +

                    "Langkah ketiga, setelah enam menit selesai, hentikan aktivitas dan akhiri tracking. " +

                    "Langkah keempat, lihat hasil pemeriksaan yang menampilkan jarak tempuh dan evaluasi kebugaran. " +

                    "Catatan penting. Hentikan tes apabila mengalami pusing, nyeri dada, sesak napas berlebihan, atau kondisi tidak nyaman lainnya. " +

                    "Tips. Gunakan sepatu yang nyaman, lakukan pemanasan ringan sebelum tes, dan berjalanlah di area yang aman dan datar.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panduan_6mwt);

        initViews();
        setupTTS();
        setupListeners();
    }

    private void initViews() {
        iV_kembali = findViewById(R.id.iV_kembali);
        iV_volume = findViewById(R.id.iV_volume);
    }

    private void setupListeners() {

        iV_kembali.setOnClickListener(v -> finish());

        iV_volume.setOnClickListener(v -> {

            if (textToSpeech != null) {

                textToSpeech.stop();

                textToSpeech.speak(
                        panduan6MWT,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "PANDUAN_6MWT"
                );
            }
        });
    }

    private void setupTTS() {

        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {

                textToSpeech.setLanguage(new Locale("id", "ID"));
                textToSpeech.setSpeechRate(0.9f);
                textToSpeech.setPitch(1.0f);
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}