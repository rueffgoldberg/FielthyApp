package example.com.fielthyapps.Feature.Nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class BMRActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private EditText etUmur;
    private EditText etBerat;
    private EditText etTinggi;

    private TextView tvJenisKelamin;
    private TextView tvAktivitas;

    private Button btnHitung;
    private LinearLayout layoutAktivitas;
    private LinearLayout layoutDropdown;
    private LinearLayout itemSangatRingan;
    private LinearLayout itemRingan;
    private LinearLayout itemSedang;
    private LinearLayout itemBerat;
    private LinearLayout itemSangatBerat;
    private ImageView imgArrow;
    private ImageView ivKembali;

    private double faktorAktivitas = 0;
    private boolean aktivitasDipilih = false;
    private String savedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmr);

        savedDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());

        // INPUT
        etUmur = findViewById(R.id.eT_bmr_umur);
        etBerat = findViewById(R.id.eT_beratbadan_bmr);
        etTinggi = findViewById(R.id.eT_tinggibadanbmr);

        // TEXTVIEW
        tvJenisKelamin = findViewById(R.id.tV_JenisKelaminBmr);
        tvAktivitas = findViewById(R.id.tvAktivitas);

        // BUTTON
        btnHitung = findViewById(R.id.btn_lanjut_hasil_bmr);

        // IMAGE
        ivKembali = findViewById(R.id.iV_kembali);
        imgArrow = findViewById(R.id.imgArrow);

        // DROPDOWN
        layoutAktivitas = findViewById(R.id.layoutAktivitas);
        layoutDropdown = findViewById(R.id.layoutDropdown);
        itemSangatRingan = findViewById(R.id.itemSangatRingan);
        itemRingan = findViewById(R.id.itemRingan);
        itemSedang = findViewById(R.id.itemSedang);
        itemBerat = findViewById(R.id.itemBerat);
        itemSangatBerat = findViewById(R.id.itemSangatBerat);

        // DEFAULT
        tvAktivitas.setText("Pilih Aktivitas Anda");
        tvAktivitas.setTextColor(
                getResources().getColor(R.color.grey)
        );

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        checkUserData();
        // SEMENTARA
        // Nanti ganti dengan data profile user
        // tvJenisKelamin.setText("Laki-laki");

        // KEMBALI
        ivKembali.setOnClickListener(v -> finish());

        // BUKA/TUTUP DROPDOWN
        layoutAktivitas.setOnClickListener(v -> {

            if (layoutDropdown.getVisibility() == LinearLayout.GONE) {

                layoutDropdown.setVisibility(LinearLayout.VISIBLE);

                imgArrow.animate()
                        .rotation(180)
                        .setDuration(200);

            } else {

                closeDropdown();
            }
        });

        // SANGAT RINGAN
        itemSangatRingan.setOnClickListener(v -> {

            tvAktivitas.setText("Sangat Ringan (1.2)");

            tvAktivitas.setTextColor(
                    getResources().getColor(R.color.black)
            );

            faktorAktivitas = 1.2;
            aktivitasDipilih = true;

            closeDropdown();
        });

        // RINGAN
        itemRingan.setOnClickListener(v -> {

            tvAktivitas.setText("Ringan (1.375)");

            tvAktivitas.setTextColor(
                    getResources().getColor(R.color.black)
            );

            faktorAktivitas = 1.375;
            aktivitasDipilih = true;

            closeDropdown();
        });

        // SEDANG
        itemSedang.setOnClickListener(v -> {

            tvAktivitas.setText("Sedang (1.55)");

            tvAktivitas.setTextColor(
                    getResources().getColor(R.color.black)
            );

            faktorAktivitas = 1.55;
            aktivitasDipilih = true;

            closeDropdown();
        });

        // BERAT
        itemBerat.setOnClickListener(v -> {

            tvAktivitas.setText("Berat (1.725)");

            tvAktivitas.setTextColor(
                    getResources().getColor(R.color.black)
            );

            faktorAktivitas = 1.725;
            aktivitasDipilih = true;

            closeDropdown();
        });

        // SANGAT BERAT
        itemSangatBerat.setOnClickListener(v -> {

            tvAktivitas.setText("Sangat Berat (1.9)");

            tvAktivitas.setTextColor(
                    getResources().getColor(R.color.black)
            );

            faktorAktivitas = 1.9;
            aktivitasDipilih = true;

            closeDropdown();
        });

        // HITUNG
        btnHitung.setOnClickListener(v -> hitungBMR());
    }

    private void closeDropdown() {

        layoutDropdown.setVisibility(LinearLayout.GONE);

        imgArrow.animate()
                .rotation(0)
                .setDuration(200);
    }

    private void checkUserData() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        fStore.collection("user")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    Long umur = documentSnapshot.getLong("umur");
                    String gender = documentSnapshot.getString("gender");

                    if (umur != null) {
                        etUmur.setText(String.valueOf(umur));
                    }

                    if (gender != null) {
                        tvJenisKelamin.setText(gender);
                    }

                });
    }

    private void hitungBMR() {

        String umurStr = etUmur.getText().toString().trim();
        String beratStr = etBerat.getText().toString().trim();
        String tinggiStr = etTinggi.getText().toString().trim();

        if (umurStr.isEmpty()
                || beratStr.isEmpty()
                || tinggiStr.isEmpty()) {

            Toast.makeText(
                    this,
                    "Lengkapi semua data terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!aktivitasDipilih) {

            Toast.makeText(
                    this,
                    "Pilih tingkat aktivitas terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int umur = Integer.parseInt(umurStr);
        double berat = Double.parseDouble(beratStr);
        double tinggi = Double.parseDouble(tinggiStr);

        if (umur <= 0) {

            Toast.makeText(
                    this,
                    "Umur tidak valid",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (berat <= 0 || tinggi <= 0) {

            Toast.makeText(
                    this,
                    "Berat dan tinggi harus lebih dari 0",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String jenisKelamin =
                tvJenisKelamin.getText().toString();

        double bmr;

        String jk = jenisKelamin.trim().toLowerCase();

        if (jk.contains("laki")) {

            bmr = (10 * berat)
                    + (6.25 * tinggi)
                    - (5 * umur)
                    + 5;

        } else {

            bmr = (10 * berat)
                    + (6.25 * tinggi)
                    - (5 * umur)
                    - 161;
        }

        double tdee = bmr * faktorAktivitas;

        double turun = tdee - 500;
        double normal = tdee;
        double naik = tdee + 500;

        DatabaseHelper dbHelper =
                new DatabaseHelper(this);

        String id =
                String.valueOf(
                        System.currentTimeMillis()
                );

        String tanggal = savedDate;

        HashMap<String, Object> data =
                new HashMap<>();

        data.put("id", id);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            data.put("uid", user.getUid());
        }
        data.put("date", tanggal);

        data.put("gender", jenisKelamin);
        data.put("umur", umur);

        data.put("berat", berat);
        data.put("tinggi", tinggi);

        data.put("aktivitas",
                tvAktivitas.getText().toString());

        data.put("faktor",
                faktorAktivitas);

        data.put("bmr",
                Math.round(bmr));

        data.put("tdee",
                Math.round(tdee));

        data.put("turun",
                Math.round(turun));

        data.put("normal",
                Math.round(normal));

        data.put("naik",
                Math.round(naik));

        dbHelper.insertOrUpdateRecord(
                DatabaseHelper.TABLE_BMR,
                id,
                data
        );

        if (fStore != null) {
            fStore.collection("bmr").document(id).set(data);
        }

        Intent intent =
                new Intent(
                        BMRActivity.this,
                        HasilBMRActivity.class
                );
        intent.putExtra("id", id);

        intent.putExtra("bmr", Math.round(bmr));
        intent.putExtra("tdee", Math.round(tdee));

        intent.putExtra("turun", Math.round(turun));
        intent.putExtra("normal", Math.round(normal));
        intent.putExtra("naik", Math.round(naik));

        intent.putExtra("aktivitas", tvAktivitas.getText().toString());
        intent.putExtra("faktor", faktorAktivitas);
        intent.putExtra("gender", jenisKelamin);
        intent.putExtra("umur", umur);
        intent.putExtra("berat", berat);
        intent.putExtra("tinggi", tinggi);

        startActivity(intent);
    }
}