package example.com.fielthyapps.Feature.Physical;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.R;

public class TrackingService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE_TIME = "ACTION_UPDATE_TIME";
    public static final String ACTION_UPDATE_LOCATION = "ACTION_UPDATE_LOCATION";
    public static final String ACTION_FINISHED = "ACTION_FINISHED";

    private static final String CHANNEL_ID = "TrackingServiceChannel";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private List<LatLng> pathPoints = new ArrayList<>();
    private float totalDistance = 0;
    private Location lastLocation;

    public static boolean isServiceRunning = false;
    private CountDownTimer countDownTimer;
    private long trackingStartMillis = 0;
    private long elapsedMillis = 0;

    private String type, age, gender, beratbadan, tinggibadan, formattedDate;
    private long duration;

    private FirebaseFirestore fStore;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate() {
        super.onCreate();
        fStore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();
        setupLocationCallback();
        setupDate();
    }

    private void setupDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            formattedDate = currentDateTime.format(dateFormatter);
        } else {
            formattedDate = "";
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START.equals(action) && !isServiceRunning) {
                duration = intent.getLongExtra("duration", 0);
                type = intent.getStringExtra("type");
                age = intent.getStringExtra("age");
                gender = intent.getStringExtra("gender");
                beratbadan = intent.getStringExtra("beratbadan");
                tinggibadan = intent.getStringExtra("tinggibadan");

                startTracking();
            } else if (ACTION_STOP.equals(action)) {
                stopTracking();
            }
        }
        return START_NOT_STICKY;
    }

    private void startTracking() {
        isServiceRunning = true;
        pathPoints.clear();
        totalDistance = 0;
        lastLocation = null;
        elapsedMillis = 0;
        trackingStartMillis = System.currentTimeMillis();

        createNotificationChannel();
        Notification notification = createNotification("Tracking berjalan...");
        startForeground(1, notification);

        startLocationUpdates();

        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedMillis = System.currentTimeMillis() - trackingStartMillis;
                updateNotification();

                Intent updateIntent = new Intent(ACTION_UPDATE_TIME);
                updateIntent.putExtra("elapsedMillis", elapsedMillis);
                LocalBroadcastManager.getInstance(TrackingService.this).sendBroadcast(updateIntent);
            }

            @Override
            public void onFinish() {
                elapsedMillis = duration;
                stopLocationUpdates();
                isServiceRunning = false;

                Intent finishIntent = new Intent(ACTION_FINISHED);
                LocalBroadcastManager.getInstance(TrackingService.this).sendBroadcast(finishIntent);

                inputDataAndSubmit();
            }
        }.start();
    }

    private void stopTracking() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopLocationUpdates();
        isServiceRunning = false;
        stopForeground(true);
        stopSelf();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                .setMaxUpdateDelayMillis(10000)
                .build();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    Location currentLocation = locationResult.getLastLocation();
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    pathPoints.add(currentLatLng);

                    if (lastLocation != null) {
                        totalDistance += lastLocation.distanceTo(currentLocation);
                    }
                    lastLocation = currentLocation;

                    Intent intent = new Intent(ACTION_UPDATE_LOCATION);
                    intent.putExtra("lat", currentLatLng.latitude);
                    intent.putExtra("lng", currentLatLng.longitude);
                    intent.putExtra("totalDistance", totalDistance);
                    LocalBroadcastManager.getInstance(TrackingService.this).sendBroadcast(intent);
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tracking Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification createNotification(String contentText) {
        long millisUntilFinished = duration - elapsedMillis;
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        Intent notificationIntent;
        if ("0".equals(type)) {
            notificationIntent = new Intent(this, PhysicalTestActivity.class);
        } else {
            notificationIntent = new Intent(this, BalkeActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Fielthy Physical Test")
                .setContentText("Sisa Waktu: " + timeLeft)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void updateNotification() {
        Notification notification = createNotification("");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, notification);
        }
    }

    private void inputDataAndSubmit() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);

        String jaraktempuh = decimalFormat.format(totalDistance);

        long finalElapsedMillis = elapsedMillis;
        double waktuMenit = finalElapsedMillis / 60000.0;
        String waktu = decimalFormat.format(waktuMenit);

        if (waktuMenit <= 0) {
            Log.e("TrackingService", "Waktu tracking belum terbaca");
            stopTracking();
            return;
        }

        saveDataToDatabase(jaraktempuh, waktu);
    }

    private void saveDataToDatabase(String jaraktempuh, String waktu) {
        if (firebaseUser == null) {
            Log.e("TrackingService", "User belum login");
            stopTracking();
            return;
        }

        String collectionName = "0".equals(type) ? "6mwt" : "balke";
        DocumentReference documentReference = fStore.collection(collectionName).document();

        JSONArray jsonArray = new JSONArray();
        for (LatLng point : pathPoints) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("lat", point.latitude);
                obj.put("lng", point.longitude);
                jsonArray.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String pathPointsStr = jsonArray.toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", firebaseUser.getUid());
        hashMap.put("id", documentReference.getId());
        hashMap.put("date", formattedDate);
        hashMap.put("age", age);
        hashMap.put("gender", gender);
        hashMap.put("beratbadan", beratbadan);
        hashMap.put("tinggibadan", tinggibadan);
        hashMap.put("jaraktempuh", jaraktempuh);
        hashMap.put("waktu", waktu);
        hashMap.put("pathPoints", pathPointsStr);
        hashMap.put("type", type);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertOrUpdateRecord(DatabaseHelper.TABLE_PHYSICAL, documentReference.getId(), hashMap);

        documentReference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(TrackingService.this, HasilTestActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("age", age);
                intent.putExtra("gender", gender);
                intent.putExtra("beratbadan", beratbadan);
                intent.putExtra("tinggibadan", tinggibadan);
                intent.putExtra("jaraktempuh", jaraktempuh);
                intent.putExtra("waktu", waktu);
                intent.putExtra("pathPointsStr", pathPointsStr);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                stopTracking();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TrackingService", "Gagal menambahkan data physical", e);
                stopTracking();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Using Broadcasts instead of binding for simplicity
    }
}
