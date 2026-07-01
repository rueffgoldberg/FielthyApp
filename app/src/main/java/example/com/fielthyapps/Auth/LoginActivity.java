package example.com.fielthyapps.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import android.widget.ImageView;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.Database.SessionManager;
import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText email_login, pass_login;
    private TextView forget, daftar;
    private Button btn_login;
    private LinearLayout btn_google_login;

    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private ProgressDialog mLoading;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Typeface poppins = ResourcesCompat.getFont(this, R.font.poppins_reg);

        email_login = findViewById(R.id.eT_email_login);
        pass_login = findViewById(R.id.eT_password_login);
        forget = findViewById(R.id.tV_forgetPass);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        daftar = findViewById(R.id.tV_daftar);
        btn_login = findViewById(R.id.btn_login);
        btn_google_login = findViewById(R.id.btn_google_login);

        mLoading = new ProgressDialog(this);
        mLoading.setMessage("Memverifikasi Akun...");

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        forget.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class)));
        daftar.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrasiActivity.class)));
        btn_login.setOnClickListener(view -> initialLogin());
        btn_google_login.setOnClickListener(view -> signInWithGoogle());
        ivTogglePassword.setOnClickListener(v -> {

            if (isPasswordVisible) {

                pass_login.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                ivTogglePassword.setImageResource(R.drawable.ic_eye);

            } else {

                pass_login.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );

                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);

            }

            pass_login.setTypeface(poppins);
            pass_login.setSelection(pass_login.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });
    }

    private void signInWithGoogle() {
        // Sign out dari sesi Google sebelumnya agar selalu muncul dialog pilih akun
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                int statusCode = e.getStatusCode();
                if (statusCode != GoogleSignInStatusCodes.SIGN_IN_CANCELLED && statusCode != 12501) {
                    Toast.makeText(this, "Google Sign-In Gagal" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mLoading.setMessage("Mencoba Masuk dengan Google...");
        mLoading.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                checkOrSyncGoogleUser(user);
                            }
                        } else {
                            mLoading.dismiss();
                            Toast.makeText(LoginActivity.this, "Autentikasi Firebase Gagal.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkOrSyncGoogleUser(FirebaseUser user) {
        mLoading.setMessage("Menyinkronkan data...");
        String uid = user.getUid();
        String email_user = user.getEmail();
        String name_user = user.getDisplayName() != null ? user.getDisplayName() : "User Google";

        fStore.collection("user").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null && doc.exists()) {
                    // User exists in Firestore, just start session and go to Home
                    sessionManager.createLoginSession(uid);
                    mLoading.dismiss();
                    Toast.makeText(LoginActivity.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                    goToHome();
                } else {
                    // New User from Google, let's create a default profile in Firestore only
                    String defaultLocation = "Belum Diatur";
                    String defaultBirthday = "Belum Diatur";
                    String defaultGender = "Belum Diatur";
                    int defaultAge = 0;

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("email", email_user);
                    map.put("uid", uid);
                    map.put("nama", name_user);
                    map.put("location", defaultLocation);
                    map.put("birthday", defaultBirthday);
                    map.put("gender", defaultGender);
                    map.put("umur", defaultAge);

                    fStore.collection("user").document(uid).set(map).addOnSuccessListener(aVoid -> {
                        sessionManager.createLoginSession(uid);
                        mLoading.dismiss();
                        Toast.makeText(LoginActivity.this, "Berhasil Registrasi Google", Toast.LENGTH_SHORT).show();
                        goToHome();
                    }).addOnFailureListener(e -> {
                        mLoading.dismiss();
                        Toast.makeText(LoginActivity.this, "Gagal membuat profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                mLoading.dismiss();
                Toast.makeText(LoginActivity.this, "Gagal sinkronisasi data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialLogin() {
        String email_user = email_login.getText().toString().trim();
        String password_user = pass_login.getText().toString().trim();

        if (email_user.isEmpty()) {
            email_login.setError("Masukkan Email");
            email_login.requestFocus();
        } else if (password_user.isEmpty()) {
            pass_login.setError("Masukkan Password");
            pass_login.requestFocus();
        } else if (!email_user.matches(emailPattern)) {
            email_login.setError("Format Email tidak valid");
            email_login.requestFocus();
        } else {
            login(email_user, password_user);
        }
    }

    private void login(String email_user, String password_user) {
        mLoading.show();

        firebaseAuth.signInWithEmailAndPassword(email_user, password_user)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                syncUserThenGoToHome(user.getUid(), email_user, password_user);
                            }
                        } else {
                            checkLocalLogin(email_user, password_user);
                        }
                    }
                });
    }

    private void syncUserThenGoToHome(String uid, String email, String password) {
        mLoading.setMessage("Menyinkronkan data...");
        fStore.collection("user").document(uid).get().addOnCompleteListener(task -> {
            sessionManager.createLoginSession(uid);
            mLoading.dismiss();
            Toast.makeText(LoginActivity.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
            goToHome();
        });
    }

    private void checkLocalLogin(String email_user, String password_user) {
        if (dbHelper.checkLogin(email_user, password_user)) {
            String uid = dbHelper.getUidByEmail(email_user);
            sessionManager.createLoginSession(uid);
            mLoading.dismiss();
            Toast.makeText(LoginActivity.this, "Berhasil Login (Mode Offline)", Toast.LENGTH_SHORT).show();
            goToHome();
        } else {
            mLoading.dismiss();
            Toast.makeText(LoginActivity.this, "Email atau Password salah. Silakan coba lagi.", Toast.LENGTH_LONG).show();
        }
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
