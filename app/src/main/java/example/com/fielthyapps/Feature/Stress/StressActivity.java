package example.com.fielthyapps.Feature.Stress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class StressActivity extends AppCompatActivity {
    private LinearLayout LL_sect_one,LL_sect_two,LL_sect_three;
    private ImageView iV_back;
    private String id;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress);
        LL_sect_one = findViewById(R.id.LL_section_one);
        LL_sect_two = findViewById(R.id.LL_section_two);
        LL_sect_three = findViewById(R.id.LL_section_three);
        iV_back = findViewById(R.id.iV_kembali);

        Intent iin = getIntent();
        final Bundle b = iin.getExtras();

        if (b != null) {
            id = (String) b.get("id");
        }

        iV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StressActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        LL_sect_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StressActivity.this,SectionSatuActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        LL_sect_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StressActivity.this,SectionDuaActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        LL_sect_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StressActivity.this,SectionTigaActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

    }
}