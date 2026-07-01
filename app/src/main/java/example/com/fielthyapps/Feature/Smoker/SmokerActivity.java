package example.com.fielthyapps.Feature.Smoker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class SmokerActivity extends AppCompatActivity {

    private LinearLayout btn_merokok,
            btn_tidakmerokok,
            btn_kalkulator_merokok;
    private ImageView iV_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoker);

        btn_merokok = findViewById(R.id.btn_merokok);
        btn_tidakmerokok = findViewById(R.id.btn_tidakmerokok);
        btn_kalkulator_merokok = findViewById(R.id.btn_kalkulator_merokok);
        iV_back = findViewById(R.id.iV_kembali);

        iV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SmokerActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btn_merokok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SmokerActivity.this, TestSmokerActivity.class);
                intent.putExtra("merokok", "merokok");
                startActivity(intent);
            }
        });

        btn_tidakmerokok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SmokerActivity.this, HasilTidakMerokokActivity.class);
                startActivity(intent);
            }
        });

        btn_kalkulator_merokok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent =
                                new Intent(
                                        SmokerActivity.this,
                                        KalkulatorMerokokActivity.class
                                );

                        startActivity(intent);
                    }
                }
        );
    }
}