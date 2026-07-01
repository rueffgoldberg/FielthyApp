package example.com.fielthyapps.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import example.com.fielthyapps.R;

public class EditAuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_auth);

        EditText eT_email = findViewById(R.id.eT_email_auth);
        EditText eT_password = findViewById(R.id.eT_password_edit);
        EditText eT_email_baru = findViewById(R.id.eT_email_baru);
        Button btn_edit = findViewById(R.id.btn_edit_auth_submit);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            eT_email.setText(user.getEmail() != null ? user.getEmail() : "");
            disableEditText(eT_email);
        }

        btn_edit.setOnClickListener(view -> {
            String emailBaru = eT_email_baru.getText().toString().trim();
            String passwordInput = eT_password.getText().toString().trim();

            if (emailBaru.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(EditAuthActivity.this, "Harap lengkapi password dan email baru", Toast.LENGTH_SHORT).show();
                return;
            }

            reauthenticateAndUpdateEmail(passwordInput, emailBaru);
        });
    }

    // Function to reauthenticate and update email
    private void reauthenticateAndUpdateEmail(String currentPassword, String newEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Reauthenticate", "Reauthentication successful");
                            updateAndVerifyEmail(user, newEmail);
                        } else {
                            Exception exception = task.getException();
                            String errorMsg = exception != null ? exception.getMessage() : "Unknown error";
                            Log.e("Reauthenticate", "Error reauthenticating: " + errorMsg);
                            Toast.makeText(this, "Autentikasi gagal. Cek kata sandi Anda. " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Log.e("UserError", "User or email is null");
            Toast.makeText(this, "Sesi pengguna tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAndVerifyEmail(FirebaseUser user, String newEmail) {
        // Firebase Auth now uses verifyBeforeUpdateEmail, updateEmail is deprecated.
        user.verifyBeforeUpdateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("EmailUpdate", "Verification email sent. Email will update upon verification.");
                        Toast.makeText(this, "Email verifikasi dikirim ke " + newEmail + ". Email berubah setelah diverifikasi.", Toast.LENGTH_LONG).show();
                        updateEmailInFirestore(user.getUid(), newEmail);
                        finish(); // Kembali ke halaman sebelumnya
                    } else {
                        Exception exception = task.getException();
                        String errorMsg = exception != null ? exception.getMessage() : "Unknown error";
                        Log.e("EmailUpdate", "Error updating email: " + errorMsg);
                        Toast.makeText(this, "Gagal mengupdate email: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Function to update email in Firestore
    private void updateEmailInFirestore(String uid, String newEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("user").document(uid);

        userRef.update("email", newEmail)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreUpdate", "Email successfully updated in Firestore"))
                .addOnFailureListener(e -> Log.e("FirestoreUpdate", "Error updating email in Firestore", e));
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }
}