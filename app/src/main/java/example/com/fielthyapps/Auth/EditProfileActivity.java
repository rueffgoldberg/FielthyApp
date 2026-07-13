package example.com.fielthyapps.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import androidx.activity.result.ActivityResultLauncher;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import android.util.Base64;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.Database.SessionManager;
import example.com.fielthyapps.R;

public class EditProfileActivity extends AppCompatActivity {
    private EditText name, date;
    private String namaUser, tanggalLahirUser;
    private ImageView fotoProfile, add_img;
    private RadioButton male, female;

    private DatabaseHelper dbHelper;
    private FirebaseFirestore fStore;
    private SessionManager sessionManager;
    private String currentUid;
    private FirebaseUser firebaseUser;

    static int PReqcode = 1;
    Uri pickedImage;
    private ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;
    private ProgressDialog mLoading;
    private Button btn_submit_edit_profile;
    int mYear, mMonth, mDay;
    static final int DATE_DIALOG_ID = 1;
    private String[] arrMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        fStore = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            currentUid = firebaseUser.getUid();
        } else {
            finish();
            return;
        }

        mLoading = new ProgressDialog(this);
        mLoading.setMessage("Menyimpan Perubahan..");

        // Setup Image Cropper (vanniktech - Maven Central)
        cropImageLauncher = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                pickedImage = result.getUriContent();
                fotoProfile.setImageURI(pickedImage);
            } else {
                Toast.makeText(this, "Gagal memotong gambar", Toast.LENGTH_SHORT).show();
            }
        });

        fotoProfile = findViewById(R.id.profile_image);
        add_img = findViewById(R.id.iV_addImg);
        ImageView iV_kembali = findViewById(R.id.iV_kembali);
        name = findViewById(R.id.eT_name_profile);
        date = findViewById(R.id.eT_date_profile);
        male = findViewById(R.id.rB_laki);
        female = findViewById(R.id.rB_wanita);
        btn_submit_edit_profile = findViewById(R.id.btn_edit_profile_submit);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        checkUser();

        if (iV_kembali != null) {
            iV_kembali.setOnClickListener(v -> finish());
        }
        add_img.setOnClickListener(v -> checkAndRequestForPermission());
        fotoProfile.setOnClickListener(v -> checkAndRequestForPermission());
        date.setOnClickListener(v -> showDialog(DATE_DIALOG_ID));
        btn_submit_edit_profile.setOnClickListener(v -> updateUser());
    }

    private void checkUser() {
        if (firebaseUser == null) return;

        // Set default profile icon first
        Glide.with(this)
                .load(R.drawable.ic_profile)
                .into(fotoProfile);

        // Ambil data profil eksklusif dan real-time langsung dari Firestore
        fStore.collection("user").document(currentUid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (name != null) name.setText(documentSnapshot.getString("nama"));
                if (date != null) date.setText(documentSnapshot.getString("birthday"));

                String gender = documentSnapshot.getString("gender");
                if (gender != null) {
                    if (gender.equalsIgnoreCase("Laki - Laki") && male != null) {
                        male.setChecked(true);
                    } else if (gender.equalsIgnoreCase("Perempuan") && female != null) {
                        female.setChecked(true);
                    }
                }

                // Ambil foto profil dari Firestore
                if (documentSnapshot.contains("photoUrl")) {
                    String photoUrl = documentSnapshot.getString("photoUrl");
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(photoUrl)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(fotoProfile);
                    } else if (firebaseUser.getPhotoUrl() != null) {
                        Glide.with(EditProfileActivity.this)
                                .load(firebaseUser.getPhotoUrl())
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(fotoProfile);
                    }
                } else if (firebaseUser.getPhotoUrl() != null) {
                    Glide.with(EditProfileActivity.this)
                            .load(firebaseUser.getPhotoUrl())
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .into(fotoProfile);
                }
            }
        });
    }

    private void updateUser() {
        namaUser = name.getText().toString().trim();
        tanggalLahirUser = date.getText().toString().trim();

        if (namaUser.isEmpty()) {
            name.setError("Nama tidak boleh kosong");
            return;
        }

        String gender = male.isChecked() ? "Laki - Laki" : "Perempuan";
        int umur = hitungUmur(tanggalLahirUser);

        mLoading.show();

        // 1. Update Firestore eksklusif
        HashMap<String, Object> update = new HashMap<>();
        update.put("nama", namaUser);
        update.put("birthday", tanggalLahirUser);
        update.put("gender", gender);
        update.put("umur", umur);
        update.put("uid", currentUid);

        fStore.collection("user").document(currentUid).set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (pickedImage != null) {
                        uploadPhoto();
                    } else {
                        finishUpdate();
                    }
                })
                .addOnFailureListener(e -> {
                    mLoading.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Gagal Update ke Cloud: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadPhoto() {
        mLoading.setMessage("Menyimpan Foto...");

        try {
            InputStream inputStream = getContentResolver().openInputStream(pickedImage);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            byte[] imageBytes = byteBuffer.toByteArray();

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "profile_" + currentUid + ".jpg",
                            RequestBody.create(MediaType.parse("image/jpeg"), imageBytes))
                    .addFormDataPart("fileName", "profile_" + currentUid + ".jpg")
                    .addFormDataPart("folder", "/FielthyApps")
                    .build();

            String credentials = "private_6R/yFWWjMg6XsjsDspYRooj9rRU=:";
            String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            Request request = new Request.Builder()
                    .url("https://upload.imagekit.io/api/v1/files/upload")
                    .addHeader("Authorization", basicAuth)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        mLoading.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Gagal Upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseBody);
                            String uploadedUrl = jsonObject.getString("url");
                            Uri newImageUri = Uri.parse(uploadedUrl);

                            runOnUiThread(() -> {
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(newImageUri)
                                        .build();
                                firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        HashMap<String, Object> photoUpdate = new HashMap<>();
                                        photoUpdate.put("photoUrl", uploadedUrl);
                                        fStore.collection("user").document(currentUid).set(photoUpdate, SetOptions.merge())
                                                .addOnCompleteListener(t -> finishUpdate());
                                    } else {
                                        mLoading.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "Gagal simpan URL ke Firebase", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                mLoading.dismiss();
                                Toast.makeText(EditProfileActivity.this, "Gagal parsing respon", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            mLoading.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Error Upload Server", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });

        } catch (Exception e) {
            mLoading.dismiss();
            Toast.makeText(this, "Gagal memproses gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void finishUpdate() {
        if (mLoading.isShowing()) {
            mLoading.dismiss();
        }
        Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }

    private int hitungUmur(String tgl) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            Calendar now = Calendar.getInstance();
            Calendar bday = Calendar.getInstance();
            bday.setTime(dateFormat.parse(tgl));
            int age = now.get(Calendar.YEAR) - bday.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < bday.get(Calendar.DAY_OF_YEAR)) age--;
            return age;
        } catch (Exception e) { return 0; }
    }

    private void checkAndRequestForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PReqcode);
            } else openGallery();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqcode);
            } else openGallery();
        }
    }

    private void openGallery() {
        CropImageOptions options = new CropImageOptions();
        options.imageSourceIncludeGallery = true;
        options.imageSourceIncludeCamera = false;
        options.aspectRatioX = 1;
        options.aspectRatioY = 1;
        options.fixAspectRatio = true;
        options.cropShape = CropImageView.CropShape.OVAL;
        cropImageLauncher.launch(new CropImageContractOptions(null, options));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PReqcode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Izin akses galeri ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) {
            DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dpd;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, day) -> {
        mYear = year; mMonth = month; mDay = day;
        String s = String.format("%02d", mDay) + " " + arrMonth[mMonth] + ", " + mYear;
        date.setText(s);
    };
    private String safeGet(HashMap<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? String.valueOf(val) : "";
    }
}