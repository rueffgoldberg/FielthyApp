package example.com.fielthyapps.Feature.Nutrition;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import example.com.fielthyapps.R;

public class DietSehatActivity extends AppCompatActivity

    private ImageView iV_kembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_sehat);

        iV_kembali = findViewById(R.id.iV_kembali);

        iV_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}