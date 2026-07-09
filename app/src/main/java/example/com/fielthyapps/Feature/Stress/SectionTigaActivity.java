package example.com.fielthyapps.Feature.Stress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class SectionTigaActivity extends AppCompatActivity {
    private String formattedDate;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_tiga);
        RecyclerView rV_ketiga = findViewById(R.id.rV_quest_ketiga);
        Button btn_lanjut = findViewById(R.id.btn_lanjut_hasil_sec_three);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        java.time.LocalDateTime currentDateTime = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        formattedDate = currentDateTime.format(formatter);

        List<QuestList> questions = new ArrayList<>();
        questions.add(new QuestList("Saya merasa tidak dapat merasakan perasaan positif sama sekali", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa sulit untuk memulai suatu kegiatan", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa tidak ada hal baik yang dapat diharapkan di masa depan", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa sedih dan murung", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa tidak antusias terhadap apa pun", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa diri saya tidak berharga", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa bahwa hidup ini tidak berarti", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));

        QuestSectionsatuAdapter adapter = new QuestSectionsatuAdapter(questions);
        rV_ketiga.setHasFixedSize(true);
        rV_ketiga.setLayoutManager(new LinearLayoutManager(this));
        rV_ketiga.setAdapter(adapter);

        Intent iin = getIntent();
        final Bundle b = iin.getExtras();

        if (b != null) {
            id = (String) b.get("id");
        }
        btn_lanjut.setOnClickListener(view -> {
            if (firebaseUser == null) {
                Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).getSelectedOption() == -1) {
                    Toast.makeText(this, "Harap jawab semua pertanyaan terlebih dahulu", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String uid = firebaseUser.getUid();
            final String id_doc = (id != null) ? id : fStore.collection("depresi").document().getId();
            
            DocumentReference documentReference = fStore.collection("depresi").document(id_doc);
            HashMap<String, Object> answers = new HashMap<>();
            answers.put("uid", uid);
            answers.put("id", id_doc);
            answers.put("date", formattedDate);
            for (int i = 0; i < questions.size(); i++) {
                QuestList question = questions.get(i);
                int selectedOption = question.getSelectedOption();
                String selectedOptionText = question.getOptions().get(selectedOption);
                answers.put("quest" + (i + 1), selectedOptionText);
            }

            // Simpan jawaban quest ke Firestore tanpa memblokir navigasi
            documentReference.set(answers);

            DocumentReference updatestress = fStore.collection("stresstest").document(id_doc);
            HashMap<String, Object> statusData = new HashMap<>();
            statusData.put("depresi", "1");

            // Simpan hanya status flag ke SQLite lokal
            DatabaseHelper dbHelper = new DatabaseHelper(SectionTigaActivity.this);
            dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_STRESS, id_doc, statusData);

            // Gunakan set+merge agar tidak crash jika dokumen stresstest belum ada
            updatestress.set(statusData, SetOptions.merge());

            Toast.makeText(SectionTigaActivity.this, "Berhasil input data Depresi", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SectionTigaActivity.this, HasilStressActivity.class);
            intent.putExtra("status", "depresi");
            intent.putExtra("id", id_doc);
            intent.putExtra("type", "test");
            startActivity(intent);
            finish();
        });
    }
}