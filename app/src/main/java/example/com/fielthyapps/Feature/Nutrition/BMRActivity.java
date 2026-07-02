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

    // --- DEKLARASI VARIABEL FIREBASE ---
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;

    // --- DEKLARASI VARIABEL UI ---
    private EditText etUmur, etBerat, etTinggi;
    private TextView tvJenisKelamin, tvAktivitas;
    private Button btnHitung;
    private LinearLayout layoutAktivitas, layoutDropdown;
    private LinearLayout itemSangatRingan, itemRingan, itemSedang, itemBerat, itemSangatBerat;
    private ImageView imgArrow, ivKembali;

    // --- DEKLARASI VARIABEL PENDUKUNG ---
    private double faktorAktivitas = 0;
    private boolean aktivitasDipilih = false;
    private String savedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmr);

        // Menyimpan waktu saat ini untuk data rekaman
        savedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        // --- INISIALISASI KOMPONEN UI ---

        // Input Form
        etUmur = findViewById(R.id.eT_bmr_umur);
        etBerat = findViewById(R.id.eT_beratbadan_bmr);
        etTinggi = findViewById(R.id.eT_tinggibadanbmr);

        // Teks Informasi
        tvJenisKelamin = findViewById(R.id.tV_JenisKelaminBmr);
        tvAktivitas = findViewById(R.id.tvAktivitas);

        // Tombol
        btnHitung = findViewById(R.id.btn_lanjut_hasil_bmr);
        ivKembali = findViewById(R.id.iV_kembali);
        imgArrow = findViewById(R.id.imgArrow);

        // Dropdown Aktivitas
        layoutAktivitas = findViewById(R.id.layoutAktivitas);
        layoutDropdown = findViewById(R.id.layoutDropdown);
        itemSangatRingan = findViewById(R.id.itemSangatRingan);
        itemRingan = findViewById(R.id.itemRingan);
        itemSedang = findViewById(R.id.itemSedang);
        itemBerat = findViewById(R.id.itemBerat);
        itemSangatBerat = findViewById(R.id.itemSangatBerat);

        // Pengaturan Tampilan Default Awal
        tvAktivitas.setText("Pilih Aktivitas Anda");
        tvAktivitas.setTextColor(getResources().getColor(R.color.grey));

        // --- INISIALISASI FIREBASE ---
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Mengambil dan menampilkan data profil user
        checkUserData();

        // --- EVENT LISTENERS ---

        // Tombol Kembali
        ivKembali.setOnClickListener(v -> finish());

        // Logika Buka/Tutup Dropdown Aktivitas
        layoutAktivitas.setOnClickListener(v -> {
            if (layoutDropdown.getVisibility() == LinearLayout.GONE) {
                layoutDropdown.setVisibility(LinearLayout.VISIBLE);
                imgArrow.animate().rotation(180).setDuration(200);
            } else {
                closeDropdown();
            }
        });

        // Pilihan: Sangat Ringan
        itemSangatRingan.setOnClickListener(v -> {
            tvAktivitas.setText("Sangat Ringan (1.2)");
            tvAktivitas.setTextColor(getResources().getColor(R.color.black));
            faktorAktivitas = 1.2;
            aktivitasDipilih = true;
            closeDropdown();
        });

        // Pilihan: Ringan
        itemRingan.setOnClickListener(v -> {
            tvAktivitas.setText("Ringan (1.375)");
            tvAktivitas.setTextColor(getResources().getColor(R.color.black));
            faktorAktivitas = 1.375;
            aktivitasDipilih = true;
            closeDropdown();
        });

        // Pilihan: Sedang
        itemSedang.setOnClickListener(v -> {
            tvAktivitas.setText("Sedang (1.55)");
            tvAktivitas.setTextColor(getResources().getColor(R.color.black));
            faktorAktivitas = 1.55;
            aktivitasDipilih = true;
            closeDropdown();
        });

        // Pilihan: Berat
        itemBerat.setOnClickListener(v -> {
            tvAktivitas.setText("Berat (1.725)");
            tvAktivitas.setTextColor(getResources().getColor(R.color.black));
            faktorAktivitas = 1.725;
            aktivitasDipilih = true;
            closeDropdown();
        });

        // Pilihan: Sangat Berat
        itemSangatBerat.setOnClickListener(v -> {
            tvAktivitas.setText("Sangat Berat (1.9)");
            tvAktivitas.setTextColor(getResources().getColor(R.color.black));
            faktorAktivitas = 1.9;
            aktivitasDipilih = true;
            closeDropdown();
        });

        // Eksekusi Tombol Hitung
        btnHitung.setOnClickListener(v -> hitungBMR());
    }

    // --- METHOD PENDUKUNG ---

    // Menutup dropdown dengan animasi panah
    private void closeDropdown() {
        layoutDropdown.setVisibility(LinearLayout.GONE);
        imgArrow.animate().rotation(0).setDuration(200);
    }

    // Memeriksa dan mengambil data pengguna (Umur & Gender) dari Firestore
    private void checkUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Batalkan jika user belum login
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

    // Menghitung BMR dan menyimpannya ke database
    private void hitungBMR() {
        // Mengambil nilai input
        String umurStr = etUmur.getText().toString().trim();
        String beratStr = etBerat.getText().toString().trim();
        String tinggiStr = etTinggi.getText().toString().trim();

        // Validasi Form Kosong
        if (umurStr.isEmpty() || beratStr.isEmpty() || tinggiStr.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi Pilihan Aktivitas
        if (!aktivitasDipilih) {
            Toast.makeText(this, "Pilih tingkat aktivitas terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parsing nilai input ke angka
        int umur = Integer.parseInt(umurStr);
        double berat = Double.parseDouble(beratStr);
        double tinggi = Double.parseDouble(tinggiStr);

        // Validasi Angka Tidak Masuk Akal
        if (umur <= 0) {
            Toast.makeText(this, "Umur tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (berat <= 0 || tinggi <= 0) {
            Toast.makeText(this, "Berat dan tinggi harus lebih dari 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Persiapan Perhitungan BMR
        String jenisKelamin = tvJenisKelamin.getText().toString();
        String jk = jenisKelamin.trim().toLowerCase();
        double bmr;

        // Rumus BMR (Mifflin-St Jeor)
        if (jk.contains("laki")) {
            bmr = (10 * berat) + (6.25 * tinggi) - (5 * umur) + 5;
        } else {
            bmr = (10 * berat) + (6.25 * tinggi) - (5 * umur) - 161;
        }

        // Perhitungan TDEE dan Rekomendasi Kalori
        double tdee = bmr * faktorAktivitas;
        double turun = tdee - 500;
        double normal = tdee;
        double naik = tdee + 500;

        // --- PROSES SIMPAN KE DATABASE ---

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String id = String.valueOf(System.currentTimeMillis());
        String tanggal = savedDate;

        // Membungkus data ke dalam HashMap
        HashMap<String, Object> data = new HashMap<>();
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
        data.put("aktivitas", tvAktivitas.getText().toString());
        data.put("faktor", faktorAktivitas);
        data.put("bmr", Math.round(bmr));
        data.put("tdee", Math.round(tdee));
        data.put("turun", Math.round(turun));
        data.put("normal", Math.round(normal));
        data.put("naik", Math.round(naik));

        // Menyimpan data ke SQLite (Lokal)
        dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_BMR, id, data);

        // Menyimpan data ke Firebase (Cloud)
        if (fStore != null) {
            fStore.collection("bmr").document(id).set(data);
        }

        // --- BERPINDAH KE HALAMAN HASIL BMR ---

        Intent intent = new Intent(BMRActivity.this, HasilBMRActivity.class);

        // Membawa data ke halaman selanjutnya
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