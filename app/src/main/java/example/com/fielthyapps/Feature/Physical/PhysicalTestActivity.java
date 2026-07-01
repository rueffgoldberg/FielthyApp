package example.com.fielthyapps.Feature.Physical;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import example.com.fielthyapps.R;

public class PhysicalTestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tV_time, tV_btn_track, hari, tanggal;
    private EditText eT_tahun, eT_gender, eT_beratbadan, eT_tinggibadan;
    private LinearLayout btn_track, btnPanduan6MWT;
    private ImageView iV_back;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;

    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationClient;

    private final List<LatLng> pathPoints = new ArrayList<>();
    private Polyline polyline;

    private BroadcastReceiver trackingReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_test);

        tV_time = findViewById(R.id.tV_time);
        tV_btn_track = findViewById(R.id.tV_btn_track);
        hari = findViewById(R.id.tV_hari);
        tanggal = findViewById(R.id.tV_tanggal);

        eT_tahun = findViewById(R.id.eT_6mwt_umur);
        eT_gender = findViewById(R.id.eT_JenisKelamin6mwt);
        eT_beratbadan = findViewById(R.id.eT_beratBadanenamwt);
        eT_tinggibadan = findViewById(R.id.eT_tinggiBadanenamwt);

        btn_track = findViewById(R.id.btn_track);
        btnPanduan6MWT = findViewById(R.id.btnPanduan6MWT);
        iV_back = findViewById(R.id.iV_kembali);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupDate();
        checkUserData();
        disableEditText(eT_tahun);
        disableEditText(eT_gender);

        tV_time.setText("06:00");

        iV_back.setOnClickListener(view -> {
            Intent intent = new Intent(PhysicalTestActivity.this, PhysicalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        checkLocationPermission();
        checkGpsStatus();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btn_track.setOnClickListener(view -> toggleTracking());
        btnPanduan6MWT.setOnClickListener(view -> {
            Intent intent =
                    new Intent(
                            PhysicalTestActivity.this,
                            Panduan6MWTActivity.class
                    );

            startActivity(intent);

        });

        setupBroadcastReceiver();

        if (TrackingService.isServiceRunning) {
            tV_btn_track.setText("Sedang Tracking...");
            btn_track.setEnabled(false);
            btn_track.setAlpha(0.7f);
        }
    }

    private void setupBroadcastReceiver() {
        trackingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) return;

                switch (intent.getAction()) {
                    case TrackingService.ACTION_UPDATE_TIME:
                        long elapsedMillis = intent.getLongExtra("elapsedMillis", 0);
                        long duration = TimeUnit.MINUTES.toMillis(6);
                        long millisUntilFinished = duration - elapsedMillis;
                        if (millisUntilFinished < 0) millisUntilFinished = 0;

                        String sDuration = String.format(Locale.ENGLISH, "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                        tV_time.setText(sDuration);
                        break;

                    case TrackingService.ACTION_UPDATE_LOCATION:
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lng = intent.getDoubleExtra("lng", 0);
                        pathPoints.add(new LatLng(lat, lng));
                        drawPolyline();
                        break;

                    case TrackingService.ACTION_FINISHED:
                        tV_btn_track.setText("Start Tracking >");
                        btn_track.setEnabled(true);
                        btn_track.setAlpha(1.0f);
                        tV_time.setText("00:00");
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(TrackingService.ACTION_UPDATE_TIME);
        filter.addAction(TrackingService.ACTION_UPDATE_LOCATION);
        filter.addAction(TrackingService.ACTION_FINISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(trackingReceiver, filter);
    }

    private void setupDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.LocalDateTime currentDateTime = java.time.LocalDateTime.now();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE", new Locale("id", "ID"));

            String formattedDate = currentDateTime.format(dateFormatter);
            hari.setText(currentDateTime.format(dayFormatter));
            tanggal.setText(formattedDate);
        }
    }

    private void toggleTracking() {
        String age = eT_tahun.getText().toString();
        String gender = eT_gender.getText().toString();
        String beratbadan = eT_beratbadan.getText().toString();
        String tinggibadan = eT_tinggibadan.getText().toString();

        if (android.text.TextUtils.isEmpty(age) || android.text.TextUtils.isEmpty(gender) || android.text.TextUtils.isEmpty(beratbadan) || android.text.TextUtils.isEmpty(tinggibadan)) {
            android.widget.Toast.makeText(this, "Mohon isi semua Data Diri terlebih dahulu sebelum Start Tracking!", android.widget.Toast.LENGTH_LONG).show();
            return;
        }

        final long duration = TimeUnit.MINUTES.toMillis(6);

        if (!TrackingService.isServiceRunning) {
            pathPoints.clear();
            if (polyline != null) {
                polyline.remove();
            }

            tV_btn_track.setText("Sedang Tracking...");
            btn_track.setEnabled(false);
            btn_track.setAlpha(0.7f);

            Intent serviceIntent = new Intent(this, TrackingService.class);
            serviceIntent.setAction(TrackingService.ACTION_START);
            serviceIntent.putExtra("duration", duration);
            serviceIntent.putExtra("type", "0");
            serviceIntent.putExtra("age", age);
            serviceIntent.putExtra("gender", gender);
            serviceIntent.putExtra("beratbadan", beratbadan);
            serviceIntent.putExtra("tinggibadan", tinggibadan);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } else {
            Toast.makeText(this, "Tes sedang berjalan dan tidak bisa dihentikan secara manual.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserData() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            return;
        }

        DocumentReference documentReference = fStore.collection("user").document(user.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (value == null) {
                return;
            }

            Long umurValue = value.getLong("umur");
            String genderUser = value.getString("gender");

            if (umurValue != null) {
                eT_tahun.setText(String.valueOf(umurValue));
            }

            if (genderUser != null) {
                eT_gender.setText(genderUser);
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    private void checkGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS tidak aktif, aktifkan sekarang?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, id) ->
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void drawPolyline() {
        if (gMap != null) {
            if (polyline != null) {
                polyline.remove();
            }

            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(ContextCompat.getColor(this, R.color.red))
                    .width(5)
                    .addAll(pathPoints);

            polyline = gMap.addPolyline(polylineOptions);

            if (!pathPoints.isEmpty()) {
                LatLng lastPoint = pathPoints.get(pathPoints.size() - 1);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 15f));
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }

        gMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                pathPoints.add(currentLocation);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (trackingReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(trackingReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (gMap != null) {
                    onMapReady(gMap);
                }
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan untuk tracking", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }
}
