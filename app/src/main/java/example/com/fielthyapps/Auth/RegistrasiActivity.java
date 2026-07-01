package example.com.fielthyapps.Auth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class RegistrasiActivity extends AppCompatActivity {
    private EditText nama, email,date,password, phone;
    private Button registrasi;
    private TextView login;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    private LinearLayout btn_google_registrasi;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private DatabaseHelper dbHelper;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private ProgressDialog mLoading;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String emailUser,namaUser, phoneUser, gender, passwordUser,sdate;
    int mYear, mMonth, mDay;
    static final int DATE_DIALOG_ID = 1;
    private String[] arrMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        dbHelper = new DatabaseHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mLoading = new ProgressDialog(this);
        mLoading.setMessage("Mohon Tunggu..");
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(
                                getString(
                                        R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        mGoogleSignInClient =
                GoogleSignIn.getClient(this, gso);

        nama = findViewById(R.id.eT_name);
        email = findViewById(R.id.eT_email);
        phone = findViewById(R.id.eT_phone);
        date = findViewById(R.id.eT_date);
        password = findViewById(R.id.eT_password_registrasi);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        registrasi = findViewById(R.id.btn_registrasi);
        login = findViewById(R.id.tV_masuk);
        btn_google_registrasi =
                findViewById(R.id.btn_google_registrasi);

        // Bind custom Gender layouts
        LinearLayout btnMale = findViewById(R.id.btn_gender_male);
        LinearLayout btnFemale = findViewById(R.id.btn_gender_female);
        ImageView ivMale = findViewById(R.id.iv_gender_male);
        ImageView ivFemale = findViewById(R.id.iv_gender_female);
        TextView tvMale = findViewById(R.id.layout_laki);
        TextView tvFemale = findViewById(R.id.layout_perempuan);

        // Tidak ada gender yang dipilih saat awal
        btnMale.setBackgroundResource(R.drawable.bg_input_whiteblue);
        ivMale.setColorFilter(Color.parseColor("#7A7A7A"));
        tvMale.setTextColor(Color.parseColor("#7A7A7A"));

        btnFemale.setBackgroundResource(R.drawable.bg_input_whiteblue);
        ivFemale.setColorFilter(Color.parseColor("#7A7A7A"));
        tvFemale.setTextColor(Color.parseColor("#7A7A7A"));

        btnMale.setOnClickListener(view -> {
            gender = "Laki - Laki";
            btnMale.setBackgroundResource(R.drawable.bg_btn_rounded_teal);
            ivMale.setColorFilter(Color.WHITE);
            tvMale.setTextColor(Color.WHITE);

            btnFemale.setBackgroundResource(R.drawable.bg_input_whiteblue);
            ivFemale.setColorFilter(Color.parseColor("#7A7A7A"));
            tvFemale.setTextColor(Color.parseColor("#7A7A7A"));
        });

        btnFemale.setOnClickListener(view -> {
            gender = "Perempuan";
            btnFemale.setBackgroundResource(R.drawable.bg_btn_rounded_teal);
            ivFemale.setColorFilter(Color.WHITE);
            tvFemale.setTextColor(Color.WHITE);

            btnMale.setBackgroundResource(R.drawable.bg_input_whiteblue);
            ivMale.setColorFilter(Color.parseColor("#7A7A7A"));
            tvMale.setTextColor(Color.parseColor("#7A7A7A"));
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrasiActivity.this,LoginActivity.class));
                finish();
            }
        });

        if (btn_google_registrasi != null) {
            btn_google_registrasi.setOnClickListener(
                    v -> signInWithGoogle());
        }

        registrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialRegist();
            }
        });

        ivTogglePassword.setOnClickListener(v -> {

            if (isPasswordVisible) {

                password.setTransformationMethod(
                        android.text.method.PasswordTransformationMethod.getInstance());

                ivTogglePassword.setImageResource(
                        R.drawable.ic_eye_off);

            } else {

                password.setTransformationMethod(
                        android.text.method.HideReturnsTransformationMethod.getInstance());

                ivTogglePassword.setImageResource(
                        R.drawable.ic_eye);
            }

            password.setSelection(
                    password.getText().length());

            isPasswordVisible = !isPasswordVisible;
        });

        date.setFocusable(false);
        date.setClickable(true);

        date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            RegistrasiActivity.this,
                            mDateSetListener,
                            mYear,
                            mMonth,
                            mDay);

            datePickerDialog.show();
        });
    }

    private void initialRegist(){
        emailUser = email.getText().toString();
        namaUser = nama.getText().toString();
        phoneUser = phone.getText().toString();
        passwordUser = password.getText().toString();

        if (emailUser.isEmpty()){
            email.setError("Masukan Email Terlebih Dahulu");
            email.setFocusable(true);
        }else if (!emailUser.matches(emailPattern)){
            email.setError("Masukan Format Email yang Benar");
            email.setFocusable(true);
        }else if (phoneUser.isEmpty()) {
            phone.setError("Masukan Nomor HP Terlebih Dahulu");
            phone.setFocusable(true);
        }else if (namaUser.isEmpty()) {
            nama.setError("Masukan Nama Terlebih Dahulu");
            nama.setFocusable(true);
        }else if (passwordUser.isEmpty()) {
            password.setError("Masukan Password Anda");
            password.setFocusable(true);
        } else if (passwordUser.length() < 8) {
            password.setError("Masukan Password minimal 8");
            password.setFocusable(true);
        } else if (gender == null) {
            Toast.makeText(
                    RegistrasiActivity.this,
                    "Silakan pilih jenis kelamin",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            registrasiUser(emailUser, passwordUser);
        }
    }

    public void registrasiUser(String emailUser, String passwordUser) {
        mLoading.show();

        firebaseAuth.createUserWithEmailAndPassword(emailUser, passwordUser)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user == null) return;

                            String uid = user.getUid();

                            Calendar now = Calendar.getInstance();
                            Calendar tanggallahir = Calendar.getInstance();
                            tanggallahir.set(mYear, mMonth, mDay);

                            int years = now.get(Calendar.YEAR) - tanggallahir.get(Calendar.YEAR);
                            int months = now.get(Calendar.MONTH) - tanggallahir.get(Calendar.MONTH);
                            int days = now.get(Calendar.DAY_OF_MONTH) - tanggallahir.get(Calendar.DAY_OF_MONTH);
                            if (days < 0) {
                                months--;
                                days += now.getActualMaximum(Calendar.DAY_OF_MONTH);
                            }
                            if (months < 0) {
                                years--;
                                months += 12;
                            }
                            int umur = years;

                            //simpan sqlite
                            dbHelper.insertUser(uid, emailUser, passwordUser, namaUser, phoneUser, sdate, gender, umur);

                            // 1. Simpan ke Firebase Firestore (Langsung, tanpa Supabase)
                            saveToFirestore(uid, emailUser, namaUser, phoneUser, sdate, gender, umur);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mLoading.dismiss();
                        Toast.makeText(RegistrasiActivity.this, "Pendaftaran Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveToFirestore(String uid, String emailUser, String namaUser,String phoneUser, String sdate, String gender, int umur) {
        DocumentReference documentReference = fStore.collection("user").document(uid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", emailUser);
        hashMap.put("uid", uid);
        hashMap.put("nama", namaUser);
        hashMap.put("phone", phoneUser);
        hashMap.put("birthday", sdate);
        hashMap.put("gender", gender);
        hashMap.put("umur", umur);

        documentReference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoading.dismiss();
                Toast.makeText(RegistrasiActivity.this, "Berhasil mendaftar!\n" + emailUser, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            sdate = LPad(mDay + "", "0", 2) + " " + arrMonth[mMonth] + ", " + mYear;
            date.setText(sdate);
        }
    };

    private static String LPad(String schar, String spad, int len) {
        String sret = schar;
        for (int i = sret.length(); i < len; i++) {
            sret = spad + sret;
        }
        return sret;
    }

    private void signInWithGoogle() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {

                    Intent signInIntent =
                            mGoogleSignInClient
                                    .getSignInIntent();

                    startActivityForResult(
                            signInIntent,
                            RC_SIGN_IN);
                });
    }

    @Override
    public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn
                            .getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account =
                        task.getResult(ApiException.class);

                if (account != null) {

                    firebaseAuthWithGoogle(
                            account.getIdToken());
                }

            } catch (ApiException e) {

                int statusCode =
                        e.getStatusCode();

                if (statusCode !=
                        GoogleSignInStatusCodes
                                .SIGN_IN_CANCELLED
                        && statusCode != 12501) {

                    Toast.makeText(
                            this,
                            "Google Sign-In Gagal",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(
            String idToken) {

        mLoading.setMessage(
                "Mendaftar dengan Google...");

        mLoading.show();

        AuthCredential credential =
                GoogleAuthProvider.getCredential(
                        idToken,
                        null);

        firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener(
                        this,
                        task -> {

                            if (task.isSuccessful()) {

                                FirebaseUser user =
                                        firebaseAuth
                                                .getCurrentUser();

                                if (user != null) {

                                    checkOrSyncGoogleUser(
                                            user);
                                }

                            } else {

                                mLoading.dismiss();

                                Toast.makeText(
                                        this,
                                        "Autentikasi Google Gagal",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
    }

    private void checkOrSyncGoogleUser(FirebaseUser user) {
        mLoading.setMessage("Menyinkronkan data...");
        String uid = user.getUid();
        String emailGoogle = user.getEmail();
        String namaGoogle = user.getDisplayName() != null ? user.getDisplayName() : "User Google";

        fStore.collection("user").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null && doc.exists()) {
                    // Akun sudah ada, langsung ke Login
                    mLoading.dismiss();
                    Toast.makeText(this, "Akun sudah terdaftar. Silakan login.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    // Akun baru, buat profil di Firestore
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("email", emailGoogle);
                    map.put("uid", uid);
                    map.put("nama", namaGoogle);
                    map.put("phone", "");
                    map.put("location", "Belum Diatur");
                    map.put("birthday", "Belum Diatur");
                    map.put("gender", "Belum Diatur");
                    map.put("umur", 0);

                    dbHelper.insertUser(
                            uid,
                            emailGoogle,
                            "",
                            namaGoogle,
                            "",
                            "Belum Diatur",
                            "Belum Diatur",
                            0
                    );

                    fStore.collection("user").document(uid).set(map)
                            .addOnSuccessListener(aVoid -> {
                                mLoading.dismiss();
                                Toast.makeText(this, "Berhasil Daftar dengan Google!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                mLoading.dismiss();
                                Toast.makeText(this, "Gagal membuat profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                mLoading.dismiss();
                Toast.makeText(this, "Gagal sinkronisasi data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}