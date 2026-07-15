package example.com.fielthyapps.Feature.Smoker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class TestSmokerActivity extends AppCompatActivity {

    private Button btnSubmit;
    private ImageView iVBack;
    private RecyclerView rvPertanyaanSmoker;
    private SmokerQuestionAdapter adapter;
    private List<SmokerQuestion> questions;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private ProgressDialog mLoading;

    private String merokok;
    private String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_smoker);

        formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        btnSubmit = findViewById(R.id.btn_hasil);
        iVBack = findViewById(R.id.iV_kembali);
        rvPertanyaanSmoker = findViewById(R.id.rv_pertanyaan_smoker);

        questions = getFtndQuestions();
        adapter = new SmokerQuestionAdapter(questions);
        rvPertanyaanSmoker.setLayoutManager(new LinearLayoutManager(this));
        rvPertanyaanSmoker.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        mLoading = new ProgressDialog(this);
        mLoading.setMessage("Please Wait..");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            merokok = (String) bundle.get("merokok");
        }

        iVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestSmokerActivity.this, SmokerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTest();
            }
        });
    }

    private List<SmokerQuestion> getFtndQuestions() {
        List<SmokerQuestion> ftndQuestions = new ArrayList<>();

        ftndQuestions.add(new SmokerQuestion(
                "Berapa lama setelah bangun tidur Anda merokok pertama kali?",
                Arrays.asList(
                        new SmokerQuestion.Option("<= 5 menit", 3),
                        new SmokerQuestion.Option("6-30 menit", 2),
                        new SmokerQuestion.Option("31-60 menit", 1),
                        new SmokerQuestion.Option("> 60 menit", 0)
                )));

        ftndQuestions.add(new SmokerQuestion(
                "Apakah Anda merasa sulit menahan diri untuk tidak merokok di tempat yang dilarang?",
                Arrays.asList(
                        new SmokerQuestion.Option("Ya", 1),
                        new SmokerQuestion.Option("Tidak", 0)
                )));

        ftndQuestions.add(new SmokerQuestion(
                "Rokok mana yang paling sulit untuk tidak dihisap?",
                Arrays.asList(
                        new SmokerQuestion.Option("Rokok pertama setelah bangun tidur", 1),
                        new SmokerQuestion.Option("Rokok lainnya", 0)
                )));

        ftndQuestions.add(new SmokerQuestion(
                "Berapa batang rokok yang Anda hisap setiap hari?",
                Arrays.asList(
                        new SmokerQuestion.Option("<= 10 batang", 0),
                        new SmokerQuestion.Option("11-20 batang", 1),
                        new SmokerQuestion.Option("21-30 batang", 2),
                        new SmokerQuestion.Option(">= 31 batang", 3)
                )));

        ftndQuestions.add(new SmokerQuestion(
                "Apakah Anda lebih sering merokok pada jam pertama setelah bangun tidur dibandingkan waktu lainnya?",
                Arrays.asList(
                        new SmokerQuestion.Option("Ya", 1),
                        new SmokerQuestion.Option("Tidak", 0)
                )));

        ftndQuestions.add(new SmokerQuestion(
                "Apakah Anda tetap merokok ketika sedang sakit hingga harus berbaring di tempat tidur hampir sepanjang hari?",
                Arrays.asList(
                        new SmokerQuestion.Option("Ya", 1),
                        new SmokerQuestion.Option("Tidak", 0)
                )));

        return ftndQuestions;
    }

    private void submitTest() {
        int unansweredQuestion = getUnansweredQuestionNumber();
        if (unansweredQuestion != -1) {
            Toast.makeText(this, "Pilih jawaban pertanyaan " + unansweredQuestion + " terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        mLoading.show();

        DocumentReference documentReference = fStore.collection("smoker").document();
        String uid = currentUser.getUid();
        int totalScore = getTotalScore();
        String statusPerokok = getStatusPerokok(totalScore);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("id", documentReference.getId());
        hashMap.put("merokok", merokok);
        hashMap.put("total_poin", totalScore);
        hashMap.put("status_perokok", statusPerokok);
        hashMap.put("date", formattedDate);

        for (int i = 0; i < questions.size(); i++) {
            SmokerQuestion question = questions.get(i);
            SmokerQuestion.Option selectedOption = question.getSelectedOption();
            int questionNumber = i + 1;

            hashMap.put("pertanyaan_" + questionNumber, question.getQuestion());
            hashMap.put("jawaban_pertanyaan_" + questionNumber, selectedOption.getText());
            hashMap.put("poin_pertanyaan_" + questionNumber, selectedOption.getScore());
        }

        DatabaseHelper dbHelper = new DatabaseHelper(TestSmokerActivity.this);
        dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_SMOKER, documentReference.getId(), hashMap);

        documentReference.set(hashMap);

        mLoading.dismiss();

        Toast.makeText(TestSmokerActivity.this, "Berhasil input data Smoker", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent(TestSmokerActivity.this, HasilSmokerActivity.class);
        resultIntent.putExtra("id", documentReference.getId());
        resultIntent.putExtra("uid", uid);
        resultIntent.putExtra("total_poin", totalScore);
        resultIntent.putExtra("status_perokok", statusPerokok);
        resultIntent.putExtra("status", "testsmoker");

        for (int i = 0; i < questions.size(); i++) {
            SmokerQuestion.Option selectedOption = questions.get(i).getSelectedOption();
            int questionNumber = i + 1;
            resultIntent.putExtra("jawaban_pertanyaan_" + questionNumber, selectedOption.getText());
            resultIntent.putExtra("poin_pertanyaan_" + questionNumber, selectedOption.getScore());
        }

        startActivity(resultIntent);
        finish();
    }

    private int getUnansweredQuestionNumber() {
        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).isAnswered()) {
                return i + 1;
            }
        }
        return -1;
    }

    private int getTotalScore() {
        int totalScore = 0;
        for (SmokerQuestion question : questions) {
            SmokerQuestion.Option selectedOption = question.getSelectedOption();
            if (selectedOption != null) {
                totalScore += selectedOption.getScore();
            }
        }
        return totalScore;
    }

    private String getStatusPerokok(int totalScore) {
        if (totalScore <= 3) {
            return "perokok ringan";
        } else if (totalScore <= 6) {
            return "perokok sedang";
        } else {
            return "perokok berat";
        }
    }
}
