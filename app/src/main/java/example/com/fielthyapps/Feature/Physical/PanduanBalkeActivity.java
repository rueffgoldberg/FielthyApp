package example.com.fielthyapps.Feature.Physical;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import example.com.fielthyapps.R;

public class PanduanBalkeActivity extends AppCompatActivity {

    private ImageView iV_kembali, iV_volume;
    private TextToSpeech textToSpeech;

    private final String panduanBalke =
            "Panduan Balke Test. " +
                    "Balke Test adalah tes untuk mengukur daya tahan kardiorespirasi " +
                    "dengan berjalan atau berlari selama lima belas menit. " +

                    "Langkah pertama, aktifkan GPS lalu tekan tombol Start Tracking. " +

                    "Langkah kedua, lakukan aktivitas berjalan atau berlari selama lima belas menit secara konsisten. " +

                    "Langkah ketiga, setelah waktu habis hentikan aktivitas. " +

                    "Langkah keempat, lihat hasil pemeriksaan dan estimasi VO dua max yang ditampilkan aplikasi. " +

                    "Catatan penting. Hentikan tes apabila mengalami pusing, nyeri dada, sesak napas berlebihan, atau kondisi tidak nyaman lainnya. " +

                    "Tips. Lakukan pemanasan sebelum memulai dan pendinginan setelah selesai untuk menghindari cedera.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panduan_balke);

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
                        panduanBalke,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "PANDUAN_BALKE"
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