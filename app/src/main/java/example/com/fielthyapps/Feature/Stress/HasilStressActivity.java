package example.com.fielthyapps.Feature.Stress;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Map;

import example.com.fielthyapps.Feature.Smoker.InformasiMenjauhiRokokAdapter;
import example.com.fielthyapps.Feature.Smoker.SmokerTipsList;
import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class HasilStressActivity extends AppCompatActivity {
    private TextView tV_hasil, tV_angka, tV_desc, tV_hasil_anda;
    private RecyclerView rV_tips;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore fStore;
    private String id, status,type;
    private ImageView iV_status;
    private Button btnSelesai;
    private TextToSpeech textToSpeech;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_hasil_stress);
            textToSpeech = new TextToSpeech(this, status -> {

                if (status == TextToSpeech.SUCCESS) {

                    textToSpeech.setLanguage(new Locale("id", "ID"));
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.setPitch(1.0f);
                }
            });
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            fStore = FirebaseFirestore.getInstance();
            tV_hasil = findViewById(R.id.tV_status_stress);
            tV_angka = findViewById(R.id.tV_angka_stress);
            tV_desc = findViewById(R.id.tV_desc);
            rV_tips = findViewById(R.id.rV_tips);
            btnSelesai = findViewById(R.id.btn_selesai);
            iV_status = findViewById(R.id.iV_status);
            tV_hasil_anda = findViewById(R.id.tV_hasil_anda);
            Intent iin = getIntent();
            final Bundle b = iin.getExtras();

            if (b != null) {
                id = (String) b.get("id");
                status = (String) b.get("status");
                type = (String) b.get("type");
            }

            get_data();

            btnSelesai.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                HasilStressActivity.this,
                                HomeActivity.class
                        );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                startActivity(intent);
                finish();
            });
        } catch (Throwable e) {
            Log.e("HasilStressActivity", "Crash di onCreate", e);
            Toast.makeText(this, "Crash di onCreate: " + e.getClass().getSimpleName() + " - " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void get_data() {
        if (id == null || id.trim().isEmpty()) {
            Toast.makeText(this, "ID pemeriksaan kosong/tidak valid!", Toast.LENGTH_LONG).show();
            tV_hasil.setText("ID Kosong");
            tV_angka.setText("Score -");
            return;
        }
        if (status == null) {
            Toast.makeText(this, "Status pemeriksaan kosong/tidak valid!", Toast.LENGTH_LONG).show();
            tV_hasil.setText("Status Kosong");
            tV_angka.setText("Score -");
            return;
        }

        if (status.equals("stress")) {
            tV_hasil_anda.setText("Tingkat Stres Anda");
            DocumentReference checkdata = fStore.collection("stress").document(id);

            checkdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String textHasil = "";
                            if (data != null) {
                                int totalHasil = calculateTotalHasil(data);
                                if (totalHasil == -1) {
                                    tV_hasil.setText("Data Tidak Lengkap");
                                    tV_hasil.setTextColor(Color.parseColor("#C6110A"));
                                    tV_angka.setText("Score -");
                                    tV_desc.setText("Kuesioner belum diselesaikan secara lengkap.");
                                    return;
                                }
                                Log.d("Score", "Score " + totalHasil);
                                SmokerTipsList[] myListData;
                                if (totalHasil < 12) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Normal");
                                    tV_hasil.setTextColor(Color.parseColor("#C0D4B4"));
                                    iV_status.setImageResource(R.drawable.ic_normal);
                                    textHasil += "Score " + totalHasil + "\n" + "Normal";
                                    tV_desc.setText(
                                            "Tingkat stres Anda berada dalam kategori normal. "
                                                    + "Anda mampu mengelola tekanan sehari-hari dengan baik. "
                                                    + "Tetap pertahankan pola hidup sehat, istirahat yang cukup, "
                                                    + "dan lakukan aktivitas yang Anda sukai.");
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Pertahankan pola hidup sehat"),
                                            new SmokerTipsList("Tidur yang cukup setiap hari"),
                                            new SmokerTipsList("Lakukan aktivitas yang Anda sukai")
                                    };
                                } else if (totalHasil < 14) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Ringan");
                                    tV_hasil.setTextColor(Color.parseColor("#52964D"));
                                    iV_status.setImageResource(R.drawable.ic_ringan);
                                    textHasil += "Score " + totalHasil + "\n" + "Ringan";
                                    tV_desc.setText(
                                            "Tingkat stres Anda berada dalam kategori ringan. "
                                                    + "Beberapa tekanan mungkin mulai dirasakan, namun masih dapat "
                                                    + "dikendalikan dengan baik. Luangkan waktu untuk beristirahat "
                                                    + "dan lakukan aktivitas yang membantu Anda merasa lebih rileks.");
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Luangkan waktu untuk relaksasi"),
                                            new SmokerTipsList("Kurangi aktivitas yang terlalu membebani"),
                                            new SmokerTipsList("Atur jadwal istirahat dengan baik")
                                    };
                                } else if (totalHasil < 17) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Sedang");
                                    tV_hasil.setTextColor(Color.parseColor("#E9B010"));
                                    iV_status.setImageResource(R.drawable.ic_sedang);
                                    textHasil += "Score " + totalHasil + "\n" + "Sedang";
                                    tV_desc.setText(
                                            "Tingkat stres Anda berada dalam kategori sedang. "
                                                    + "Stres yang Anda alami mulai memengaruhi aktivitas sehari-hari. "
                                                    + "Cobalah mengatur waktu dengan lebih baik, berbagi cerita dengan "
                                                    + "orang terpercaya, dan lakukan teknik relaksasi secara rutin.");
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Berbagi cerita dengan orang terpercaya"),
                                            new SmokerTipsList("Lakukan teknik relaksasi"),
                                            new SmokerTipsList("Kelola waktu dan beban pekerjaan")
                                    };
                                } else if (totalHasil < 19) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Berat");
                                    tV_hasil.setTextColor(Color.parseColor("#BB5C0B"));
                                    iV_status.setImageResource(R.drawable.ic_berat);
                                    textHasil += "Score " + totalHasil + "\n" + "Berat";
                                    tV_desc.setText(
                                            "Tingkat stres Anda berada dalam kategori berat. "
                                                    + "Tekanan yang dirasakan cukup besar dan dapat memengaruhi "
                                                    + "kesehatan fisik maupun emosional. Segera lakukan langkah "
                                                    + "pengelolaan stres yang tepat dan jangan ragu mencari dukungan.");
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Cari dukungan keluarga atau teman"),
                                            new SmokerTipsList("Kurangi sumber stres yang dapat dihindari"),
                                            new SmokerTipsList("Lakukan olahraga ringan secara rutin")
                                    };
                                } else {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Sangat Berat");
                                    tV_hasil.setTextColor(Color.parseColor("#C6110A"));
                                    iV_status.setImageResource(R.drawable.ic_sangat_berat);
                                    textHasil += "Score " + totalHasil + "\n" + "Sangat Berat";
                                    tV_desc.setText(
                                            "Tingkat stres Anda berada dalam kategori sangat berat. "
                                                    + "Kondisi ini memerlukan perhatian lebih karena dapat berdampak "
                                                    + "pada kesehatan dan kualitas hidup. Disarankan untuk mencari "
                                                    + "dukungan dari keluarga, teman, atau tenaga profesional.");
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Segera cari bantuan profesional"),
                                            new SmokerTipsList("Jangan menghadapi masalah sendirian"),
                                            new SmokerTipsList("Minta dukungan keluarga dan teman terdekat")
                                    };
                                }
                                textHasil += "Berikut adalah tips yang dapat Anda lakukan untuk mengurangi stress:\n";
                                textHasil += "Bicarakan keluhan dengan seseorang yang dapat dipercaya";
                                textHasil += "Melakukan kegiatan yang sesuai dengan minat dan kemampuan";
                                textHasil += "Kembangkan hobi yang bermanfaat";
                                textHasil += "Meningkatkan ibadah dan mendekatkan diri pada tuhan";
                                textHasil += "Berpikir positif";
                                textHasil += "Tenangkan pikiran dengan relaksasi";
                                textHasil += "Jagalah Kesehatan dengan olahraga aktivitas fisik secara teratur, tidak cukup,\n" +
                                        "makan bergizi seimbang, serta terapkan perilaku bersih dan sehat";
                                ImageView imgBtnHasilPemeriksaan = findViewById(R.id.imgBtnHasilPemeriksaan);
                                String finalTextHasil = textHasil;
                                imgBtnHasilPemeriksaan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (textToSpeech != null) {

                                            try {

                                                Toast.makeText(
                                                        HasilStressActivity.this,
                                                        "Sedang memuat suara...",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                textToSpeech.speak(
                                                        finalTextHasil,
                                                        TextToSpeech.QUEUE_FLUSH,
                                                        null,
                                                        "HASIL_STRESS"
                                                );

                                            } catch (Exception e) {

                                                Toast.makeText(
                                                        HasilStressActivity.this,
                                                        e.getLocalizedMessage(),
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                    }
                                });

                                InformasiMenjauhiRokokAdapter adapter = new InformasiMenjauhiRokokAdapter(myListData);
                                rV_tips.setHasFixedSize(true);
                                rV_tips.setLayoutManager(new LinearLayoutManager(HasilStressActivity.this));
                                rV_tips.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(HasilStressActivity.this, "Dokumen hasil stress tidak ditemukan di Firestore! (ID: " + id + ")", Toast.LENGTH_LONG).show();
                            tV_hasil.setText("Data Kosong");
                            tV_angka.setText("Score -");
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(HasilStressActivity.this, "Gagal mengambil data stress: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                tV_hasil.setText("Error Load");
                tV_angka.setText("Score -");
            });
        } else if (status.equals("cemas")) {
            tV_hasil_anda.setText("Tingkat Kecemasan Anda");
            DocumentReference checkdata = fStore.collection("cemas").document(id);

            checkdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            if (data != null) {
                                int totalHasil = calculateTotalHasil(data);
                                if (totalHasil == -1) {
                                    tV_hasil.setText("Data Tidak Lengkap");
                                    tV_hasil.setTextColor(Color.parseColor("#C6110A"));
                                    tV_angka.setText("Score -");
                                    tV_desc.setText("Kuesioner belum diselesaikan secara lengkap.");
                                    return;
                                }
                                Log.d("Score", "Score " + totalHasil);
                                SmokerTipsList[] myListData;
                                if (totalHasil < 6) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Normal");
                                    tV_hasil.setTextColor(Color.parseColor("#C0D4B4"));
                                    iV_status.setImageResource(R.drawable.ic_normal);
                                    tV_desc.setText(
                                            "Tingkat kecemasan Anda berada dalam kategori normal. "
                                                    + "Anda mampu menghadapi berbagai situasi dengan baik tanpa "
                                                    + "gangguan kecemasan yang berarti."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Pertahankan pola hidup sehat"),
                                            new SmokerTipsList("Tidur yang cukup setiap hari"),
                                            new SmokerTipsList("Lakukan aktivitas yang Anda sukai")
                                    };
                                } else if (totalHasil < 8) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Ringan");
                                    tV_hasil.setTextColor(Color.parseColor("#52964D"));
                                    iV_status.setImageResource(R.drawable.ic_ringan);
                                    tV_desc.setText(
                                            "Tingkat kecemasan Anda berada dalam kategori ringan. "
                                                    + "Rasa khawatir terkadang muncul namun masih dalam batas wajar "
                                                    + "dan dapat dikendalikan."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Luangkan waktu untuk relaksasi"),
                                            new SmokerTipsList("Kurangi aktivitas yang terlalu membebani"),
                                            new SmokerTipsList("Atur jadwal istirahat dengan baik")
                                    };
                                } else if (totalHasil < 13) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Sedang");
                                    tV_hasil.setTextColor(Color.parseColor("#E9B010"));
                                    iV_status.setImageResource(R.drawable.ic_sedang);
                                    tV_desc.setText(
                                            "Tingkat kecemasan Anda berada dalam kategori sedang. "
                                                    + "Kecemasan mulai memengaruhi konsentrasi dan kenyamanan Anda "
                                                    + "dalam menjalani aktivitas sehari-hari."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Berbagi cerita dengan orang terpercaya"),
                                            new SmokerTipsList("Lakukan teknik relaksasi"),
                                            new SmokerTipsList("Kelola waktu dan beban pekerjaan")
                                    };
                                } else if (totalHasil < 16) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Berat");
                                    tV_hasil.setTextColor(Color.parseColor("#BB5C0B"));
                                    iV_status.setImageResource(R.drawable.ic_berat);
                                    tV_desc.setText(
                                            "Tingkat kecemasan Anda berada dalam kategori berat. "
                                                    + "Perasaan cemas yang berlebihan dapat mengganggu aktivitas, "
                                                    + "hubungan sosial, maupun produktivitas."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Cari dukungan keluarga atau teman"),
                                            new SmokerTipsList("Kurangi sumber stres yang dapat dihindari"),
                                            new SmokerTipsList("Lakukan olahraga ringan secara rutin")
                                    };
                                } else {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Sangat Berat");
                                    tV_hasil.setTextColor(Color.parseColor("#C6110A"));
                                    iV_status.setImageResource(R.drawable.ic_sangat_berat);
                                    tV_desc.setText(
                                            "Tingkat kecemasan Anda berada dalam kategori sangat berat. "
                                                    + "Kecemasan yang dirasakan sudah cukup intens dan berpotensi "
                                                    + "mengganggu kualitas hidup sehingga memerlukan perhatian lebih."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Segera cari bantuan profesional"),
                                            new SmokerTipsList("Jangan menghadapi masalah sendirian"),
                                            new SmokerTipsList("Minta dukungan keluarga dan teman terdekat")
                                    };
                                }
                                InformasiMenjauhiRokokAdapter adapter = new InformasiMenjauhiRokokAdapter(myListData);
                                rV_tips.setHasFixedSize(true);
                                rV_tips.setLayoutManager(new LinearLayoutManager(HasilStressActivity.this));
                                rV_tips.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(HasilStressActivity.this, "Dokumen hasil kecemasan tidak ditemukan di Firestore! (ID: " + id + ")", Toast.LENGTH_LONG).show();
                            tV_hasil.setText("Data Kosong");
                            tV_angka.setText("Score -");
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(HasilStressActivity.this, "Gagal mengambil data cemas: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                tV_hasil.setText("Error Load");
                tV_angka.setText("Score -");
            });
        } else if (status.equals("depresi")) {
            tV_hasil_anda.setText("Tingkat Depresi Anda");
            DocumentReference checkdata = fStore.collection("depresi").document(id);

            checkdata.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            if (data != null) {
                                int totalHasil = calculateTotalHasil(data);
                                if (totalHasil == -1) {
                                    tV_hasil.setText("Data Tidak Lengkap");
                                    tV_hasil.setTextColor(Color.parseColor("#C6110A"));
                                    tV_angka.setText("Score -");
                                    tV_desc.setText("Kuesioner belum diselesaikan secara lengkap.");
                                    return;
                                }
                                Log.d("Score", "Score " + totalHasil);
                                SmokerTipsList[] myListData;
                                if (totalHasil < 7) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Normal");
                                    tV_hasil.setTextColor(Color.parseColor("#C0D4B4"));
                                    iV_status.setImageResource(R.drawable.ic_normal);
                                    tV_desc.setText(
                                            "Tingkat depresi Anda berada dalam kategori normal. "
                                                    + "Kondisi emosional Anda saat ini relatif stabil dan tidak "
                                                    + "menunjukkan tanda depresi yang signifikan."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Pertahankan pola hidup sehat"),
                                            new SmokerTipsList("Tidur yang cukup setiap hari"),
                                            new SmokerTipsList("Lakukan aktivitas yang Anda sukai")
                                    };
                                } else if (totalHasil < 9) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Ringan");
                                    tV_hasil.setTextColor(Color.parseColor("#52964D"));
                                    iV_status.setImageResource(R.drawable.ic_ringan);
                                    tV_desc.setText(
                                            "Tingkat depresi Anda berada dalam kategori ringan. "
                                                    + "Anda mungkin mengalami perubahan suasana hati sesekali, "
                                                    + "namun masih dapat menjalani aktivitas dengan baik."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Luangkan waktu untuk relaksasi"),
                                            new SmokerTipsList("Kurangi aktivitas yang terlalu membebani"),
                                            new SmokerTipsList("Atur jadwal istirahat dengan baik")
                                    };
                                } else if (totalHasil < 14) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Sedang");
                                    tV_hasil.setTextColor(Color.parseColor("#E9B010"));
                                    iV_status.setImageResource(R.drawable.ic_sedang);
                                    tV_desc.setText(
                                            "Tingkat depresi Anda berada dalam kategori sedang. "
                                                    + "Perasaan sedih, kehilangan motivasi, atau kelelahan emosional "
                                                    + "mulai dirasakan dan memerlukan perhatian."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Berbagi cerita dengan orang terpercaya"),
                                            new SmokerTipsList("Lakukan teknik relaksasi"),
                                            new SmokerTipsList("Kelola waktu dan beban pekerjaan")
                                    };
                                } else if (totalHasil < 17) {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Berat");
                                    tV_hasil.setTextColor(Color.parseColor("#BB5C0B"));
                                    iV_status.setImageResource(R.drawable.ic_berat);
                                    tV_desc.setText(
                                            "Tingkat depresi Anda berada dalam kategori berat. "
                                                    + "Gejala yang dialami dapat memengaruhi aktivitas sehari-hari "
                                                    + "dan hubungan sosial Anda."
                                    );

                                    myListData = new SmokerTipsList[]{
                                            new SmokerTipsList("Cari dukungan keluarga atau teman"),
                                            new SmokerTipsList("Kurangi sumber stres yang dapat dihindari"),
                                            new SmokerTipsList("Lakukan olahraga ringan secara rutin")
                                    };
                                } else {
                                    tV_angka.setText(totalHasil + "/21");
                                    tV_hasil.setText("Sangat Berat");
                                    tV_hasil.setTextColor(Color.parseColor("#C6110A"));
                                    iV_status.setImageResource(R.drawable.ic_sangat_berat);
                                    tV_desc.setText(
                                            "Tingkat depresi Anda berada dalam kategori sangat berat. "
                                                    + "Kondisi ini memerlukan perhatian serius karena dapat berdampak "
                                                    + "besar pada kesehatan mental dan kualitas hidup. "
                                                    + "Pertimbangkan untuk berkonsultasi dengan tenaga profesional."
                                    );
                                    myListData = new SmokerTipsList[] {
                                            new SmokerTipsList("Segera cari bantuan profesional"),
                                            new SmokerTipsList("Jangan menghadapi masalah sendirian"),
                                            new SmokerTipsList("Minta dukungan keluarga dan teman terdekat")
                                    };
                                }
                                InformasiMenjauhiRokokAdapter adapter = new InformasiMenjauhiRokokAdapter(myListData);
                                rV_tips.setHasFixedSize(true);
                                rV_tips.setLayoutManager(new LinearLayoutManager(HasilStressActivity.this));
                                rV_tips.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(HasilStressActivity.this, "Dokumen hasil depresi tidak ditemukan di Firestore! (ID: " + id + ")", Toast.LENGTH_LONG).show();
                            tV_hasil.setText("Data Kosong");
                            tV_angka.setText("Score -");
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(HasilStressActivity.this, "Gagal mengambil data depresi: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                tV_hasil.setText("Error Load");
                tV_angka.setText("Score -");
            });
        }
    }

    // Fungsi untuk menghitung total hasil
    private int calculateTotalHasil(Map<String, Object> data) {
        int totalHasil = 0;

        for (int i = 1; i <= 7; i++) {
            String questKey = "quest" + i;
            String answer = (String) data.get(questKey);

            // UBAH DI SINI: Cek jika data tidak null dan tidak kosong
            if (answer != null && !answer.trim().isEmpty()) {
                int hasil = 0;
                switch (answer) {
                    case "Tidak sesuai/tidak pernah":
                        hasil = 0;
                        break;
                    case "Perilaku muncul sesekali/jarang":
                        hasil = 1;
                        break;
                    case "Sesuai/cukup sering muncul":
                        hasil = 2;
                        break;
                    case "Sangat sesuai/sangat sering muncul":
                        hasil = 3;
                        break;
                    default:
                        Toast.makeText(HasilStressActivity.this, "Data tidak valid! Pertanyaan ke-" + i + " belum dijawab.", Toast.LENGTH_LONG).show();
                        return -1;
                }
                totalHasil += hasil;
            } else {
                Toast.makeText(HasilStressActivity.this, "Data tidak valid! Pertanyaan ke-" + i + " belum dijawab.", Toast.LENGTH_LONG).show();
                return -1;
            }
        }
        return totalHasil;
    }
    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}