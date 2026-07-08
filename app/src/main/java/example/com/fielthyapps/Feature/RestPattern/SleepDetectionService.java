package example.com.fielthyapps.Feature.RestPattern;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.TimeUnit;

import example.com.fielthyapps.R;

public class SleepDetectionService extends Service implements SensorEventListener {
    private static final String CHANNEL_ID = "sleep_monitoring_channel";
    private static final String CONFIRM_CHANNEL_ID = "confirm_channel_high";
    private static final int NOTIFICATION_ID = 2;
    private static final int CONFIRM_NOTIFICATION_ID = 4;
    private static final String TAG = "SleepDetectionService";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    // --- PENGATURAN SENSITIVITAS ---
    private static final float ACTIVITY_THRESHOLD = 0.5f;
    private static final float WAKEUP_THRESHOLD = 5.0f;

    // PERBAIKAN 1: Dikembalikan menjadi 10 Menit (Bukan 10 Detik lagi)
    private static final long INACTIVITY_LIMIT = 10 * 60 * 1000;

    private float lastX, lastY, lastZ;
    private long lastUserActivityTime = System.currentTimeMillis();
    private long sleepStartTime = 0;
    private long screenOffTime = 0;
    private boolean isSleeping = false;
    private boolean isFirstSample = true;

    private long pendingWakeUpTime = 0;
    private int heavyMovementCount = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private SharedPreferences sleepPrefs;

    private final BroadcastReceiver interactionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // PERBAIKAN 2: Hanya Bangun jika User MEMBUKA KUNCI HP (Unlock)
            if (Intent.ACTION_USER_PRESENT.equals(action)) {
                Log.d("REST_TEST", "USER UNLOCKED PHONE");
                if (isSleeping) {
                    wakeUpDetected();
                }
                lastUserActivityTime = System.currentTimeMillis();
                saveSleepState();

                // Jika hanya layar menyala (karena notif WA/Email), JANGAN DIBANGUNKAN!
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.d("REST_TEST", "SCREEN ON RECEIVED");
                if (!isSleeping) {
                    lastUserActivityTime = System.currentTimeMillis();
                    saveSleepState();
                }

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Log.d("REST_TEST", "SCREEN OFF RECEIVED");
                screenOffTime = System.currentTimeMillis();
                saveSleepState();
            }
        }
    };

    private final Runnable monitorTask = new Runnable() {
        @Override
        public void run() {
            checkSleepStatus();
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sleepPrefs = getSharedPreferences("SleepMonitorPrefs", Context.MODE_PRIVATE);
        loadSleepState();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FielthyApps:SleepMonitorLock");
        if (!wakeLock.isHeld()) wakeLock.acquire();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(interactionReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(interactionReceiver, filter);
        }

        createNotificationChannels();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isFirstSample = true;
        loadSleepState();

        SharedPreferences prefs = getSharedPreferences("notif_prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("notif_rest", true)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        handler.removeCallbacks(monitorTask);
        handler.post(monitorTask);

        String title = isSleeping ? "Status: Sedang Beristirahat" : "Monitoring Aktif";
        String desc = isSleeping ? "Monitor pola tidur aktif." : "Sensor pola istirahat berjalan.";
        Notification notification = getPersistentNotification(title, desc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }

        return START_STICKY;
    }

    private void loadSleepState() {
        if (sleepPrefs != null) {
            isSleeping = sleepPrefs.getBoolean("IS_SLEEPING", false);
            sleepStartTime = sleepPrefs.getLong("SLEEP_START_TIME", 0);
            long savedActivity = sleepPrefs.getLong("LAST_ACTIVITY_TIME", 0);
            lastUserActivityTime = (savedActivity > 0) ? savedActivity : System.currentTimeMillis();
            screenOffTime = sleepPrefs.getLong("SCREEN_OFF_TIME", 0);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0], y = event.values[1], z = event.values[2];
        if (isFirstSample) {
            lastX = x; lastY = y; lastZ = z;
            isFirstSample = false;
            return;
        }

        boolean isVertical = Math.abs(y) > 6.0f;
        if (isVertical && !isSleeping) {
            lastUserActivityTime = System.currentTimeMillis();
        }

        float delta = Math.abs(x - lastX) + Math.abs(y - lastY) + Math.abs(z - lastZ);

        if (isSleeping) {
            // Murni mengandalkan pergerakan keras (bukan lagi karena layar terang)
            if (delta > WAKEUP_THRESHOLD) {
                if (pendingWakeUpTime == 0) {
                    pendingWakeUpTime = System.currentTimeMillis();
                    heavyMovementCount = 1;
                } else {
                    heavyMovementCount++;
                }

                if (heavyMovementCount >= 3) {
                    wakeUpDetected();
                }
            } else if (pendingWakeUpTime > 0) {
                if (System.currentTimeMillis() - pendingWakeUpTime > 60 * 1000) {
                    pendingWakeUpTime = 0;
                    heavyMovementCount = 0;
                }
            }
        } else {
            if (delta > ACTIVITY_THRESHOLD || powerManager.isInteractive()) {
                lastUserActivityTime = System.currentTimeMillis();
            }
        }

        lastX = x; lastY = y; lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void checkSleepStatus() {
        long idleDuration = System.currentTimeMillis() - lastUserActivityTime;

        if (!isSleeping && idleDuration >= INACTIVITY_LIMIT) {
            isSleeping = true;

            if (screenOffTime > 0) {
                sleepStartTime = screenOffTime;
            } else {
                sleepStartTime = lastUserActivityTime;
            }

            saveSleepState();
            updateNotification("Status: Sedang Beristirahat", "Monitor pola tidur aktif.");
            sendBroadcastToUI("START_SLEEP", 0);
        }
    }

    private void wakeUpDetected() {
        if (!isSleeping) return;

        long duration = System.currentTimeMillis() - sleepStartTime;
        isSleeping = false;
        lastUserActivityTime = System.currentTimeMillis();

        pendingWakeUpTime = 0;
        heavyMovementCount = 0;

        saveSleepState();

        if (duration >= 60 * 1000) {
            triggerConfirmation(duration, sleepStartTime, System.currentTimeMillis());
        }
        updateNotification("Monitoring Aktif", "Mendeteksi pola istirahat...");
        sendBroadcastToUI("STOP_SLEEP", duration);
    }

    private void saveSleepState() {
        if (sleepPrefs != null) {
            SharedPreferences.Editor editor = sleepPrefs.edit();
            editor.putBoolean("IS_SLEEPING", isSleeping);
            editor.putLong("SLEEP_START_TIME", sleepStartTime);
            editor.putLong("LAST_ACTIVITY_TIME", lastUserActivityTime);
            editor.putLong("SCREEN_OFF_TIME", screenOffTime);
            editor.apply();
        }
    }

    // PERBAIKAN 3: KEMBALINYA NOTIFIKASI SILUMAN UNTUK MENYELAMATKAN DATA
    private void triggerConfirmation(long duration, long start, long end) {
        SharedPreferences prefs = getSharedPreferences("notif_prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("notif_rest", true)) return;

        Intent intent = new Intent(this, SleepConfirmationActivity.class);
        intent.putExtra("duration", duration);
        intent.putExtra("start_time", start);
        intent.putExtra("end_time", end);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Notifikasi Jaring Pengaman (Hanya akan terlihat jika Pop-up diblokir Xiaomi)
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CONFIRM_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Konfirmasi Bangun")
                .setContentText("Istirahat: " + formatDuration(duration) + ". Simpan?")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(CONFIRM_NOTIFICATION_ID, builder.build());

        try {
            // Tetap paksa tembak layar Pop-up!
            startActivity(intent);
        } catch (Exception e) {
            Log.e("REST_SERVICE", "Gagal memunculkan popup: " + e.getMessage());
        }
    }

    private String formatDuration(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        if (hours > 0) return hours + " jam " + minutes + " menit";
        return minutes + " mnt";
    }

    private void updateNotification(String title, String text) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(NOTIFICATION_ID, getPersistentNotification(title, text));
    }

    private Notification getPersistentNotification(String title, String text) {
        Intent intent = new Intent(this, RestPatternActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_rest)
                .setContentIntent(pi)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void sendBroadcastToUI(String status, long duration) {
        Intent intent = new Intent("SLEEP_DETECTION_UPDATE");
        intent.putExtra("type", status);
        intent.putExtra("value", duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        saveSleepState();
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        try { unregisterReceiver(interactionReceiver); } catch (Exception ignored) {}
        if (sensorManager != null) sensorManager.unregisterListener(this);
        handler.removeCallbacks(monitorTask);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Monitoring", NotificationManager.IMPORTANCE_LOW));
                NotificationChannel confirm = new NotificationChannel(CONFIRM_CHANNEL_ID, "Konfirmasi Bangun", NotificationManager.IMPORTANCE_HIGH);
                confirm.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                confirm.setDescription("Notifikasi popup untuk konfirmasi waktu bangun tidur");
                nm.createNotificationChannel(confirm);
            }
        }
    }
}