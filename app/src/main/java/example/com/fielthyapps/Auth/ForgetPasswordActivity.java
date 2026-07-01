package example.com.fielthyapps.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import example.com.fielthyapps.R;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText email;
    private Button submit;
    private ProgressDialog mLoading;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.eT_email_forget);
        submit = findViewById(R.id.btn_submit_forget);
        
        mLoading = new ProgressDialog(this);
        mLoading.setMessage("Mohon Tunggu...");
        
        firebaseAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString().trim();

                if (emailStr.isEmpty()) {
                    email.setError("Masukkan Email");
                    email.requestFocus();
                } else {
                    sendResetEmail(emailStr);
                }
            }
        });
    }

    private void sendResetEmail(String emailStr) {
        mLoading.show();
        // Langsung minta Firebase kirim email reset
        firebaseAuth.sendPasswordResetEmail(emailStr)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mLoading.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPasswordActivity.this, 
                                "Link reset password telah dikirim ke email Anda.", 
                                Toast.LENGTH_LONG).show();
                            
                            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            // Firebase akan memberitahu jika email memang tidak terdaftar di sistem Auth mereka
                            String error = task.getException() != null ? task.getException().getMessage() : "Gagal mengirim email reset.";
                            Toast.makeText(ForgetPasswordActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
