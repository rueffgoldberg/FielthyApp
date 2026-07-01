package example.com.fielthyapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import example.com.fielthyapps.Database.SessionManager;

import example.com.fielthyapps.Auth.LoginActivity;
import example.com.fielthyapps.Service.DataLayerListenerService;
import example.com.fielthyapps.Database.DatabaseHelper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(MainActivity.this);
                if (sessionManager.isLoggedIn()){
                    String uid = sessionManager.getCurrentUserUid();
                    if (uid != null && !uid.isEmpty()) {
                        syncHistoryAndGoHome(uid);
                    } else {
                        goToHome();
                    }
                }else{
                    Intent home = new Intent(MainActivity.this, example.com.fielthyapps.Auth.WelcomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(home);
                    finish();
                }
            }
        },1000);
    }

    private void syncHistoryAndGoHome(String uid) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        String[] collections = {
                "medcheck", "nutritiontest", "bmr",
                "6mwt", "balke", "restpattern",
                "smoker", "kalk_merokok", "stresstest", "foodrecognition"
        };
        String[] tables = {
                DatabaseHelper.TABLE_MEDCHECK, DatabaseHelper.TABLE_NUTRITION, DatabaseHelper.TABLE_BMR,
                DatabaseHelper.TABLE_PHYSICAL, DatabaseHelper.TABLE_PHYSICAL, DatabaseHelper.TABLE_REST,
                DatabaseHelper.TABLE_SMOKER, DatabaseHelper.TABLE_KALK_MEROKOK, DatabaseHelper.TABLE_STRESS, DatabaseHelper.TABLE_FOOD_RECOG
        };

        final int[] completedTasks = {0};

        for (int i = 0; i < collections.length; i++) {
            final String tableName = tables[i];
            fStore.collection(collections[i]).whereEqualTo("uid", uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                try {
                                    HashMap<String, Object> map = new HashMap<>(doc.getData());
                                    dbHelper.insertOrUpdateRecord(tableName, doc.getId(), map);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        completedTasks[0]++;
                        if (completedTasks[0] == collections.length) {
                            goToHome();
                        }
                    });
        }
    }

    private void goToHome() {
        Intent home = new Intent(MainActivity.this, HomeActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }
}