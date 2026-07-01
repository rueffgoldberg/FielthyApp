package example.com.fielthyapps.Feature.Smoker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import example.com.fielthyapps.R;

public class TipsBerhentiMerokokActivity extends AppCompatActivity {
    private RecyclerView rV_tips;
    private ImageView iV_back;
    private String getBatang,getRupiah,getBungkus,getTahun,id,uid,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_berhenti_merokok);
        rV_tips = findViewById(R.id.rV_tips);
        iV_back = findViewById(R.id.iV_kembali);

        Intent iin = getIntent();
        final Bundle b = iin.getExtras();

        if (b != null) {
            status = (String) b.get("status");
            id = (String) b.get("id");
            uid = (String) b.get("uid");
            getBatang = (String) b.get("batang");
            getTahun = (String) b.get("tahun");
            getBungkus = (String) b.get("bungkus");
            getRupiah = (String) b.get("rupiah");
        }

        iV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TipsBerhentiMerokokActivity.this, HasilSmokerActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("uid", uid);
                intent.putExtra("batang", getBatang);
                intent.putExtra("tahun", getTahun);
                intent.putExtra("bungkus", getBungkus);
                intent.putExtra("rupiah", getRupiah);
                intent.putExtra("status", "testsmoker");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        SmokerTipsList[] myListData = new SmokerTipsList[] {

                new SmokerTipsList(
                        "Bulatkan tekad dan motivasi untuk berhenti merokok."
                ),

                new SmokerTipsList(
                        "Tentukan metode berhenti merokok, baik secara langsung maupun bertahap."
                ),

                new SmokerTipsList(
                        "Kenali waktu dan situasi yang paling sering memicu keinginan merokok."
                ),

                new SmokerTipsList(
                        "Saat muncul keinginan merokok, cobalah menunda dan alihkan perhatian ke aktivitas lain."
                ),

                new SmokerTipsList(
                        "Lakukan olahraga secara teratur untuk membantu mengurangi keinginan merokok."
                ),

                new SmokerTipsList(
                        "Mintalah dukungan dari keluarga, teman, atau orang terdekat."
                ),

                new SmokerTipsList(
                        "Konsultasikan dengan dokter atau tenaga kesehatan jika mengalami kesulitan berhenti merokok."
                )

        };
        InformasiMenjauhiRokokAdapter adapter = new InformasiMenjauhiRokokAdapter(myListData);
        rV_tips.setHasFixedSize(true);
        rV_tips.setLayoutManager(new LinearLayoutManager(this));
        rV_tips.setAdapter(adapter);
    }
}