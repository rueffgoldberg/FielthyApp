package example.com.fielthyapps.Feature.Nutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.databinding.ActivityNutritionBinding;

public class NutritionActivity extends AppCompatActivity {

    private String id;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore fStore;

    private ActivityNutritionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNutritionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            id = (String) b.get("id");
        }

        // BACK
        binding.iVKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NutritionActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // KALKULATOR BMR
        binding.LLKalkulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        NutritionActivity.this,
                        BMRActivity.class
                );

                intent.putExtra("id", id);

                startActivity(intent);
            }
        });


        // FOOD RECOGNITION
        binding.tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NutritionActivity.this, FoodRecognitionActivity.class);
                startActivity(intent);
            }
        });

    }
}