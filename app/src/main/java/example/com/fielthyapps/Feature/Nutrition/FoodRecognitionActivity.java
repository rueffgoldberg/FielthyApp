package example.com.fielthyapps.Feature.Nutrition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import example.com.fielthyapps.Utils.ImageAnalyzer;
import example.com.fielthyapps.databinding.ActivityFoodRecognitionBinding;

public class FoodRecognitionActivity extends AppCompatActivity {
    public static final int WIDTH = 224;
    public static final int HEIGHT = 224;
    public static final int REQUEST_CODE_PERMISSIONS = 123;
    public static final int REQUEST_CODE_GALLERY = 124;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private Preview preview;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;
    private final Executor cameraExecutor = Executors.newSingleThreadExecutor();

    private ActivityFoodRecognitionBinding binding;
    private FoodRecognitionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFoodRecognitionBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FoodRecognitionViewModel.class);

        viewModel.recognition.observe(this, recognition -> {
            if (recognition != null) {
                binding.percentTextView.setText(Math.round(recognition.getConfidence() * 100) + "%");
                binding.resultsTextView.setText(recognition.getLabel() + " ");
                binding.btnCamera.setOnClickListener((v -> {
                    Intent intent = new Intent(FoodRecognitionActivity.this, FoodResultActivity.class);
                    intent.putExtra("name", recognition.getLabel());
                    startActivity(intent);
                    finish();
                }));
            }
        });

        binding.btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private Boolean allPermissionsGranted() {
        return Arrays.stream(REQUIRED_PERMISSIONS).allMatch(
            permission -> ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                // Cek apakah izin sudah ditolak permanen ("Jangan tanya lagi")
                boolean isPermanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.CAMERA);

                if (isPermanentlyDenied) {
                    // Arahkan user ke halaman pengaturan aplikasi
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Izin Kamera Diperlukan")
                            .setMessage("Fitur Food Recognition membutuhkan izin kamera.\n\nSilakan aktifkan izin Kamera secara manual melalui Pengaturan Aplikasi.")
                            .setPositiveButton("Buka Pengaturan", (dialog, which) -> {
                                android.content.Intent intent = new android.content.Intent(
                                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                android.net.Uri uri = android.net.Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                finish();
                            })
                            .setNegativeButton("Batal", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                } else {
                    Toast.makeText(this, "Izin kamera diperlukan untuk fitur ini", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            android.net.Uri selectedImage = data.getData();
            try {
                java.io.InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(imageStream);
                
                ImageAnalyzer tempAnalyzer = new ImageAnalyzer(this, recognition -> {
                    Intent intent = new Intent(FoodRecognitionActivity.this, FoodResultActivity.class);
                    intent.putExtra("name", recognition.getLabel());
                    startActivity(intent);
                    finish();
                });
                tempAnalyzer.analyzeBitmap(bitmap);
                
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar dari galeri", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                preview = new Preview.Builder().build();

                imageAnalyzer = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalyzer.setAnalyzer(cameraExecutor, new ImageAnalyzer(this, recognition -> {
                    // updating the list of recognised object
                    viewModel.updateData(recognition);
                }));

                // Select camera, back is the default. If it is not available, choose front camera
                CameraSelector cameraSelector = cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                        ? CameraSelector.DEFAULT_BACK_CAMERA
                        : CameraSelector.DEFAULT_FRONT_CAMERA;

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();

                    // Bind use cases to camera - try to bind everything at once and CameraX will find
                    // the best combination.
                    camera = cameraProvider.bindToLifecycle(
                            this, cameraSelector, preview, imageAnalyzer
                    );

                    // Attach the preview to preview view, aka View Finder
                    preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                } catch (Exception exc) {
                    Log.e("TAG", "Use case binding failed", exc);
                }
            } catch (ExecutionException | InterruptedException | CameraInfoUnavailableException e) {
                // Handle exceptions
                Log.e("TAG", "Camera initialization failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }
}