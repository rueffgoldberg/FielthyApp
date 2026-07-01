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

public class SectionSatuActivity extends AppCompatActivity {
    private String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_satu);
        RecyclerView rV_pertama = findViewById(R.id.rV_quest_pertama);
        Button btn_lanjut = findViewById(R.id.btn_lanjut_hasil);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        List<QuestList> questions = new ArrayList<>();

        java.time.LocalDateTime currentDateTime = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        formattedDate = currentDateTime.format(formatter);

        questions.add(new QuestList("Saya merasa sulit ditenangkan", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya cenderung bereaksi berlebihan terhadap suatu situasi", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa telah menghabiskan banyak energi untuk cemas", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa gelisah", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa sulit untuk rileks/santai", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya tidak sabar terhadap hal-hal yang menghambat apa yang sedang saya lakukan", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));
        questions.add(new QuestList("Saya merasa mudah tersinggung", Arrays.asList("Tidak sesuai/tidak pernah", "Perilaku muncul sesekali/jarang", "Sesuai/cukup sering muncul", "Sangat sesuai/sangat sering muncul")));

        QuestSectionsatuAdapter adapter = new QuestSectionsatuAdapter(questions);
        rV_pertama.setHasFixedSize(true);
        rV_pertama.setLayoutManager(new LinearLayoutManager(this));
        rV_pertama.setAdapter(adapter);

        btn_lanjut.setOnClickListener(view -> {
            if (firebaseUser == null) return;
            String uid = firebaseUser.getUid();
            String intentId = getIntent().getStringExtra("id");
            final String id_doc = (intentId != null) ? intentId : fStore.collection("stress").document().getId();
            
            DocumentReference documentReference = fStore.collection("stress").document(id_doc);
            HashMap<String, Object> answers = new HashMap<>();
            answers.put("uid", uid);
            answers.put("id", id_doc);
            answers.put("date", formattedDate);
            for (int i = 0; i < questions.size(); i++) {
                QuestList question = questions.get(i);
                int selectedOption = question.getSelectedOption();
                if (selectedOption != -1) {
                    String selectedOptionText = question.getOptions().get(selectedOption);
                    answers.put("quest" + (i + 1), selectedOptionText);
                } else {
                    answers.put("quest" + (i + 1), "Tidak ada jawaban dipilih");
                }
            }

            // Simpan jawaban quest ke Firestore (bukan SQLite, karena kolom quest tidak ada di tabel lokal)
            documentReference.set(answers)
                    .addOnSuccessListener(unused -> {
                        DocumentReference updatestress = fStore.collection("stresstest").document(id_doc);
                        HashMap<String, Object> statusData = new HashMap<>();
                        statusData.put("stress", "1");

                        // Simpan hanya status flag ke SQLite lokal
                        DatabaseHelper dbHelper = new DatabaseHelper(SectionSatuActivity.this);
                        dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_STRESS, id_doc, statusData);

                        // Gunakan set+merge agar tidak crash jika dokumen stresstest belum ada
                        updatestress.set(statusData, SetOptions.merge())
                                .addOnSuccessListener(unused1 -> {
                                    Toast.makeText(SectionSatuActivity.this, "Berhasil input data Stress", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SectionSatuActivity.this, HasilStressActivity.class);
                                    intent.putExtra("status", "stress");
                                    intent.putExtra("id", id_doc);
                                    intent.putExtra("type", "test");
                                    startActivity(intent);
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Gagal menyimpan data stress", e));
        });
    }
}