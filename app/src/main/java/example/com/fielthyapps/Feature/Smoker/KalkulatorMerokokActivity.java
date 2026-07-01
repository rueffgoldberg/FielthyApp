package example.com.fielthyapps.Feature.Smoker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.Database.SessionManager;
import example.com.fielthyapps.R;

public class KalkulatorMerokokActivity extends AppCompatActivity {

    private EditText etBatangPerHari;
    private EditText etHargaRokok;
    private EditText etIsiPerBungkus;
    private EditText etLamaMerokok;

    private Button btnHitung;
    private ImageView ivKembali;
    private String savedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalkulator_merokok);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        savedDate = sdf.format(new Date());

        etBatangPerHari = findViewById(R.id.etBatangPerHari);
        etHargaRokok = findViewById(R.id.etHargaRokok);
        etIsiPerBungkus = findViewById(R.id.etIsiPerBungkus);
        etLamaMerokok = findViewById(R.id.etLamaMerokok);

        btnHitung = findViewById(R.id.btn_lanjut_hasil_merokok);
        ivKembali = findViewById(R.id.iV_kembali);

        ivKembali.setOnClickListener(v -> finish());

        btnHitung.setOnClickListener(v -> hitungData());
    }

    private void hitungData() {

        String batangStr = etBatangPerHari.getText().toString().trim();
        String hargaStr = etHargaRokok.getText().toString().trim();
        String isiStr = etIsiPerBungkus.getText().toString().trim();
        String lamaStr = etLamaMerokok.getText().toString().trim();

        if (batangStr.isEmpty()
                || hargaStr.isEmpty()
                || isiStr.isEmpty()
                || lamaStr.isEmpty()) {

            Toast.makeText(
                    this,
                    "Lengkapi semua data terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int batangPerHari = Integer.parseInt(batangStr);
        double hargaPerBungkus = Double.parseDouble(hargaStr);
        int isiPerBungkus = Integer.parseInt(isiStr);
        int lamaMerokok = Integer.parseInt(lamaStr);

        if (batangPerHari <= 0
                || hargaPerBungkus <= 0
                || isiPerBungkus <= 0
                || lamaMerokok <= 0) {

            Toast.makeText(
                    this,
                    "Data yang dimasukkan tidak valid",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Konsumsi
        int perBulan = batangPerHari * 30;
        int perTahun = batangPerHari * 365;
        int totalBatang = perTahun * lamaMerokok;

        // Harga per batang
        double hargaPerBatang = hargaPerBungkus / isiPerBungkus;

        // Pengeluaran
        double biayaPerHari = batangPerHari * hargaPerBatang;
        double biayaPerBulan = biayaPerHari * 30;
        double biayaPerTahun = biayaPerHari * 365;
        double totalBiaya = biayaPerTahun * lamaMerokok;

        Intent intent =
                new Intent(
                        KalkulatorMerokokActivity.this,
                        HasilKalkulatorMerokokActivity.class
                );

        intent.putExtra("batang_hari", batangPerHari);
        intent.putExtra("batang_bulan", perBulan);
        intent.putExtra("batang_tahun", perTahun);
        intent.putExtra("total_batang", totalBatang);

        intent.putExtra("biaya_hari", biayaPerHari);
        intent.putExtra("biaya_bulan", biayaPerBulan);
        intent.putExtra("biaya_tahun", biayaPerTahun);
        intent.putExtra("total_biaya", totalBiaya);
        intent.putExtra("lama_merokok", lamaMerokok);

        // Save to Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);
        String uid = sessionManager.getCurrentUserUid();

        if (uid != null) {
            String id = String.valueOf(System.currentTimeMillis());

            HashMap<String, Object> data = new HashMap<>();
            data.put("uid", uid);
            data.put("date", savedDate);
            data.put("batang_hari", String.valueOf(batangPerHari));
            data.put("batang_bulan", String.valueOf(perBulan));
            data.put("batang_tahun", String.valueOf(perTahun));
            data.put("total_batang", String.valueOf(totalBatang));
            data.put("biaya_hari", String.valueOf(biayaPerHari));
            data.put("biaya_bulan", String.valueOf(biayaPerBulan));
            data.put("biaya_tahun", String.valueOf(biayaPerTahun));
            data.put("total_biaya", String.valueOf(totalBiaya));
            data.put("lama_merokok", String.valueOf(lamaMerokok));

            dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_KALK_MEROKOK, id, data);

            FirebaseFirestore.getInstance().collection("kalk_merokok").document(id).set(data);

            // Pass the ID to the intent just in case we need it
            intent.putExtra("id", id);
        }

        startActivity(intent);
    }
}