package example.com.fielthyapps.Feature.Nutrition;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.Service.ElevenLabs;
import example.com.fielthyapps.databinding.ActivityFoodResultBinding;

public class FoodResultActivity extends AppCompatActivity {
    private ActivityFoodResultBinding binding;
    private ElevenLabs tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String value = getIntent().getStringExtra("name");
        boolean isHistory = getIntent().getBooleanExtra("is_history", false);
        tts = new ElevenLabs(this);

        if (isHistory) {
            displayHistoryData();
        } else {
            loadData(value);
        }

        binding.btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stopMp3();
        }
    }

    private void displayHistoryData() {
        String name = getIntent().getStringExtra("name");
        String porsi = getIntent().getStringExtra("porsi");
        String kalori = getIntent().getStringExtra("kalori");
        String protein = getIntent().getStringExtra("protein");
        String karbohidrat = getIntent().getStringExtra("karbohidrat");
        String lemak = getIntent().getStringExtra("lemak");

        binding.tvName.setText(name);
        binding.tvWeight.setText(porsi);
        binding.tvMetricServing.setText(porsi);

        binding.tvCalories.setText(kalori);
        if (kalori != null && !kalori.contains("Kcal")) binding.tvCalories.setText(kalori + " Kcal");

        binding.tvProtein.setText(protein);
        if (protein != null && !protein.contains("gr")) binding.tvProtein.setText(protein + " gr");

        binding.tvCarbs.setText(karbohidrat);
        if (karbohidrat != null && !karbohidrat.contains("gr")) binding.tvCarbs.setText(karbohidrat + " gr");

        binding.tvFat.setText(lemak);
        if (lemak != null && !lemak.contains("gr")) binding.tvFat.setText(lemak + " gr");

        String serat = getIntent().getStringExtra("serat");
        String kalsium = getIntent().getStringExtra("kalsium");
        String besi = getIntent().getStringExtra("besi");
        String natrium = getIntent().getStringExtra("natrium");
        String kalium = getIntent().getStringExtra("kalium");
        String vitamin_a = getIntent().getStringExtra("vitamin_a");
        String vitamin_c = getIntent().getStringExtra("vitamin_c");
        String lemak_jenuh = getIntent().getStringExtra("lemak_jenuh");
        String lemak_ganda = getIntent().getStringExtra("lemak_ganda");
        String lemak_tunggal = getIntent().getStringExtra("lemak_tunggal");
        String kolesterol = getIntent().getStringExtra("kolesterol");
        String gula = getIntent().getStringExtra("gula");

        binding.tvFiber.setText(serat != null ? serat : "0");
        if (serat != null && !serat.contains("gr")) binding.tvFiber.setText(serat + " gr");

        binding.tvCalcium.setText(kalsium != null ? kalsium : "0");
        if (kalsium != null && !kalsium.contains("mg")) binding.tvCalcium.setText(kalsium + " mg");

        binding.tvIron.setText(besi != null ? besi : "0");
        if (besi != null && !besi.contains("mg")) binding.tvIron.setText(besi + " mg");

        binding.tvSodium.setText(natrium != null ? natrium : "0");
        if (natrium != null && !natrium.contains("mg")) binding.tvSodium.setText(natrium + " mg");

        binding.tvPotassium.setText(kalium != null ? kalium : "0");
        if (kalium != null && !kalium.contains("mg")) binding.tvPotassium.setText(kalium + " mg");

        binding.tvVitaminA.setText(vitamin_a != null ? vitamin_a : "0");
        if (vitamin_a != null && !vitamin_a.contains("mcg") && !vitamin_a.contains("mg")) binding.tvVitaminA.setText(vitamin_a + " mcg");

        binding.tvVitaminC.setText(vitamin_c != null ? vitamin_c : "0");
        if (vitamin_c != null && !vitamin_c.contains("mg")) binding.tvVitaminC.setText(vitamin_c + " mg");

        binding.tvSaturatedFat.setText(lemak_jenuh != null ? lemak_jenuh : "0");
        if (lemak_jenuh != null && !lemak_jenuh.contains("gr")) binding.tvSaturatedFat.setText(lemak_jenuh + " gr");

        binding.tvPolyunsaturatedFat.setText(lemak_ganda != null ? lemak_ganda : "0");
        if (lemak_ganda != null && !lemak_ganda.contains("gr")) binding.tvPolyunsaturatedFat.setText(lemak_ganda + " gr");

        binding.tvMonounsaturatedFat.setText(lemak_tunggal != null ? lemak_tunggal : "0");
        if (lemak_tunggal != null && !lemak_tunggal.contains("gr")) binding.tvMonounsaturatedFat.setText(lemak_tunggal + " gr");

        binding.tvCholesterol.setText(kolesterol != null ? kolesterol : "0");
        if (kolesterol != null && !kolesterol.contains("mg")) binding.tvCholesterol.setText(kolesterol + " mg");

        binding.tvSugar.setText(gula != null ? gula : "0");
        if (gula != null && !gula.contains("gr")) binding.tvSugar.setText(gula + " gr");

        String speechText = name + " untuk porsi " + porsi + " mengandung " + kalori + " kalori, "
                + protein + " gram protein, " + karbohidrat + " gram karbohidrat, dan "
                + lemak + " gram lemak.";

        binding.btnTTS.setOnClickListener(v -> {
            if (tts != null) {
                try {
                    tts.textToSpeech(speechText);
                } catch (Exception e) {
                    Toast.makeText(FoodResultActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void loadData(String value) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Memastikan file JSON dimasukkan ke SQLite jika database masih kosong
        if (dbHelper.isFoodDataEmpty()) {
            dbHelper.loadNutritionFromAssets(this);
        }

        // Mencari makanan dari SQLite (hasil JSON)
        HashMap<String, String> foodData = dbHelper.getFoodByName(value);

        if (foodData != null && !foodData.isEmpty()) {
            displayFoodData(foodData);
        } else {
            // Jika makanan benar-benar tidak ada di JSON
            Toast.makeText(this, "Data nutrisi untuk " + value + " belum tersedia.", Toast.LENGTH_LONG).show();

            // Set UI Kosong agar tidak terjadi crash
            binding.tvName.setText(value != null ? value : "Tidak Diketahui");
            binding.tvWeight.setText("0 g");
            binding.tvMetricServing.setText("0 g");
            binding.tvCalories.setText("0 Kcal");
            binding.tvProtein.setText("0 gr");
            binding.tvCarbs.setText("0 gr");
            binding.tvFat.setText("0 gr");
        }
    }

    private void displayFoodData(HashMap<String, String> foodData) {
        String namaMakanan = foodData.get(DatabaseHelper.COL_FOOD_NAMA);
        String kalori = foodData.get(DatabaseHelper.COL_FOOD_KALORI);
        String protein = foodData.get(DatabaseHelper.COL_FOOD_PROTEIN);
        String karbohidrat = foodData.get(DatabaseHelper.COL_FOOD_KARB);
        String lemak = foodData.get(DatabaseHelper.COL_FOOD_LEMAK);

        binding.tvName.setText(namaMakanan);
        binding.tvWeight.setText("100 g");
        binding.tvMetricServing.setText("100 g");

        binding.tvCalories.setText(kalori + " Kcal");
        binding.tvProtein.setText(protein + " gr");
        binding.tvCarbs.setText(karbohidrat + " gr");
        binding.tvFat.setText(lemak + " gr");

        // Setel 0 untuk detail lainnya karena dataset JSON yang baru hanya memiliki makro nutrisi
        binding.tvFiber.setText("0 gr");
        binding.tvCalcium.setText("0 mg");
        binding.tvIron.setText("0 mg");
        binding.tvSodium.setText("0 mg");
        binding.tvPotassium.setText("0 mg");
        binding.tvVitaminA.setText("0 mcg");
        binding.tvVitaminC.setText("0 mg");
        binding.tvSaturatedFat.setText("0 gr");
        binding.tvPolyunsaturatedFat.setText("0 gr");
        binding.tvMonounsaturatedFat.setText("0 gr");
        binding.tvCholesterol.setText("0 mg");
        binding.tvSugar.setText("0 gr");

        String speechText = namaMakanan + " mengandung " + kalori + " kalori, "
                + protein + " gram protein, "
                + karbohidrat + " gram karbohidrat, dan "
                + lemak + " gram lemak.";

        binding.btnTTS.setOnClickListener(v -> {
            if (tts != null) {
                try {
                    tts.textToSpeech(speechText);
                } catch (Exception e) {
                    Toast.makeText(FoodResultActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Simpan ke History dan Firestore jika bukan dari intent history
        if (!getIntent().getBooleanExtra("is_history", false)) {
            saveToHistory(
                    namaMakanan,
                    "100 g",
                    kalori,
                    protein,
                    karbohidrat,
                    lemak,
                    "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"
            );
        }
    }

    private void saveToHistory(String name, String serving, String calories, String protein, String carbs, String fat,
                               String serat, String kalsium, String besi, String natrium, String kalium,
                               String vitamin_a, String vitamin_c, String lemak_jenuh, String lemak_ganda,
                               String lemak_tunggal, String kolesterol, String gula) {
        try {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String id = UUID.randomUUID().toString();
            String date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id", "ID")).format(new Date());

            HashMap<String, Object> data = new HashMap<>();
            data.put("id", id);
            data.put("uid", uid);
            data.put("date", date);
            data.put("nama_makanan", name != null ? name : "");
            data.put("porsi", serving != null ? serving : "");
            data.put("kalori", calories != null ? calories : "");
            data.put("protein", protein != null ? protein : "");
            data.put("karbohidrat", carbs != null ? carbs : "");
            data.put("lemak", fat != null ? fat : "");
            data.put("serat", serat != null ? serat : "");
            data.put("kalsium", kalsium != null ? kalsium : "");
            data.put("besi", besi != null ? besi : "");
            data.put("natrium", natrium != null ? natrium : "");
            data.put("kalium", kalium != null ? kalium : "");
            data.put("vitamin_a", vitamin_a != null ? vitamin_a : "");
            data.put("vitamin_c", vitamin_c != null ? vitamin_c : "");
            data.put("lemak_jenuh", lemak_jenuh != null ? lemak_jenuh : "");
            data.put("lemak_ganda", lemak_ganda != null ? lemak_ganda : "");
            data.put("lemak_tunggal", lemak_tunggal != null ? lemak_tunggal : "");
            data.put("kolesterol", kolesterol != null ? kolesterol : "");
            data.put("gula", gula != null ? gula : "");

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_FOOD_RECOG, id, data);

            FirebaseFirestore.getInstance().collection("foodrecognition").document(id).set(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}