package example.com.fielthyapps.Feature.Nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;


public class HasilBMRActivity extends AppCompatActivity {

    private TextView tvBmr,tvTdee, tvAktivitas, tvFaktor, tvTurun, tvNormal, tvNaik, tvGender, tvUmur, tvBerat, tvTinggi, tvAktivitasDetail;
    private Button btnSelesai;
    private LinearLayout llMakananSehat, llDietPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_bmr);

        tvBmr = findViewById(R.id.tv_bmr);
        tvTdee = findViewById(R.id.tv_tdee);
        tvAktivitas = findViewById(R.id.tv_aktivitas);
        tvFaktor = findViewById(R.id.tv_faktor);
        tvTurun = findViewById(R.id.tV_turun);
        tvNormal = findViewById(R.id.tv_normal);
        tvNaik = findViewById(R.id.tv_naik);
        tvGender = findViewById(R.id.tv_gender);
        tvUmur = findViewById(R.id.tv_umur);
        tvBerat = findViewById(R.id.tv_berat);
        tvTinggi = findViewById(R.id.tv_tinggi);
        tvAktivitasDetail = findViewById(R.id.tv_aktivitas_detail);
        btnSelesai = findViewById(R.id.btn_selesai);
        llMakananSehat = findViewById(R.id.ll_makanan_sehat);
        llDietPlan = findViewById(R.id.ll_diet_plan);
        btnSelesai.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            HasilBMRActivity.this,
                            HomeActivity.class
                    );
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
            );

            startActivity(intent);
            finish();

        });

        llMakananSehat.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HasilBMRActivity.this,
                            MakananSehatActivity.class
                    );

            startActivity(intent);
        });

        llDietPlan.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HasilBMRActivity.this,
                            DietSehatActivity.class
                    );

            startActivity(intent);
        });

        long bmr =
                getIntent().getLongExtra("bmr", 0);

        long tdee =
                getIntent().getLongExtra("tdee", 0);

        String aktivitas =
                getIntent().getStringExtra("aktivitas");

        double faktor = getIntent().getDoubleExtra("faktor", 0);
        long turun = getIntent().getLongExtra("turun", 0);

        long normal = getIntent().getLongExtra("normal", 0);

        long naik = getIntent().getLongExtra("naik", 0);

        String gender = getIntent().getStringExtra("gender");

        int umur = getIntent().getIntExtra("umur", 0);

        double berat = getIntent().getDoubleExtra("berat", 0);

        double tinggi = getIntent().getDoubleExtra("tinggi", 0);
        NumberFormat format = NumberFormat.getInstance(new Locale("id", "ID"));

        tvBmr.setText(format.format(bmr));
        tvTdee.setText(format.format(tdee));

        tvAktivitas.setText(aktivitas);
        tvGender.setText(gender);

        tvUmur.setText(
                umur + " Tahun"
        );

        tvBerat.setText(
                format.format(berat) + " kg"
        );

        tvTinggi.setText(
                format.format(tinggi) + " cm"
        );

        tvAktivitasDetail.setText(
                aktivitas
        );
        tvFaktor.setText(
                String.format(
                        Locale.US,
                        "%.3f",
                        faktor
                )
        );

        tvTurun.setText(
                format.format(turun) + " kkal/hari"
        );

        tvNormal.setText(
                format.format(normal) + " kkal/hari"
        );

        tvNaik.setText(
                format.format(naik) + " kkal/hari"
        );
    }
}