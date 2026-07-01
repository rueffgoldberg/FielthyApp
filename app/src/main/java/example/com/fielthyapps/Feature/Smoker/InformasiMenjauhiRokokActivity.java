package example.com.fielthyapps.Feature.Smoker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import example.com.fielthyapps.R;

public class InformasiMenjauhiRokokActivity extends AppCompatActivity {
    private RecyclerView rV_tips;
    private ImageView iV_back;
    private String getBatang,getRupiah,getBungkus,getTahun,id,uid,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi_menjauhi_rokok);
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
                Intent intent = new Intent(InformasiMenjauhiRokokActivity.this, HasilSmokerActivity.class);
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
                        "Setelah 20 menit, denyut nadi dan tekanan darah mulai kembali normal."
                ),

                new SmokerTipsList(
                        "Setelah 8 jam, kadar oksigen dalam darah meningkat dan karbon monoksida berkurang."
                ),

                new SmokerTipsList(
                        "Setelah 48 jam, karbon monoksida telah hilang dari tubuh serta indera perasa dan penciuman mulai membaik."
                ),

                new SmokerTipsList(
                        "Setelah 72 jam, saluran pernapasan mulai lebih rileks sehingga bernapas terasa lebih mudah."
                ),

                new SmokerTipsList(
                        "Setelah 2–12 minggu, sirkulasi darah membaik dan aktivitas fisik menjadi lebih ringan dilakukan."
                ),

                new SmokerTipsList(
                        "Setelah 3–9 bulan, batuk dan gangguan pernapasan berkurang serta fungsi paru meningkat."
                ),

                new SmokerTipsList(
                        "Setelah 1 tahun, risiko penyakit jantung koroner menurun hingga sekitar setengah dibandingkan perokok aktif."
                ),

                new SmokerTipsList(
                        "Setelah 10 tahun, risiko kematian akibat kanker paru berkurang secara signifikan dibandingkan perokok aktif."
                )
        };
        InformasiMenjauhiRokokAdapter adapter = new InformasiMenjauhiRokokAdapter(myListData);
        rV_tips.setHasFixedSize(true);
        rV_tips.setLayoutManager(new LinearLayoutManager(this));
        rV_tips.setAdapter(adapter);
    }
}