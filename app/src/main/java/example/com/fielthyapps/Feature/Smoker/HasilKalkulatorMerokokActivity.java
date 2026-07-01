package example.com.fielthyapps.Feature.Smoker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class HasilKalkulatorMerokokActivity extends AppCompatActivity {

    private TextView tvTotalBatang;
    private TextView tvLamaMerokok;

    private TextView tvHari;
    private TextView tvBulan;
    private TextView tvTahun;

    private TextView tvBiayaHari;
    private TextView tvBiayaBulan;
    private TextView tvBiayaTahun;

    private TextView tvTotalPengeluaran;
    private TextView tvPeriode;

    private TextView tvHematBulan;
    private TextView tvHemat6Bulan;
    private TextView tvHematTahun;

    private ImageView ivKembali;
    private Button btnKeBeranda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_kalkulator_merokok);

        initView();
        loadData();

        ivKembali.setOnClickListener(v -> finish());

        btnKeBeranda.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void initView() {

        ivKembali = findViewById(R.id.iV_kembali);
        btnKeBeranda = findViewById(R.id.btn_ke_beranda);

        tvTotalBatang = findViewById(R.id.tv_total_batang);
        tvLamaMerokok = findViewById(R.id.tv_lama_merokok);

        tvHari = findViewById(R.id.tv_hari);
        tvBulan = findViewById(R.id.tv_bulan);
        tvTahun = findViewById(R.id.tv_tahun);

        tvBiayaHari = findViewById(R.id.tv_biaya_hari);
        tvBiayaBulan = findViewById(R.id.tv_biaya_bulan);
        tvBiayaTahun = findViewById(R.id.tv_biaya_tahun);

        tvTotalPengeluaran =
                findViewById(R.id.tv_total_pengeluaran);

        tvPeriode =
                findViewById(R.id.tv_periode);

        tvHematBulan =
                findViewById(R.id.tv_hemat_bulan);

        tvHemat6Bulan =
                findViewById(R.id.tv_hemat_6bulan);

        tvHematTahun =
                findViewById(R.id.tv_hemat_tahun);
    }

    private void loadData() {

        int batangHari =
                getIntent().getIntExtra(
                        "batang_hari",
                        0
                );

        int batangBulan =
                getIntent().getIntExtra(
                        "batang_bulan",
                        0
                );

        int batangTahun =
                getIntent().getIntExtra(
                        "batang_tahun",
                        0
                );

        int totalBatang =
                getIntent().getIntExtra(
                        "total_batang",
                        0
                );

        int lamaMerokok =
                getIntent().getIntExtra(
                        "lama_merokok",
                        0
                );

        double biayaHari =
                getIntent().getDoubleExtra(
                        "biaya_hari",
                        0
                );

        double biayaBulan =
                getIntent().getDoubleExtra(
                        "biaya_bulan",
                        0
                );

        double biayaTahun =
                getIntent().getDoubleExtra(
                        "biaya_tahun",
                        0
                );

        double totalBiaya =
                getIntent().getDoubleExtra(
                        "total_biaya",
                        0
                );

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        rupiah.setMaximumFractionDigits(0);

        // TOTAL BATANG ROKOK
        tvTotalBatang.setText(
                String.format(
                        Locale.getDefault(),
                        "%,d",
                        totalBatang
                )
        );

        tvLamaMerokok.setText(
                "Batang rokok yang telah dikonsumsi selama "
                        + lamaMerokok
                        + " tahun"
        );

        // KONSUMSI ROKOK
        tvHari.setText(
                batangHari + " batang"
        );

        tvBulan.setText(
                batangBulan + " batang"
        );

        tvTahun.setText(
                String.format(
                        Locale.getDefault(),
                        "%,d",
                        batangTahun
                ) + " batang"
        );

        // PENGELUARAN
        tvBiayaHari.setText(
                rupiah.format(biayaHari)
        );

        tvBiayaBulan.setText(
                rupiah.format(biayaBulan)
        );

        tvBiayaTahun.setText(
                rupiah.format(biayaTahun)
        );

        // TOTAL PENGELUARAN
        tvTotalPengeluaran.setText(
                rupiah.format(totalBiaya)
        );

        tvPeriode.setText(
                "Selama "
                        + lamaMerokok
                        + " Tahun"
        );

        // PENGHEMATAN
        tvHematBulan.setText(
                rupiah.format(
                        biayaBulan
                )
        );

        tvHemat6Bulan.setText(
                rupiah.format(
                        biayaBulan * 6
                )
        );

        tvHematTahun.setText(
                rupiah.format(
                        biayaTahun
                )
        );
    }
}