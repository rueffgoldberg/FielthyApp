package example.com.fielthyapps.Feature.Smoker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class TestSmokerActivity extends AppCompatActivity {

    private Button btn_submit;
    private ImageView iV_back;

    private RadioButton rb1a, rb1b, rb1c, rb1d;
    private RadioButton rb2a, rb2b, rb2c, rb2d;

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

        btn_submit = findViewById(R.id.btn_hasil);
        iV_back = findViewById(R.id.iV_kembali);

        rb1a = findViewById(R.id.rb_1a);
        rb1b = findViewById(R.id.rb_1b);
        rb1c = findViewById(R.id.rb_1c);
        rb1d = findViewById(R.id.rb_1d);

        rb2a = findViewById(R.id.rb_2a);
        rb2b = findViewById(R.id.rb_2b);
        rb2c = findViewById(R.id.rb_2c);
        rb2d = findViewById(R.id.rb_2d);

        setupSingleChoice(rb1a, rb1b, rb1c, rb1d);
        setupSingleChoice(rb2a, rb2b, rb2c, rb2d);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        mLoading = new ProgressDialog(this);
        mLoading.setMessage("Please Wait..");

        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            merokok = (String) b.get("merokok");
        }

        iV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestSmokerActivity.this, SmokerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTest();
            }
        });
    }

    private void setupSingleChoice(RadioButton... radioButtons) {
        for (RadioButton radioButton : radioButtons) {
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (RadioButton rb : radioButtons) {
                        rb.setChecked(rb == view);
                    }
                }
            });
        }
    }

    private void submitTest() {
            String jawaban1 = getJawabanPertanyaan1();
            String jawaban2 = getJawabanPertanyaan2();

            if (jawaban1.isEmpty()) {
                Toast.makeText(this, "Pilih jawaban pertanyaan 1 terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (jawaban2.isEmpty()) {
                Toast.makeText(this, "Pilih jawaban pertanyaan 2 terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser == null) {
                Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
                return;
            }

        String statusPerokok = getStatusPerokok(jawaban1, jawaban2);

        mLoading.show();

        DocumentReference documentReference = fStore.collection("smoker").document();
        String uid = currentUser.getUid();

        int poin1 = getPoinPertanyaan1();
        int poin2 = getPoinPertanyaan2();
        int totalPoin = poin1 + poin2;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("id", documentReference.getId());
        hashMap.put("merokok", merokok);
        hashMap.put("jawaban_pertanyaan_1", jawaban1);
        hashMap.put("jawaban_pertanyaan_2", jawaban2);
        hashMap.put("poin_pertanyaan_1", poin1);
        hashMap.put("poin_pertanyaan_2", poin2);
        hashMap.put("total_poin", totalPoin);
        hashMap.put("status_perokok", statusPerokok);
        hashMap.put("date", formattedDate);

        DatabaseHelper dbHelper = new DatabaseHelper(TestSmokerActivity.this);
        dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_SMOKER, documentReference.getId(), hashMap);

        // Simpan ke Firestore tanpa memblokir navigasi
        documentReference.set(hashMap);

        mLoading.dismiss();

        Toast.makeText(TestSmokerActivity.this,
                "Berhasil input data Smoker",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(TestSmokerActivity.this, HasilSmokerActivity.class);
        intent.putExtra("id", documentReference.getId());
        intent.putExtra("uid", uid);
        intent.putExtra("jawaban_pertanyaan_1", jawaban1);
        intent.putExtra("jawaban_pertanyaan_2", jawaban2);
        intent.putExtra("poin_pertanyaan_1", poin1);
        intent.putExtra("poin_pertanyaan_2", poin2);
        intent.putExtra("total_poin", totalPoin);
        intent.putExtra("status_perokok", statusPerokok);
        intent.putExtra("status", "testsmoker");

        startActivity(intent);
        finish();
    }

    private String getJawabanPertanyaan1() {
        if (rb1a.isChecked()) return "A";
        if (rb1b.isChecked()) return "B";
        if (rb1c.isChecked()) return "C";
        if (rb1d.isChecked()) return "D";
        return "";
    }

    private String getJawabanPertanyaan2() {
        if (rb2a.isChecked()) return "A";
        if (rb2b.isChecked()) return "B";
        if (rb2c.isChecked()) return "C";
        if (rb2d.isChecked()) return "D";
        return "";
    }

    private int getPoinPertanyaan1() {
        if (rb1a.isChecked()) return 3;
        if (rb1b.isChecked()) return 2;
        if (rb1c.isChecked()) return 1;
        if (rb1d.isChecked()) return 0;
        return -1;
    }

    private int getPoinPertanyaan2() {
        if (rb2a.isChecked()) return 0;
        if (rb2b.isChecked()) return 1;
        if (rb2c.isChecked()) return 2;
        if (rb2d.isChecked()) return 3;
        return -1;
    }

    private String getStatusPerokok(String jawaban1, String jawaban2) {
        String kombinasi = jawaban1 + jawaban2;

        switch (kombinasi) {
            case "AA":
            case "AB":
            case "AC":
            case "AD":
            case "BA":
            case "BB":
            case "BC":
            case "BD":
            case "CC":
            case "CD":
            case "DC":
            case "DD":
                return "perokok berat";

            case "CA":
            case "CB":
            case "DB":
                return "perokok sedang";

            case "DA":
                return "perokok ringan";

            default:
                return "";
        }
    }
}

